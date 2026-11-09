package com.orbitalix.gui.anim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public final class ParticleSystem implements Animatable {
    public record Particle(double x, double y, double vx, double vy, double lifetime, Color color) {
        public Particle { if (lifetime <= 0 || color == null) throw new IllegalArgumentException("Invalid particle"); }
    }
    private static final class LiveParticle {
        double x, y, age;
        final Particle particle;
        LiveParticle(Particle particle) { this.particle = particle; x = particle.x(); y = particle.y(); }
    }
    private final List<LiveParticle> particles = new ArrayList<>();
    private long seed;

    public void emit(Emitter emitter, double x, double y, int count) {
        if (count < 0) throw new IllegalArgumentException("Particle count cannot be negative");
        for (int i = 0; i < count; i++) particles.add(new LiveParticle(emitter.emit(x, y, ++seed)));
    }
    @Override public boolean advance(double elapsedSeconds) {
        if (elapsedSeconds < 0) throw new IllegalArgumentException("Elapsed time cannot be negative");
        particles.removeIf(p -> {
            p.age += elapsedSeconds; p.x += p.particle.vx() * elapsedSeconds; p.y += p.particle.vy() * elapsedSeconds;
            return p.age >= p.particle.lifetime();
        });
        return !particles.isEmpty();
    }
    public void paint(Graphics2D g) {
        for (LiveParticle p : particles) {
            int alpha = (int)(255 * Math.max(0, 1 - p.age / p.particle.lifetime()));
            Color c = p.particle.color(); g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g.fillOval((int)p.x, (int)p.y, 4, 4);
        }
    }
    public int size() { return particles.size(); }
}
