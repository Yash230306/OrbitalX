package com.orbitalix.gui.anim;

public final class ScreenShake implements Animatable {
    private final double magnitude, duration;
    private double elapsed;
    public ScreenShake(double magnitude, double durationSeconds) {
        if (magnitude < 0 || durationSeconds <= 0) throw new IllegalArgumentException("Invalid shake parameters");
        this.magnitude = magnitude; duration = durationSeconds;
    }
    @Override public boolean advance(double elapsedSeconds) { elapsed += elapsedSeconds; return elapsed < duration; }
    public int offsetX() { return (int)(Math.sin(elapsed * 83) * magnitude * Math.max(0, 1 - elapsed / duration)); }
    public int offsetY() { return (int)(Math.cos(elapsed * 71) * magnitude * Math.max(0, 1 - elapsed / duration)); }
}
