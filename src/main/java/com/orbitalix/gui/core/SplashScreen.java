package com.orbitalix.gui.core;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;

public final class SplashScreen extends JPanel {
    private final Theme theme;
    public SplashScreen(Theme theme) { this.theme = theme; setOpaque(true); setBackground(theme.background()); }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setPaint(new GradientPaint(0, 0, theme.background(), getWidth(), getHeight(), theme.panel()));
        g.fillRect(0, 0, getWidth(), getHeight());
        int cx = getWidth() / 2, cy = getHeight() / 2;
        g.setColor(theme.accent()); g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        String title = "ORBITALIX";
        g.drawString(title, cx - g.getFontMetrics().stringWidth(title) / 2, cy - 10);
        g.setColor(theme.muted()); g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        String subtitle = "MISSION CONTROL SYSTEMS ONLINE";
        g.drawString(subtitle, cx - g.getFontMetrics().stringWidth(subtitle) / 2, cy + 20);
        g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 80));
        g.drawOval(cx - 118, cy - 82, 236, 164);
        g.dispose();
    }
}
