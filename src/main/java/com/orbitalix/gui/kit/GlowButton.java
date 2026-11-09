package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GlowButton extends JButton {
    private final Theme theme;
    private final boolean primary;

    public GlowButton(String text, Theme theme, boolean primary) {
        super(text);
        this.theme = theme;
        this.primary = primary;
        setForeground(theme.foreground());
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Color base = primary ? theme.accent() : theme.panelRaised();
        if (getModel().isPressed()) base = base.darker();
        else if (getModel().isRollover()) base = base.brighter();
        g.setColor(base);
        g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
        g.setColor(primary ? theme.accent() : theme.grid());
        g.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
        g.dispose();
        super.paintComponent(graphics);
    }
}
