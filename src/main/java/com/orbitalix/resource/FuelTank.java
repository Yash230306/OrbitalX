package com.orbitalix.resource;

/** Main propulsion tank plus an isolated reserve that requires explicit cross-feed. */
public final class FuelTank extends Resource {
    private double reserveLevel;
    private final double reserveCapacity;

    public FuelTank(double capacity, double initialLevel) { this(capacity, initialLevel, 20, 20); }
    public FuelTank(double capacity, double initialLevel, double reserveCapacity, double reserveLevel) {
        super("Fuel", "%", capacity, initialLevel);
        if (!Double.isFinite(reserveCapacity) || reserveCapacity < 0 || !Double.isFinite(reserveLevel)
                || reserveLevel < 0 || reserveLevel > reserveCapacity) throw new IllegalArgumentException("Invalid reserve tank");
        this.reserveCapacity = reserveCapacity;
        this.reserveLevel = reserveLevel;
    }

    public synchronized double reserveLevel() { return reserveLevel; }
    public synchronized void restoreReserveLevel(double value) {
        if (!Double.isFinite(value) || value < 0 || value > reserveCapacity)
            throw new IllegalArgumentException("Reserve level must be within reserve capacity");
        reserveLevel = value;
    }
    public double reserveCapacity() { return reserveCapacity; }
    public synchronized double crossFeed(double requested) {
        if (!Double.isFinite(requested) || requested < 0) throw new IllegalArgumentException("Transfer must be non-negative");
        double moved = Math.min(Math.min(requested, reserveLevel), capacity() - level());
        reserveLevel -= moved;
        refill(moved);
        return moved;
    }
    @Override public synchronized FuelTank copy() {
        return new FuelTank(capacity(), copyLevel(), reserveCapacity, reserveLevel);
    }
}
