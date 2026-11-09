package com.orbitalix.gui.core;

import com.orbitalix.api.SimulationController;
import com.orbitalix.api.SimulationSnapshot;
import com.orbitalix.api.TickListener;
import com.orbitalix.gui.theme.Theme;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Template-method base for themed simulation views. */
public abstract class View extends JPanel implements TickListener {
    protected final SimulationController controller;
    protected final Theme theme;
    protected SimulationSnapshot snapshot;

    protected View(SimulationController controller, Theme theme) {
        this.controller = controller;
        this.theme = theme;
        snapshot = controller.snapshot();
        setOpaque(true);
        setBackground(theme.background());
        controller.addTickListener(this);
    }

    @Override public final void onTick(SimulationSnapshot value) {
        snapshot = value;
        onSnapshot(value);
        repaint();
    }

    public final void close() { controller.removeTickListener(this); }
    protected void onSnapshot(SimulationSnapshot value) {}
    protected void animate(double elapsedSeconds) {}
    protected abstract void paintView(Graphics2D graphics);

    @Override protected final void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        paintView(g);
        g.dispose();
    }
}
