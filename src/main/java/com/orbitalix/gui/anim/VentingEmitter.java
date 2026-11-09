package com.orbitalix.gui.anim;

import java.awt.Color;
import java.util.Random;

public final class VentingEmitter extends Emitter {
    public VentingEmitter() { super(new Color(105, 224, 240)); }
    @Override public ParticleSystem.Particle emit(double x, double y, long seed) {
        Random r = new Random(seed); return new ParticleSystem.Particle(x, y, (r.nextDouble() - .5) * 38, -18 - r.nextDouble() * 22, 2.2, color());
    }
}
