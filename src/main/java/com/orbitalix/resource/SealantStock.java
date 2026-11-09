package com.orbitalix.resource;

public final class SealantStock extends Resource {
    public SealantStock(double units) { this(units, units); }
    private SealantStock(double capacity, double initial) { super("Sealant", "units", capacity, initial); }
    public boolean useOne() { return consume(1) == 1; }
    @Override public SealantStock copy() { return new SealantStock(capacity(), level()); }
}
