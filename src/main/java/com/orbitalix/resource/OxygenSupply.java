package com.orbitalix.resource;

public final class OxygenSupply extends Resource {
    public OxygenSupply(double capacity, double initialLevel) { super("Oxygen", "%", capacity, initialLevel); }
    @Override public OxygenSupply copy() { return new OxygenSupply(capacity(), level()); }
}
