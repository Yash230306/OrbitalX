package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

public final class ProgressRing extends JComponent {
    private final Theme theme; private double progress;
    public ProgressRing(Theme theme) { this.theme = theme; setPreferredSize(new java.awt.Dimension(54, 54)); }
    public void setProgress(double progress) { this.progress = Math.max(0, Math.min(1, progress)); repaint(); }
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create(); int d = Math.min(getWidth(), getHeight()) - 8;
        g.setStroke(new BasicStroke(5)); g.setColor(theme.grid()); g.drawOval(4, 4, d, d);
        g.setColor(theme.accent()); g.draw(new Arc2D.Double(4, 4, d, d, 90, -360 * progress, Arc2D.OPEN)); g.dispose();
    }
}
