package com.orbitalix.gui.anim;

public final class Spring implements Easing {
    @Override public double apply(double progress) {
        double p = Math.max(0, Math.min(1, progress));
        return 1 - Math.exp(-7 * p) * Math.cos(10 * p);
    }
}
