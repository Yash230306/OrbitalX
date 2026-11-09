package com.orbitalix.gui;

import com.orbitalix.api.EventType;
import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.Severity;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.anim.ExhaustEmitter;
import com.orbitalix.gui.anim.ParticleSystem;
import com.orbitalix.gui.anim.ScreenShake;
import com.orbitalix.gui.anim.SparksEmitter;
import com.orbitalix.gui.anim.VentingEmitter;
import com.orbitalix.gui.anim.Vignette;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** Transparent shell layer for event-driven particles, tint, vignette, and screen shake. */
public final class MissionEffectsOverlay extends JComponent {
    private final SimulationController controller;
    private final ParticleSystem particles = new ParticleSystem();
    private final Vignette vignette = new Vignette();
    private ScreenShake shake;
    private Color tint = Color.WHITE;
    private float tintStrength;
    private float vignetteStrength;

    public MissionEffectsOverlay(SimulationController controller) {
        this.controller = controller;
        setOpaque(false);
    }

    public void trigger(MissionEvent event) {
        if (!effectsEnabled()) return;
        int centerX = Math.max(1, getWidth()) / 2, centerY = Math.max(1, getHeight()) / 2;
        switch (event.severity()) {
            case INFO -> {
                if (event.type() == EventType.ACHIEVEMENT || event.type() == EventType.MISSION_END) {
                    particles.emit(new ExhaustEmitter(), centerX, centerY, 12);
                    tint = new Color(82, 232, 161); tintStrength = .12f; vignetteStrength = .2f;
                }
            }
            case LOW, MEDIUM -> {
                particles.emit(new VentingEmitter(), centerX, centerY, 10);
                tint = new Color(255, 190, 75); tintStrength = .12f; vignetteStrength = .24f;
            }
            case HIGH -> {
                particles.emit(new SparksEmitter(), centerX, centerY, 14);
                tint = new Color(255, 124, 84); tintStrength = .2f; vignetteStrength = .38f;
                shake = new ScreenShake(4, .4);
            }
            case CRITICAL -> {
                particles.emit(new SparksEmitter(), centerX, centerY, 24);
                tint = new Color(255, 58, 72); tintStrength = .3f; vignetteStrength = .55f;
                shake = new ScreenShake(8, .65);
            }
        }
        repaint();
    }

    public void advanceFrame(double elapsedSeconds) {
        particles.advance(elapsedSeconds);
        tintStrength = Math.max(0, tintStrength - (float)(elapsedSeconds * .28));
        vignetteStrength = Math.max(0, vignetteStrength - (float)(elapsedSeconds * .42));
        if (shake != null && !shake.advance(elapsedSeconds)) shake = null;
        repaint();
    }

    public int shakeOffsetX() { return shake == null ? 0 : shake.offsetX(); }
    public int shakeOffsetY() { return shake == null ? 0 : shake.offsetY(); }
    public int particleCount() { return particles.size(); }
    public float tintStrength() { return tintStrength; }

    private boolean effectsEnabled() {
        return Boolean.parseBoolean(controller.getSetting("effects", "true"));
    }

    @Override public boolean contains(int x, int y) { return false; }

    @Override protected void paintComponent(Graphics graphics) {
        if (!effectsEnabled()) return;
        Graphics2D g = (Graphics2D)graphics.create();
        if (tintStrength > 0) {
            g.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue(),
                    Math.min(70, (int)(tintStrength * 150))));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        particles.paint(g);
        vignette.paint(g, getWidth(), getHeight(), tint, vignetteStrength);
        g.dispose();
    }
}
