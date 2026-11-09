package com.orbitalix.gui.anim;

import java.awt.Color;
import java.util.Random;

public final class SparksEmitter extends Emitter {
    public SparksEmitter() { super(new Color(255, 202, 84)); }
    @Override public ParticleSystem.Particle emit(double x, double y, long seed) {
        Random r = new Random(seed); return new ParticleSystem.Particle(x, y, (r.nextDouble() - .5) * 100, -r.nextDouble() * 85, .55, color());
    }
}
