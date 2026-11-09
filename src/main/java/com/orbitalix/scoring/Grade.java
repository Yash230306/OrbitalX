package com.orbitalix.scoring;

public enum Grade {
    S(900), A(750), B(600), C(450), D(300), F(0);
    private final int minimumScore;
    Grade(int minimumScore) { this.minimumScore = minimumScore; }
    public int minimumScore() { return minimumScore; }
}
