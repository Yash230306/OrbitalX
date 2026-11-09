package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GlassCard extends JPanel {
    private final Theme theme;
    private final String heading;

    public GlassCard(String heading, Theme theme) {
        this.heading = heading;
        this.theme = theme;
        setOpaque(false);
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(theme.panel());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 55));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        if (!heading.isBlank()) {
            g.setColor(theme.accent());
            g.drawString(heading, 16, 23);
        }
        g.dispose();
        super.paintComponent(graphics);
    }
}
