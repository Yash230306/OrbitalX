package com.orbitalix.scoring;

public enum Grade {
    S(900) { @Override public String description() { return "Exceptional mission performance"; } },
    A(750) { @Override public String description() { return "Mission objectives achieved"; } },
    B(600) { @Override public String description() { return "Strong mission performance"; } },
    C(450) { @Override public String description() { return "Mission remains in progress"; } },
    D(300) { @Override public String description() { return "Major mission risks remain"; } },
    F(0) { @Override public String description() { return "Mission objectives not achieved"; } };
    private final int minimumScore;
    Grade(int minimumScore) { this.minimumScore = minimumScore; }
    public int minimumScore() { return minimumScore; }
    public abstract String description();
}
