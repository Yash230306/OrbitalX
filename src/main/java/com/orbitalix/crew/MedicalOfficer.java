package com.orbitalix.crew;
import com.orbitalix.api.CrewTaskType;
public final class MedicalOfficer extends CrewMember {
    public MedicalOfficer(String name) { super(name); }
    @Override public String role() { return "Medical"; }
    @Override public double skillBonus(CrewTaskType task) { return task == CrewTaskType.MEDICAL_CARE ? .375 : .1; }
    @Override protected CrewMember newCopy(String name) { return new MedicalOfficer(name); }
}
