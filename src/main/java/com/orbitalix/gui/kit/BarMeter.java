package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;

public final class BarMeter extends JComponent {
    private final Theme theme; private final String label; private double value;
    public BarMeter(String label, double value, Theme theme) { this.label = label; this.theme = theme; setValue(value); setPreferredSize(new java.awt.Dimension(180, 34)); }
    public void setValue(double value) { this.value = Math.max(0, Math.min(100, value)); repaint(); }
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create(); g.setColor(theme.foreground()); g.drawString(label, 0, 12);
        int y = 18, width = Math.max(0, getWidth() - 1); g.setColor(theme.grid()); g.fillRoundRect(0, y, width, 10, 8, 8);
        g.setColor(value < 25 ? theme.warning() : theme.accent()); g.fillRoundRect(0, y, (int)(width * value / 100), 10, 8, 8); g.dispose();
    }
}
