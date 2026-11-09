package com.orbitalix.gui.kit;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

public final class Sparkline extends JComponent {
    private List<Double> values = List.of();
    private final Color color;
    public Sparkline(Color color) { this.color = color; setPreferredSize(new java.awt.Dimension(120, 32)); }
    public void setValues(List<Double> values) { this.values = List.copyOf(values); repaint(); }
    @Override protected void paintComponent(Graphics graphics) {
        if (values.size() < 2) return;
        Graphics2D g = (Graphics2D) graphics.create(); g.setColor(color);
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(min + 1);
        for (int i = 1; i < values.size(); i++) {
            int x1 = (i - 1) * Math.max(1, getWidth() - 1) / (values.size() - 1), x2 = i * Math.max(1, getWidth() - 1) / (values.size() - 1);
            int y1 = getHeight() - 2 - (int)((values.get(i - 1) - min) * Math.max(1, getHeight() - 4) / Math.max(1e-9, max - min));
            int y2 = getHeight() - 2 - (int)((values.get(i) - min) * Math.max(1, getHeight() - 4) / Math.max(1e-9, max - min));
            g.drawLine(x1, y1, x2, y2);
        }
        g.dispose();
    }
}
