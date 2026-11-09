package com.orbitalix.crew;

import com.orbitalix.api.Copyable;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.HealthStatus;
import com.orbitalix.model.SpaceEnvironment;
import com.orbitalix.model.Spacecraft;
import com.orbitalix.model.SystemComponent;
import java.util.List;

/** Crew member base encapsulates condition, task assignment, and health response. */
public abstract class CrewMember implements SystemComponent, Copyable<CrewMember> {
    private final String name;
    private double health = 100;
    private double stress = 5;
    private double radiationDose;
    private CrewTask task;

    protected CrewMember(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Crew name is required");
        this.name = name;
    }
    protected abstract CrewMember newCopy(String name);
    public abstract String role();
    public abstract double skillBonus(CrewTaskType task);

    @Override public final String name() { return name; }
    public final synchronized double healthPercent() { return health; }
    public final synchronized double stress() { return stress; }
    public final synchronized double radiationDose() { return radiationDose; }
    public final synchronized boolean alive() { return health > 0; }
    public final synchronized CrewTask task() { return task; }
    public final synchronized int taskTicksLeft() { return task == null ? 0 : task.remaining(); }
    public final synchronized String taskName() { return task == null ? "AVAILABLE" : task.type().name(); }
    @Override public final synchronized HealthStatus health() { return health > 60 ? HealthStatus.NOMINAL : health > 25 ? HealthStatus.WARNING : health > 0 ? HealthStatus.CRITICAL : HealthStatus.FAILED; }
    @Override public final List<SystemComponent> children() { return List.of(); }

    public final synchronized void assign(CrewTaskType type, com.orbitalix.api.FaultType target) {
        if (!alive()) throw new IllegalStateException(name + " is not available");
        if (task != null) throw new IllegalStateException(name + " already has a task");
        int base = switch (type) { case EVA_REPAIR -> 10; case MEDICAL_CARE -> 8; case MANUAL_REBOOT -> 5; case REST -> 6; };
        int duration = Math.max(1, (int) Math.ceil(base * (1 - Math.min(0.5, skillBonus(type)))));
        task = new CrewTask(type, target, duration);
    }

    public final synchronized void tick(Spacecraft craft, SpaceEnvironment environment) {
        if (!alive()) return;
        if (environment.radiationStorm() && !craft.isCrewSheltered()) radiationDose += 0.2;
        boolean dangerous = craft.oxygen() < 25 || craft.co2() > 6 || craft.pressure() < 60 || radiationDose > 40;
        if (dangerous) health = Math.max(0, health - 1);
        stress = Math.max(0, Math.min(100, stress + (dangerous ? 2 : -0.5)));
        if (task != null) {
            CrewTaskType assignedType = task.type();
            if (task.tick(craft)) {
                if (assignedType == CrewTaskType.REST) stress = Math.max(0, stress - 20);
                task = null;
            }
        }
    }

    public final synchronized void heal(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Healing must be non-negative");
        health = Math.min(100, health + amount);
    }
    public final synchronized void addRadiationDose(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Dose must be non-negative");
        radiationDose += amount;
    }
    public final synchronized void reduceStress(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Stress reduction must be non-negative");
        stress = Math.max(0, stress - amount);
    }
    @Override public final synchronized CrewMember copy() {
        CrewMember result = newCopy(name);
        result.health = health;
        result.stress = stress;
        result.radiationDose = radiationDose;
        result.task = task == null ? null : task.copy();
        return result;
    }
}
