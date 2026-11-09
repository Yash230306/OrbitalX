package com.orbitalix.simulation;

import com.orbitalix.api.AutonomyMode;
import com.orbitalix.api.MissionConfig;
import com.orbitalix.api.LimitType;
import com.orbitalix.config.CraftType;
import com.orbitalix.config.Destination;
import com.orbitalix.config.Difficulty;
import com.orbitalix.api.FaultType;
import com.orbitalix.api.EventType;
import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.Severity;
import com.orbitalix.action.*;
import com.orbitalix.advisor.*;
import com.orbitalix.autonomy.*;
import com.orbitalix.detection.*;
import com.orbitalix.event.EventBus;
import com.orbitalix.fault.Fault;
import com.orbitalix.fault.FaultFactory;
import com.orbitalix.fault.FaultInjector;
import com.orbitalix.fault.FaultTarget;
import com.orbitalix.crew.CrewMember;
import com.orbitalix.crew.CrewRoster;
import com.orbitalix.model.SpaceEnvironment;
import com.orbitalix.model.Spacecraft;
import com.orbitalix.resource.FuelTank;
import com.orbitalix.sensor.DriftDecorator;
import com.orbitalix.sensor.GyroSensor;
import com.orbitalix.sensor.SpikeDecorator;
import com.orbitalix.sensor.TemperatureSensor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Headless regression matrix verifies seeded determinism and representative autonomy survival. */
public final class SelfTest {
    private SelfTest() {}

    public static int run() {
        int failures = 0;
        System.out.println("Scenario             Difficulty  Autonomy          Outcome                  Ticks  Fuel");
        for (int scenario = 0; scenario < 12; scenario++) {
            for (Difficulty difficulty : Difficulty.values()) {
                for (AutonomyMode autonomy : AutonomyMode.values()) {
                    SimulationEngine first = new SimulationEngine(new MissionConfig(Destination.MARS_TRANSFER,
                            CraftType.CREW_CAPSULE, difficulty, 19770318L, scenario == 11, scenario));
                    first.setAutonomyMode(autonomy);
                    for (int i = 0; i < 300 && !first.snapshot().mission().ended(); i++) first.tick();
                    SimulationEngine second = new SimulationEngine(new MissionConfig(Destination.MARS_TRANSFER,
                            CraftType.CREW_CAPSULE, difficulty, 19770318L, scenario == 11, scenario));
                    second.setAutonomyMode(autonomy);
                    for (int i = 0; i < 300 && !second.snapshot().mission().ended(); i++) second.tick();
                    boolean deterministic = first.snapshot().mission().outcome().equals(second.snapshot().mission().outcome())
                            && Math.abs(first.fuel() - second.fuel()) < 0.000001;
                    if (!deterministic) failures++;
                    if (scenario == 10 && difficulty == Difficulty.NORMAL && autonomy == AutonomyMode.FULL_AUTO
                            && !first.snapshot().mission().outcome().startsWith("SUCCESS")) failures++;
                    System.out.printf(Locale.ROOT, "%-20s %-11s %-17s %-24s %5d %5.1f%n",
                            first.scenarioNames().get(scenario), difficulty, autonomy,
                            first.snapshot().mission().outcome(), first.tickCount(), first.fuel());
                }
            }
        }
        failures += runApiChecks();
        failures += runSensorFaultResourceChecks();
        failures += runModelCrewChecks();
        failures += runDetectionEventCopyChecks();
        failures += runActionAdvisorAutonomyChecks();
        System.out.println(failures == 0 ? "SELFTEST PASS (determinism and survival checks)" : "SELFTEST FAIL: " + failures + " assertion(s)");
        return failures == 0 ? 0 : 1;
    }

    private static int runApiChecks() {
        int failures = 0;
        SimulationEngine engine = new SimulationEngine();
        engine.tick();
        double fuelBeforePreview = engine.fuel();
        int tickBeforePreview = engine.tickCount();
        engine.preview("reduce-throttle", 30);
        if (engine.tickCount() != tickBeforePreview || engine.fuel() != fuelBeforePreview) failures++;

        engine.setUplinkDelay(true);
        engine.setThrottle(60);
        if (engine.snapshot().comms().queue().size() != 1) failures++;
        for (int i = 0; i < engine.config().destination().uplinkDelay(); i++) engine.tick();
        if (engine.snapshot().throttle() != 60) failures++;

        SimulationEngine fork = engine.copy();
        engine.tick();
        fork.tick();
        if (Math.abs(engine.fuel() - fork.fuel()) > 0.000001) failures++;
        try {
            engine.snapshot().resources().clear();
            failures++;
        } catch (UnsupportedOperationException expected) {
            // Snapshot lists are deliberately immutable.
        }
        try {
            engine.setLimit(LimitType.MAX_THROTTLE, 120, false);
            failures++;
        } catch (IllegalArgumentException expected) {
            // Unsafe values require explicit override.
        }
        if (failures != 0) System.err.println("API checks failed: " + failures);
        return failures;
    }

    private static int runSensorFaultResourceChecks() {
        int failures = 0;
        GyroSensor gyro = new GyroSensor("gyro-test", 2);
        gyro.read();
        gyro.read();
        if (gyro.history().size() != 2 || gyro.copy().history().size() != 2) failures++;
        DriftDecorator drift = new DriftDecorator(gyro, 0.5);
        if (drift.measure() != 2.5 || drift.measure() != 3.0) failures++;
        SpikeDecorator spike = new SpikeDecorator(gyro, 20);
        if (spike.measure() != 22 || spike.measure() != 2) failures++;

        FuelTank tank = new FuelTank(100, 95, 20, 20);
        if (tank.crossFeed(10) != 5 || tank.level() != 100 || tank.reserveLevel() != 15) failures++;
        FuelTank tankCopy = tank.copy();
        tankCopy.consume(10);
        if (tank.level() != 100 || tankCopy.level() != 90 || tankCopy.reserveLevel() != 15) failures++;

        for (FaultType type : FaultType.values()) {
            Fault fault = FaultFactory.create(type);
            if (fault.type() != type && type != FaultType.SENSOR_DRIFT) failures++;
        }
        Map<FaultType, Integer> active = new EnumMap<>(FaultType.class);
        FaultTarget target = new FaultTarget() {
            @Override public void activateFault(FaultType type, int durationTicks) { active.put(type, durationTicks); }
            @Override public boolean isFaultActive(FaultType type) { return active.containsKey(type); }
            @Override public void clearFault(FaultType type) { active.remove(type); }
        };
        Fault finite = FaultFactory.create(FaultType.DROPOUT);
        FaultInjector injector = new FaultInjector();
        injector.inject(target, finite);
        if (!finite.isActive() || !target.isFaultActive(FaultType.DROPOUT)) failures++;
        if (failures != 0) System.err.println("Sensor/fault/resource checks failed: " + failures);
        return failures;
    }

    private static int runModelCrewChecks() {
        int failures = 0;
        Spacecraft craft = new Spacecraft(MissionConfig.defaults());
        if (craft.subsystems().size() != 8 || craft.livingCrewCount() != craft.livingCrewCapacity()) failures++;
        double initialPressure = craft.pressure();
        craft.activateFault(FaultType.HULL_BREACH, 0);
        craft.tick(SpaceEnvironment.nominal(craft.radiation(), 1));
        if (craft.pressure() >= initialPressure || craft.oxygen() >= craft.oxygenSupply().capacity()) failures++;

        craft.crewRoster().assign("Engineer", com.orbitalix.api.CrewTaskType.EVA_REPAIR,
                FaultType.HULL_BREACH, true);
        for (int i = 0; i < 8; i++) craft.tick(SpaceEnvironment.nominal(craft.radiation(), 1));
        if (craft.isFaultActive(FaultType.HULL_BREACH)) failures++;

        CrewMember original = craft.crewRoster().members().get(0);
        Spacecraft clone = craft.copy();
        CrewMember copied = clone.crewRoster().members().get(0);
        copied.reduceStress(1);
        if (original == copied || original.stress() == copied.stress()) failures++;
        if (clone.crewRoster() == craft.crewRoster() || clone.subsystems().get(0) == craft.subsystems().get(0)) failures++;

        try {
            craft.setThrottle(101);
            failures++;
        } catch (IllegalArgumentException expected) {
            // Throttle is checked against physical and configured limits.
        }
        if (failures != 0) System.err.println("Model/crew checks failed: " + failures);
        return failures;
    }

    private static int runDetectionEventCopyChecks() {
        int failures = 0;
        TemperatureSensor hot = new TemperatureSensor("engine-temperature", 105);
        List<Anomaly> thresholdFindings = new DetectionEngine(List.of(new ThresholdDetector())).scan(List.of(hot), 7);
        if (thresholdFindings.size() != 1 || thresholdFindings.get(0).severity() != Severity.CRITICAL) failures++;

        AtomicInteger sample = new AtomicInteger(90);
        TemperatureSensor rising = new TemperatureSensor("trend-test", () -> sample.getAndIncrement());
        for (int i = 0; i < 10; i++) rising.read();
        TrendPredictor.Prediction prediction = new TrendPredictor().predict(rising, 100, true, 10).orElse(null);
        if (prediction == null || prediction.ticksToThreshold() != 1) failures++;

        AtomicInteger sequence = new AtomicInteger();
        TemperatureSensor outlier = new TemperatureSensor("statistics-test",
                () -> sequence.getAndIncrement() < 8 ? sequence.get() : 100);
        for (int i = 0; i < 8; i++) outlier.read();
        List<Anomaly> statistical = new DetectionEngine(List.of(new ZScoreDetector())).scan(List.of(outlier), 9);
        if (statistical.size() != 1 || !statistical.get(0).detector().equals("Z-Score")) failures++;

        EventBus bus = new EventBus();
        List<MissionEvent> received = new ArrayList<>();
        AutoCloseable subscription = bus.subscribe(EventBus.MISSION_EVENTS, received::add);
        bus.publish(EventBus.MISSION_EVENTS, new MissionEvent(1, EventType.ANOMALY, Severity.HIGH, "test"));
        try {
            subscription.close();
        } catch (Exception failure) {
            failures++;
        }
        bus.publish(EventBus.MISSION_EVENTS, new MissionEvent(2, EventType.MISSION_END, Severity.INFO, "done"));
        if (received.size() != 1 || received.get(0).tick() != 1 || bus.subscriberCount(EventBus.MISSION_EVENTS) != 0) failures++;

        Spacecraft craft = new Spacecraft(MissionConfig.defaults());
        craft.tick(SpaceEnvironment.nominal(craft.radiation(), 1));
        Spacecraft craftCopy = craft.copy();
        if (craft.subsystems().get(0).sensors().get(0).history().size()
                != craftCopy.subsystems().get(0).sensors().get(0).history().size()) failures++;
        com.orbitalix.model.Mission mission = new com.orbitalix.model.Mission(craft, Destination.MARS_TRANSFER);
        mission.tick(SpaceEnvironment.nominal(craft.radiation(), 1));
        com.orbitalix.model.Mission missionCopy = mission.copy();
        int copiedTrailSize = missionCopy.orbit().trail().size();
        missionCopy.tick(SpaceEnvironment.nominal(craft.radiation(), 1));
        if (mission.orbit().trail().size() != copiedTrailSize || missionCopy.orbit().trail().size() != copiedTrailSize + 1) failures++;

        if (failures != 0) System.err.println("Detection/event/copy checks failed: " + failures);
        return failures;
    }

    private static int runActionAdvisorAutonomyChecks() {
        int failures = 0;
        List<CorrectiveAction> actions = List.of(new ReduceThrottleAction(30), new CrossFeedReserveFuelAction(),
                new AbortBurnAction(), new ActivateRadiatorsAction(), new RestartCoolantPumpAction(),
                new ShedNonEssentialLoadAction(), new ReorientSolarPanelsAction(), new SwitchToBackupSensorAction(),
                new SwitchToBackupAntennaAction(), new RecalibrateGyroAction(), new SwitchToBackupScrubberAction(),
                new SealBreachAction(), new EnterRadiationShelterAction(), new CourseCorrectionBurnAction(),
                new EvasiveManeuverAction(), new RebootComputerAction(), new SwitchToBackupComputerAction(),
                new SwitchToBackupEngineAction(), new RestoreSafeLimitsAction(), new EnterSafeModeAction());
        if (actions.size() != 20 || actions.stream().map(CorrectiveAction::id).distinct().count() != 20) {
            failures++;
        }

        Spacecraft craft = new Spacecraft(MissionConfig.defaults());
        craft.setThrottle(50);
        if (!new ReduceThrottleAction(30).execute(craft).successful() || craft.throttle() != 30) failures++;
        if (!new CrossFeedReserveFuelAction().execute(craft).successful() || !craft.reserveCrossFeed()) failures++;
        if (!new AbortBurnAction().execute(craft).successful() || craft.throttle() != 0) failures++;
        if (!new ActivateRadiatorsAction().execute(craft).successful()) failures++;
        craft.activateFault(FaultType.COOLANT_FAILURE, 0);
        new RestartCoolantPumpAction().execute(craft);
        if (craft.coolantRestartTicks() != 3) failures++;
        craft.activateFault(FaultType.SOLAR_FAILURE, 0);
        new ReorientSolarPanelsAction().execute(craft);
        if (!craft.solarPanelsReoriented()) failures++;
        new SwitchToBackupSensorAction().execute(craft);
        new SwitchToBackupAntennaAction().execute(craft);
        new RecalibrateGyroAction().execute(craft);
        new SwitchToBackupScrubberAction().execute(craft);
        craft.activateFault(FaultType.HULL_BREACH, 0);
        if (!new SealBreachAction().execute(craft).successful()) failures++;
        new EnterRadiationShelterAction().execute(craft);
        double fuelBeforeBurn = craft.fuel();
        if (!new CourseCorrectionBurnAction().execute(craft).successful()
                || Math.abs(craft.fuel() - (fuelBeforeBurn - 3)) > 1e-9) failures++;
        if (!new EvasiveManeuverAction().execute(craft).successful()) failures++;
        new RebootComputerAction().execute(craft);
        if (!craft.computerOffline() || craft.computerFreeze() != 3) failures++;
        new SwitchToBackupComputerAction().execute(craft);
        new SwitchToBackupEngineAction().execute(craft);
        if (!craft.backupEngineActive() || craft.computerOffline()) failures++;
        try {
            craft.limits().set(LimitType.MAX_THROTTLE, 120, true);
            craft.setThrottle(100);
            new RestoreSafeLimitsAction().execute(craft);
            if (craft.limits().overridden() || craft.throttle() > 80) failures++;
        } catch (com.orbitalix.exception.LimitOverrideException | IllegalArgumentException failure) {
            failures++;
        }
        new EnterSafeModeAction().execute(craft);
        craft.tick(SpaceEnvironment.nominal(craft.radiation(), 1));
        if (!craft.state().name().equals("SAFE_MODE")) failures++;

        Spacecraft unhealthy = new Spacecraft(MissionConfig.defaults());
        unhealthy.fuelTank().consume(50);
        unhealthy.batteryPack().consume(100);
        for (int i = 0; i < 50; i++) unhealthy.updateTemperature(120);
        unhealthy.updateCourseError(11);
        for (FaultType type : List.of(FaultType.FUEL_LEAK, FaultType.COOLANT_FAILURE, FaultType.SOLAR_FAILURE,
                FaultType.DROPOUT, FaultType.SENSOR_DRIFT, FaultType.SCRUBBER_FAILURE, FaultType.HULL_BREACH,
                FaultType.RADIATION_STORM, FaultType.NAV_DRIFT, FaultType.DEBRIS_THREAT, FaultType.COMPUTER_FAULT,
                FaultType.ENGINE_MISFIRE)) unhealthy.activateFault(type, 0);
        try {
            unhealthy.limits().set(LimitType.MAX_THROTTLE, 120, true);
        } catch (com.orbitalix.exception.LimitOverrideException failure) {
            failures++;
        }
        AdvisorEngine advisor = new AdvisorEngine();
        if (advisor.rules().size() != 15 || advisor.advise(unhealthy, List.of(), 0).size() != 15) failures++;
        List<Recommendation> ordered = advisor.advise(unhealthy, List.of(), 0);
        if (!ordered.equals(ordered.stream().sorted().toList())) failures++;

        EventBus bus = new EventBus();
        AutopilotController autopilot = new AutopilotController(bus, 2);
        Recommendation safe = new Recommendation("safe-test", "Safe action", "test", 1, 5,
                new ActivateRadiatorsAction());
        ResponseDecision safeResult = autopilot.respond(new Spacecraft(MissionConfig.defaults()), safe,
                AutonomyMode.MANUAL, true, 1);
        if (!safeResult.handled() || !safeResult.decidedBy().equals("SELF_HEALING")) failures++;
        Recommendation costly = new Recommendation("costly-test", "Costly action", "test", 1, 5,
                new CrossFeedReserveFuelAction());
        Spacecraft manualCraft = new Spacecraft(MissionConfig.defaults());
        ResponseDecision manual = autopilot.respond(manualCraft, costly, AutonomyMode.MANUAL, false, 1);
        if (!manual.needsHuman() || manualCraft.reserveCrossFeed()) failures++;
        AutopilotController timedAutopilot = new AutopilotController(bus, 2);
        ResponseDecision timed1 = timedAutopilot.respond(manualCraft, costly, AutonomyMode.TIMED_AUTOPILOT, false, 2);
        ResponseDecision timed2 = timedAutopilot.respond(manualCraft, costly, AutonomyMode.TIMED_AUTOPILOT, false, 3);
        ResponseDecision timed3 = timedAutopilot.respond(manualCraft, costly, AutonomyMode.TIMED_AUTOPILOT, false, 4);
        if (timed1.handled() || timed2.handled() || !timed3.handled() || !manualCraft.reserveCrossFeed()) failures++;

        CorrectiveAction failingSafeAction = new CorrectiveAction() {
            @Override public String id() { return "failing-safe"; }
            @Override public String description() { return "test failure"; }
            @Override public com.orbitalix.api.ActionRisk risk() { return com.orbitalix.api.ActionRisk.SAFE; }
            @Override public ActionResult execute(Spacecraft ignored) { return ActionResult.failure("simulated failure"); }
        };
        Recommendation failingSafe = new Recommendation("failing-safe", "Failed repair", "test", 1, 5, failingSafeAction);
        AutopilotController retrying = new AutopilotController(bus, 2);
        retrying.respond(new Spacecraft(MissionConfig.defaults()), failingSafe, AutonomyMode.MANUAL, true, 5);
        ResponseDecision escalation = retrying.respond(new Spacecraft(MissionConfig.defaults()), failingSafe,
                AutonomyMode.MANUAL, true, 6);
        if (!escalation.needsHuman() || !escalation.escalated()) failures++;

        Spacecraft crewCraft = new Spacecraft(MissionConfig.defaults());
        for (int i = 0; i < 5; i++) crewCraft.sealantStock().useOne();
        crewCraft.activateFault(FaultType.HULL_BREACH, 0);
        Recommendation crewAdvice = advisor.advise(crewCraft, List.of(), 0).stream()
                .filter(rec -> rec.id().equals("hull-breach")).findFirst().orElseThrow();
        ResponseDecision crewDecision = autopilot.respond(crewCraft, crewAdvice, AutonomyMode.FULL_AUTO, false, 1);
        if (!crewDecision.decidedBy().equals("CREW")
                || crewCraft.crewRoster().members().stream().noneMatch(member -> member.task() != null)) failures++;

        if (failures != 0) System.err.println("Action/advisor/autonomy checks failed: " + failures);
        return failures;
    }
}
