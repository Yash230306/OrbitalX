package com.orbitalix.gui.anim;

@FunctionalInterface
public interface Easing {
    double apply(double progress);
}
