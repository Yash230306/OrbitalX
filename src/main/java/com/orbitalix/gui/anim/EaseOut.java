package com.orbitalix.gui.anim;

public final class EaseOut implements Easing {
    @Override public double apply(double progress) {
        double p = Math.max(0, Math.min(1, progress));
        return 1 - (1 - p) * (1 - p) * (1 - p);
    }
}
