package com.orbitalix.gui.kit;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;

public final class Badge extends JLabel {
    public Badge(String text, Color color) {
        super(text);
        setForeground(color); setFont(getFont().deriveFont(Font.BOLD, 10f));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        setOpaque(false);
    }
}
