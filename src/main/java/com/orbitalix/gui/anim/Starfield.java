package com.orbitalix.gui.anim;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public final class Starfield extends JComponent {
    private final int[] x, y, radius;
    public Starfield(int count, long seed) {
        if (count < 1) throw new IllegalArgumentException("Star count must be positive");
        Random random = new Random(seed); x = new int[count]; y = new int[count]; radius = new int[count];
        for (int i = 0; i < count; i++) { x[i] = random.nextInt(10_000); y[i] = random.nextInt(10_000); radius[i] = 1 + random.nextInt(2); }
        setOpaque(false);
    }
    @Override protected void paintComponent(Graphics g) {
        g.setColor(new Color(210, 230, 255, 130));
        for (int i = 0; i < x.length; i++) g.fillOval(x[i] * Math.max(1, getWidth()) / 10_000, y[i] * Math.max(1, getHeight()) / 10_000, radius[i], radius[i]);
    }
}
