package com.orbitalix.resource;

public final class Battery extends Resource {
    public Battery(double capacity, double initialLevel) { super("Battery", "%", capacity, initialLevel); }
    public double charge(double amount) { return refill(amount); }
    @Override public Battery copy() { return new Battery(capacity(), level()); }
}
