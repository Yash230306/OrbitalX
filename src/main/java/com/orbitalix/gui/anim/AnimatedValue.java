package com.orbitalix.gui.anim;

public final class AnimatedValue implements Animatable {
    private final Easing easing;
    private final double duration;
    private double from, target, value, elapsed;

    public AnimatedValue(double initialValue, double durationSeconds, Easing easing) {
        if (durationSeconds <= 0 || easing == null) throw new IllegalArgumentException("Positive duration and easing are required");
        value = initialValue; from = initialValue; target = initialValue; duration = durationSeconds; this.easing = easing;
    }

    public void target(double next) { from = value; target = next; elapsed = 0; }
    public double value() { return value; }
    @Override public boolean advance(double elapsedSeconds) {
        if (elapsedSeconds < 0) throw new IllegalArgumentException("Elapsed time cannot be negative");
        if (value == target) return false;
        elapsed = Math.min(duration, elapsed + elapsedSeconds);
        double progress = easing.apply(elapsed / duration);
        value = from + (target - from) * progress;
        if (elapsed >= duration) value = target;
        return true;
    }
}
