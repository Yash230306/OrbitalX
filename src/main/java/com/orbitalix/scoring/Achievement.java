package com.orbitalix.scoring;

public interface Achievement {
    String id();
    String title();
    boolean earned(MissionMetrics metrics);
}
