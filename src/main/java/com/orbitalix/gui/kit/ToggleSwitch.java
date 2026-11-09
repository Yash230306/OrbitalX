package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JToggleButton;
import java.awt.Graphics;
import java.awt.Graphics2D;

public final class ToggleSwitch extends JToggleButton {
    private final Theme theme;

    public ToggleSwitch(String label, Theme theme) {
        super(label);
        this.theme = theme;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(theme.foreground());
        setToolTipText(label);
        setPreferredSize(new java.awt.Dimension(116, 34));
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        int y = (getHeight() - 18) / 2;
        int x = 5;
        g.setColor(isSelected() ? theme.accent() : theme.grid());
        g.fillRoundRect(x, y, 42, 18, 18, 18);
        g.setColor(theme.foreground());
        g.fillOval(isSelected() ? x + 24 : x + 2, y + 2, 14, 14);
        g.dispose();
        super.paintComponent(graphics);
    }
}
