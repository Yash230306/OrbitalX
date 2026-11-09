package com.orbitalix.crew;
import com.orbitalix.api.CrewTaskType;
public final class Commander extends CrewMember {
    public Commander(String name) { super(name); }
    @Override public String role() { return "Commander"; }
    @Override public double skillBonus(CrewTaskType task) { return task == CrewTaskType.REST ? .25 : .1; }
    @Override protected CrewMember newCopy(String name) { return new Commander(name); }
}
