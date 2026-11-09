package com.orbitalix.crew;

import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.SpaceEnvironment;
import com.orbitalix.model.Spacecraft;
import java.util.*;

/** CrewRoster composes crew members and provides safe task assignment and ticking. */
public final class CrewRoster implements Iterable<CrewMember>, com.orbitalix.api.Copyable<CrewRoster> {
    private final List<CrewMember> members;

    public CrewRoster(List<? extends CrewMember> members) {
        if (members == null || members.isEmpty()) throw new IllegalArgumentException("A roster needs crew members");
        this.members = new ArrayList<>(members);
    }
    public static CrewRoster forCapacity(int capacity) {
        if (capacity < 1 || capacity > 4) throw new IllegalArgumentException("Crew capacity must be 1..4");
        List<CrewMember> roster = new ArrayList<>();
        roster.add(new Commander("Commander"));
        if (capacity > 1) roster.add(new FlightEngineer("Flight Engineer"));
        if (capacity > 2) roster.add(new MedicalOfficer("Medical Officer"));
        if (capacity > 3) roster.add(new Pilot("Pilot"));
        return new CrewRoster(roster);
    }
    public synchronized List<CrewMember> members() { return List.copyOf(members); }
    public synchronized void add(CrewMember member) {
        Objects.requireNonNull(member);
        if (members.stream().anyMatch(existing -> existing.name().equalsIgnoreCase(member.name())))
            throw new IllegalArgumentException("Crew names must be unique");
        members.add(member);
    }
    public synchronized long livingCount() { return members.stream().filter(CrewMember::alive).count(); }
    public synchronized CrewMember find(String name) {
        return members.stream().filter(member -> member.name().equalsIgnoreCase(name)
                || member.role().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown crew member: " + name));
    }
    public synchronized void assign(String name, CrewTaskType type, FaultType target, boolean evaAllowed) {
        if (type == CrewTaskType.EVA_REPAIR && !evaAllowed) throw new IllegalStateException("EVA is unsafe in current conditions");
        CrewMember member = find(name);
        member.assign(type, target);
    }
    public synchronized void tick(Spacecraft spacecraft, SpaceEnvironment environment) {
        for (CrewMember member : members) member.tick(spacecraft, environment);
    }
    @Override public synchronized Iterator<CrewMember> iterator() { return members().iterator(); }
    @Override public synchronized CrewRoster copy() { return new CrewRoster(members.stream().map(CrewMember::copy).toList()); }
}
