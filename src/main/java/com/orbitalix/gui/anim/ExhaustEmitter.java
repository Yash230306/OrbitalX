package com.orbitalix.gui.anim;

import java.awt.Color;
import java.util.Random;

public final class ExhaustEmitter extends Emitter {
    public ExhaustEmitter() { super(new Color(255, 154, 72)); }
    @Override public ParticleSystem.Particle emit(double x, double y, long seed) {
        Random r = new Random(seed); return new ParticleSystem.Particle(x, y, (r.nextDouble() - .5) * 18, 30 + r.nextDouble() * 24, 1.4, color());
    }
}
