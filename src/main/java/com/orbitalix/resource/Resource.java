package com.orbitalix.resource;

import com.orbitalix.api.Copyable;
import com.orbitalix.util.Identifiable;

/** Encapsulated consumable resource with guarded transfer and exhaustion estimates. */
public abstract class Resource implements Copyable<Resource>, Identifiable {
    private final String id;
    private final String unit;
    private final double capacity;
    private double level;

    protected Resource(String id, String unit, double capacity, double initialLevel) {
        if (id == null || id.isBlank() || unit == null || unit.isBlank()
                || !Double.isFinite(capacity) || capacity <= 0
                || !Double.isFinite(initialLevel) || initialLevel < 0 || initialLevel > capacity) {
            throw new IllegalArgumentException("Invalid resource definition");
        }
        this.id = id;
        this.unit = unit;
        this.capacity = capacity;
        this.level = initialLevel;
    }

    @Override public final String id() { return id; }
    public final String unit() { return unit; }
    public final double capacity() { return capacity; }
    public final synchronized double level() { return level; }
    public final synchronized double fraction() { return level / capacity; }

    public final synchronized double consume(double amount) {
        validateAmount(amount);
        double consumed = Math.min(amount, level);
        level -= consumed;
        return consumed;
    }

    public final synchronized double refill(double amount) {
        validateAmount(amount);
        double added = Math.min(amount, capacity - level);
        level += added;
        return added;
    }

    public final synchronized int ticksToEmpty(double consumptionPerTick) {
        validateAmount(consumptionPerTick);
        if (consumptionPerTick == 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(level / consumptionPerTick);
    }

    protected final synchronized double copyLevel() { return level; }

    private static void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Amount must be finite and non-negative");
    }

    @Override public abstract Resource copy();
}
