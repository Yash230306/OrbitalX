package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JButton;
import java.awt.Color;

public final class IconButton extends JButton {
    public IconButton(String glyph, String accessibleName, Theme theme) {
        super(glyph);
        setToolTipText(accessibleName);
        getAccessibleContext().setAccessibleName(accessibleName);
        setForeground(theme.foreground());
        setBackground(theme.panelRaised());
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(38, 34));
    }
}
