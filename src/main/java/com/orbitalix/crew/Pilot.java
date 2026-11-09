package com.orbitalix.crew;
import com.orbitalix.api.CrewTaskType;
public final class Pilot extends CrewMember {
    public Pilot(String name) { super(name); }
    @Override public String role() { return "Pilot"; }
    @Override public double skillBonus(CrewTaskType task) { return task == CrewTaskType.MANUAL_REBOOT ? .2 : .1; }
    @Override protected CrewMember newCopy(String name) { return new Pilot(name); }
}
