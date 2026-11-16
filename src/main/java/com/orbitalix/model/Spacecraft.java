package com.orbitalix.model;

import com.orbitalix.api.*;
import com.orbitalix.config.CraftType;
import com.orbitalix.crew.CrewRoster;
import com.orbitalix.fault.FaultTarget;
import com.orbitalix.resource.*;
import com.orbitalix.sensor.Sensor;
import com.orbitalix.state.*;
import java.util.*;

/** Composite spacecraft owns its subsystems, consumable resources, limits, and operating state. */
public final class Spacecraft implements SystemComponent, FaultTarget, Copyable<Spacecraft> {
    private final MissionConfig config;
    private final OperatingLimits limits;
    private final FuelTank fuel;
    private final Battery battery;
    private final OxygenSupply oxygen;
    private final SealantStock sealant;
    private CrewRoster crewRoster;
    private final EnumMap<FaultType, Integer> faults = new EnumMap<>(FaultType.class);
    private final List<Subsystem> subsystems;
    private int throttle = 50;
    private double temperature = 24;
    private double pressure = 101;
    private double co2 = 0.5;
    private double radiation;
    private double courseError;
    private double signalStrength = 1;
    private double engineEfficiency = 1;
    private boolean nonEssentialSystemsOn = true;
    private boolean radiatorsActive = true;
    private boolean coolantWorking = true;
    private boolean backupAntennaActive;
    private boolean backupEngineActive;
    private boolean backupScrubberActive;
    private boolean reserveCrossFeed;
    private boolean crewSheltered;
    private boolean solarPanelsReoriented;
    private boolean backupSensorActive;
    private boolean computerOffline;
    private int computerError;
    private int computerFreeze;
    private int coolantRestartTicks;
    private SpacecraftState state = new NominalState();

    public Spacecraft(MissionConfig config) {
        this(config, new OperatingLimits(), new FuelTank(config.craftType().fuel(), config.craftType().fuel()),
                new Battery(100, 85), new OxygenSupply(config.craftType().oxygen(), config.craftType().oxygen()),
                new SealantStock(config.craftType().sealant()));
    }

    private Spacecraft(MissionConfig config, OperatingLimits limits, FuelTank fuel, Battery battery,
                       OxygenSupply oxygen, SealantStock sealant) {
        this.config = Objects.requireNonNull(config);
        this.limits = limits;
        this.fuel = fuel;
        this.battery = battery;
        this.oxygen = oxygen;
        this.sealant = sealant;
        this.radiation = config.destination().radiationBase();
        this.crewRoster = CrewRoster.forCapacity(config.craftType().crew());
        this.subsystems = new ArrayList<>(List.of(new PropulsionSubsystem(this), new PowerSubsystem(this),
                new ThermalSubsystem(this), new CommunicationSubsystem(this), new AttitudeSubsystem(this),
                new LifeSupportSubsystem(this), new NavigationSubsystem(this), new ComputerSubsystem(this)));
    }

    @Override public String name() { return "OrbitalX Spacecraft"; }
    public MissionConfig config() { return config; }
    public OperatingLimits limits() { return limits; }
    public FuelTank fuelTank() { return fuel; }
    public Battery batteryPack() { return battery; }
    public OxygenSupply oxygenSupply() { return oxygen; }
    public SealantStock sealantStock() { return sealant; }
    public synchronized int throttle() { return throttle; }
    public synchronized void setThrottle(int value) {
        if (value < 0 || value > 100) throw new IllegalArgumentException("Throttle must be 0..100");
        if (value > limits.get(LimitType.MAX_THROTTLE)) throw new IllegalArgumentException("Throttle exceeds operating limit");
        throttle = value;
    }
    public synchronized double fuel() { return fuel.level(); }
    public synchronized double battery() { return battery.level(); }
    public synchronized double oxygen() { return oxygen.level(); }
    public synchronized double temperature() { return temperature; }
    public synchronized double pressure() { return pressure; }
    public synchronized double co2() { return co2; }
    public synchronized double radiation() { return radiation; }
    public synchronized double courseError() { return courseError; }
    public synchronized double signalStrength() { return signalStrength; }
    public synchronized double engineEfficiency() { return engineEfficiency; }
    public synchronized boolean nonEssentialSystemsOn() { return nonEssentialSystemsOn; }
    public synchronized boolean radiatorsActive() { return radiatorsActive; }
    public synchronized boolean coolantWorking() { return coolantWorking; }
    public synchronized boolean backupAntennaActive() { return backupAntennaActive; }
    public synchronized boolean backupEngineActive() { return backupEngineActive; }
    public synchronized boolean backupScrubberActive() { return backupScrubberActive; }
    public synchronized boolean reserveCrossFeed() { return reserveCrossFeed; }
    public synchronized CrewRoster crewRoster() { return crewRoster; }
    public synchronized int livingCrewCount() { return (int) crewRoster.livingCount(); }
    public synchronized boolean isCrewSheltered() { return crewSheltered && pressure >= 60 && oxygen.level() > 25 && co2 <= 6; }
    public synchronized boolean computerOffline() { return computerOffline; }
    public synchronized int computerError() { return computerError; }
    public synchronized int computerFreeze() { return computerFreeze; }
    public synchronized boolean backupSensorActive() { return backupSensorActive; }
    public synchronized int coolantRestartTicks() { return coolantRestartTicks; }
    public synchronized SpacecraftState state() { return state; }
    public synchronized void setState(SpacecraftState value) { state = Objects.requireNonNull(value); }
    public synchronized List<Subsystem> subsystems() { return List.copyOf(subsystems); }
    public synchronized List<Sensor> sensors() {
        return subsystems.stream().flatMap(subsystem -> subsystem.sensors().stream()).toList();
    }
    public synchronized void sampleSensors() { sensors().forEach(Sensor::read); }

    public synchronized void applyTelemetry(SpacecraftTelemetry telemetry) {
        Objects.requireNonNull(telemetry);
        throttle = telemetry.throttle();
        fuel.restoreLevel(telemetry.fuel());
        fuel.restoreReserveLevel(telemetry.reserveFuel());
        battery.restoreLevel(telemetry.battery());
        oxygen.restoreLevel(telemetry.oxygen());
        sealant.restoreLevel(telemetry.sealant());
        temperature = telemetry.temperature();
        pressure = Math.min(101, telemetry.pressure());
        co2 = telemetry.co2();
        radiation = telemetry.radiation();
        courseError = telemetry.courseError();
        signalStrength = Math.min(1, telemetry.signalStrength());
        engineEfficiency = Math.min(1, telemetry.engineEfficiency());
        computerError = telemetry.computerError();
        computerFreeze = telemetry.computerFreeze();
        coolantRestartTicks = telemetry.coolantRestartTicks();
        computerOffline = computerFreeze > 0;
        nonEssentialSystemsOn = telemetry.nonEssentialSystemsOn();
        radiatorsActive = telemetry.radiatorsActive();
        coolantWorking = telemetry.coolantWorking();
        backupAntennaActive = telemetry.backupAntennaActive();
        backupEngineActive = telemetry.backupEngineActive();
        backupScrubberActive = telemetry.backupScrubberActive();
        reserveCrossFeed = telemetry.reserveCrossFeed();
        solarPanelsReoriented = telemetry.solarPanelsReoriented();
        crewSheltered = telemetry.crewSheltered();
        for (FaultType type : List.copyOf(faults.keySet()))
            if (!telemetry.activeFaults().contains(type)) clearFault(type);
        for (FaultType type : telemetry.activeFaults())
            if (!faults.containsKey(type)) activateFault(type, 0);
        limits.restoreSafeDefaults();
        for (Map.Entry<LimitType, Double> limit : telemetry.limits().entrySet()) {
            try {
                limits.set(limit.getKey(), limit.getValue(), telemetry.overridden());
            } catch (com.orbitalix.exception.LimitOverrideException failure) {
                throw new IllegalStateException("Could not synchronize operating limit " + limit.getKey(), failure);
            }
        }
        limits.restoreOverrideFlag(telemetry.overridden());
        state = state.transition(health());
    }

    public synchronized void tick(SpaceEnvironment environment) {
        for (Subsystem subsystem : subsystems) subsystem.update(this, environment);
        radiation = environment.radiation();
        crewRoster.tick(this, environment);
        state = state.transition(health());
        expireFaults();
    }
    public synchronized void burnFuel(double amount) {
        double consumedFromMain = fuel.consume(amount);
        double remainder = amount - consumedFromMain;
        if (remainder > 0 && reserveCrossFeed) {
            double transferred = fuel.crossFeed(remainder);
            fuel.consume(transferred);
        }
    }
    public synchronized void updateBattery(double delta) {
        if (delta >= 0) battery.refill(delta); else battery.consume(-delta);
    }
    public synchronized void updateTemperature(double target) { temperature += (target - temperature) * 0.1; }
    public synchronized void updateCourseError(double delta) { courseError = Math.min(90, courseError + delta); }
    public synchronized void recalibrateCourse() { courseError = 0; }
    public synchronized void setPressure(double value) { pressure = Math.max(0, Math.min(101, value)); }
    public synchronized void leakPressure(double amount) { pressure = Math.max(0, pressure - amount); }
    public synchronized void consumeOxygen(double amount) { oxygen.consume(amount); }
    public synchronized void addCo2(double amount) { co2 = Math.min(100, co2 + amount); }
    public synchronized void scrubCo2(double amount) { co2 = Math.max(0, co2 - amount); }
    public synchronized void setSignalStrength(double value) { signalStrength = Math.max(0, Math.min(1, value)); }
    public synchronized void setEngineEfficiency(double value) { engineEfficiency = Math.max(0, Math.min(1, value)); }
    public synchronized void setNonEssentialSystemsOn(boolean value) { nonEssentialSystemsOn = value; }
    public synchronized void setRadiatorsActive(boolean value) { radiatorsActive = value; }
    public synchronized void setCoolantWorking(boolean value) { coolantWorking = value; }
    public synchronized void setBackupAntennaActive(boolean value) { backupAntennaActive = value; }
    public synchronized void setBackupEngineActive(boolean value) { backupEngineActive = value; }
    public synchronized void setBackupScrubberActive(boolean value) { backupScrubberActive = value; }
    public synchronized void crossFeedReserveFuel() { reserveCrossFeed = true; }
    public synchronized void setCrewSheltered(boolean value) { crewSheltered = value; }
    public synchronized void setSolarPanelsReoriented(boolean value) { solarPanelsReoriented = value; }
    public synchronized boolean solarPanelsReoriented() { return solarPanelsReoriented; }
    public synchronized void addComputerError() { computerError = Math.min(100, computerError + 1); }
    public synchronized void crashComputer() { computerOffline = true; computerFreeze = Math.max(computerFreeze, 3); }
    public synchronized void advanceComputerFreeze() {
        if (computerFreeze > 0) computerFreeze--;
        if (computerFreeze == 0) computerOffline = false;
    }
    public synchronized void restartComputer() { computerError = 0; computerFreeze = 0; computerOffline = false; }
    public synchronized void rebootComputer() { computerError = 0; computerFreeze = 3; computerOffline = true; }
    public synchronized void switchToBackupComputer() { computerError = 0; computerFreeze = 0; computerOffline = false; }
    public synchronized void setBackupSensorActive(boolean value) { backupSensorActive = value; }
    public synchronized void restartCoolantPump() {
        clearFault(FaultType.COOLANT_FAILURE);
        coolantWorking = false;
        coolantRestartTicks = 3;
    }
    public synchronized void advanceCoolantRestart() {
        if (coolantRestartTicks > 0 && --coolantRestartTicks == 0) coolantWorking = true;
    }
    public synchronized boolean spendFuel(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Fuel cost must be non-negative");
        if (fuel.level() < amount) return false;
        fuel.consume(amount);
        return true;
    }
    public synchronized boolean sealBreach() {
        if (!sealant.useOne()) return false;
        pressure = Math.min(101, pressure + 18);
        clearFault(FaultType.HULL_BREACH);
        return true;
    }

    @Override public synchronized void activateFault(FaultType type, int durationTicks) {
        if (durationTicks < 0) throw new IllegalArgumentException("Fault duration cannot be negative");
        faults.put(Objects.requireNonNull(type), durationTicks);
        for (Subsystem subsystem : subsystems) if (belongs(subsystem.name(), type)) subsystem.addFault(type);
    }
    @Override public synchronized boolean isFaultActive(FaultType type) { return faults.containsKey(type); }
    @Override public synchronized void clearFault(FaultType type) {
        faults.remove(type);
        for (Subsystem subsystem : subsystems) subsystem.removeFault(type);
    }
    public synchronized Set<FaultType> activeFaults() { return Set.copyOf(faults.keySet()); }
    private void expireFaults() {
        List<FaultType> expired = new ArrayList<>();
        faults.replaceAll((type, ticks) -> {
            if (ticks > 0 && ticks - 1 == 0) expired.add(type);
            return ticks > 0 ? ticks - 1 : ticks;
        });
        expired.forEach(this::clearFault);
    }
    private static boolean belongs(String subsystem, FaultType type) {
        return switch (type) {
            case FUEL_LEAK, ENGINE_MISFIRE -> subsystem.equals("Propulsion");
            case OVERHEAT, COOLANT_FAILURE -> subsystem.equals("Thermal");
            case SOLAR_FAILURE -> subsystem.equals("Power");
            case DROPOUT -> subsystem.equals("Communication");
            case SENSOR_DRIFT, SPIKE, STUCK_VALUE -> subsystem.equals("Attitude");
            case NAV_DRIFT -> subsystem.equals("Navigation");
            case COMPUTER_FAULT -> subsystem.equals("Computer");
            case SCRUBBER_FAILURE, HULL_BREACH -> subsystem.equals("Life Support");
            default -> false;
        };
    }
    @Override public synchronized HealthStatus health() {
        List<SystemComponent> components = new ArrayList<>(subsystems);
        components.addAll(crewRoster.members());
        return SystemComponent.worstOf(components);
    }
    @Override public synchronized List<SystemComponent> children() {
        List<SystemComponent> components = new ArrayList<>(subsystems);
        components.addAll(crewRoster.members());
        return List.copyOf(components);
    }
    @Override public synchronized Spacecraft copy() {
        Spacecraft clone = new Spacecraft(config, limits.copy(), fuel.copy(), battery.copy(), oxygen.copy(), sealant.copy());
        clone.throttle = throttle; clone.temperature = temperature; clone.pressure = pressure; clone.co2 = co2;
        clone.radiation = radiation; clone.courseError = courseError; clone.signalStrength = signalStrength;
        clone.engineEfficiency = engineEfficiency; clone.nonEssentialSystemsOn = nonEssentialSystemsOn;
        clone.radiatorsActive = radiatorsActive; clone.coolantWorking = coolantWorking;
        clone.backupAntennaActive = backupAntennaActive; clone.backupEngineActive = backupEngineActive;
        clone.backupScrubberActive = backupScrubberActive; clone.reserveCrossFeed = reserveCrossFeed;
        clone.crewSheltered = crewSheltered; clone.computerOffline = computerOffline;
        clone.solarPanelsReoriented = solarPanelsReoriented;
        clone.backupSensorActive = backupSensorActive;
        clone.coolantRestartTicks = coolantRestartTicks;
        clone.computerError = computerError; clone.computerFreeze = computerFreeze;
        clone.crewRoster = crewRoster.copy();
        clone.state = state.copy();
        clone.faults.putAll(faults);
        for (int i = 0; i < subsystems.size(); i++) clone.subsystems.set(i, subsystems.get(i).copy(clone));
        return clone;
    }
    public synchronized int livingCrewCapacity() { return config.craftType().crew(); }
}
