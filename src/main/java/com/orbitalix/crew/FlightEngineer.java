package com.orbitalix.crew;
import com.orbitalix.api.CrewTaskType;
public final class FlightEngineer extends CrewMember {
    public FlightEngineer(String name) { super(name); }
    @Override public String role() { return "Engineer"; }
    @Override public double skillBonus(CrewTaskType task) { return task == CrewTaskType.EVA_REPAIR ? .4 : .1; }
    @Override protected CrewMember newCopy(String name) { return new FlightEngineer(name); }
}
