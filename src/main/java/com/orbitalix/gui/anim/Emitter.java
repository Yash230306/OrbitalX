package com.orbitalix.gui.anim;

import java.awt.Color;

public abstract class Emitter {
    private final Color color;
    protected Emitter(Color color) { this.color = color; }
    public Color color() { return color; }
    public abstract ParticleSystem.Particle emit(double x, double y, long seed);
}
