package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.config.Destination;
import java.util.Objects;

/** Mission aggregate advances distance using propulsion, course error, and craft-specific efficiency. */
public final class Mission {
    private final Spacecraft spacecraft;
    private final Destination destination;
    private OrbitModel orbit;
    private double progress;
    private int tick;
    private boolean ended;
    private String outcome = "IN PROGRESS";

    public Mission(Spacecraft spacecraft, Destination destination) {
        this.spacecraft = Objects.requireNonNull(spacecraft);
        this.destination = Objects.requireNonNull(destination);
        this.orbit = new OrbitModel(destination.distance());
    }
    public synchronized void tick(SpaceEnvironment environment) {
        if (ended) return;
        tick++;
        spacecraft.tick(environment);
        double courseFactor = Math.max(0.3, 1 - spacecraft.courseError() / 30);
        progress += spacecraft.throttle() * 0.02 * spacecraft.engineEfficiency() * courseFactor
                * spacecraft.config().craftType().progressFactor();
        orbit.record(progress, spacecraft.courseError());
        if (spacecraft.fuel() <= 0 && (!spacecraft.reserveCrossFeed() || spacecraft.fuelTank().reserveLevel() <= 0)) {
            end("LOST - FUEL EXHAUSTED");
        } else if (spacecraft.oxygen() <= 0) {
            end("LOST - OXYGEN EXHAUSTED");
        } else if (spacecraft.livingCrewCount() == 0) {
            end("LOST - CREW LOST");
        } else if (progress >= destination.distance()) {
            end("SUCCESS - DESTINATION REACHED");
        } else if (spacecraft.isFaultActive(FaultType.COMPUTER_FAULT) && spacecraft.health().name().equals("FAILED")) {
            end("LOST - UNRECOVERABLE COMPUTER FAILURE");
        }
    }
    private void end(String result) { ended = true; outcome = result; }
    public synchronized double progress() { return progress; }
    public synchronized double progressPercent() { return Math.min(100, progress / destination.distance() * 100); }
    public synchronized double distanceLeft() { return Math.max(0, destination.distance() - progress); }
    public synchronized int tickCount() { return tick; }
    public synchronized boolean ended() { return ended; }
    public synchronized String outcome() { return outcome; }
    public synchronized Spacecraft spacecraft() { return spacecraft; }
    public synchronized OrbitModel orbit() { return orbit; }
    public synchronized void synchronizeState(double progress, int tick, boolean ended, String outcome, double courseError) {
        if (!Double.isFinite(progress) || progress < 0 || tick < this.tick || outcome == null)
            throw new IllegalArgumentException("Invalid synchronized mission state");
        if (tick > this.tick) orbit.record(progress, courseError);
        this.progress = progress;
        this.tick = tick;
        this.ended = ended;
        this.outcome = outcome;
    }
    public synchronized Mission copy() {
        Mission result = new Mission(spacecraft.copy(), destination);
        result.progress = progress; result.tick = tick; result.ended = ended; result.outcome = outcome;
        result.orbit = orbit.copy();
        return result;
    }
}
