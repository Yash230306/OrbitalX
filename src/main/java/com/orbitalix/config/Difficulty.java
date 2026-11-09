package com.orbitalix.config;

public enum Difficulty {
    EASY(0.5, 0.9, 1.5, false) {
        @Override public String guidance() { return "Wide response window"; }
    },
    NORMAL(1, 1, 1, false) {
        @Override public String guidance() { return "Standard operating conditions"; }
    },
    HARD(1.6, 1.1, 0.6, true) {
        @Override public String guidance() { return "Short response window; autonomy recommended"; }
    };

    private final double faultMultiplier;
    private final double burnMultiplier;
    private final double responseWindow;
    private final boolean uplinkByDefault;

    Difficulty(double faultMultiplier, double burnMultiplier, double responseWindow, boolean uplinkByDefault) {
        this.faultMultiplier = faultMultiplier;
        this.burnMultiplier = burnMultiplier;
        this.responseWindow = responseWindow;
        this.uplinkByDefault = uplinkByDefault;
    }

    public double faultMultiplier() { return faultMultiplier; }
    public double burnMultiplier() { return burnMultiplier; }
    public double responseWindow() { return responseWindow; }
    public boolean uplinkByDefault() { return uplinkByDefault; }
    public abstract String guidance();
}
