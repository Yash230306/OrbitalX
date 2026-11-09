package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

public final class ArcGauge extends JComponent {
    private final Theme theme;
    private final String label;
    private double value, maximum, marker;

    public ArcGauge(String label, double value, double maximum, double marker, Theme theme) {
        this.label = label; this.theme = theme; setValue(value, maximum, marker);
        setPreferredSize(new java.awt.Dimension(136, 136));
        setToolTipText(label);
    }
    public void setValue(double value, double maximum, double marker) {
        if (maximum <= 0) throw new IllegalArgumentException("Gauge maximum must be positive");
        this.value = Math.max(0, Math.min(value, maximum)); this.maximum = maximum; this.marker = marker; repaint();
    }
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        int size = Math.min(getWidth(), getHeight()) - 20, x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
        g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(theme.grid()); g.draw(new Arc2D.Double(x, y, size, size, 135, 270, Arc2D.OPEN));
        g.setColor(value / maximum < .2 ? theme.warning() : theme.accent());
        g.draw(new Arc2D.Double(x, y, size, size, 135, 270 * value / maximum, Arc2D.OPEN));
        int mx = getWidth() / 2 + (int)(Math.cos(Math.toRadians(135 - 270 * Math.max(0, Math.min(1, marker / maximum))) * (size * .43)));
        int my = getHeight() / 2 - (int)(Math.sin(Math.toRadians(135 - 270 * Math.max(0, Math.min(1, marker / maximum))) * (size * .43)));
        g.setColor(Color.WHITE); g.fillOval(mx - 3, my - 3, 6, 6);
        g.setColor(theme.foreground()); g.drawString(label, getWidth() / 2 - g.getFontMetrics().stringWidth(label) / 2, getHeight() / 2 + 5);
        g.drawString(String.format(java.util.Locale.ROOT, "%.0f%%", value * 100 / maximum), getWidth() / 2 - 14, getHeight() / 2 + 23);
        g.dispose();
    }
}
