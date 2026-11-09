package com.orbitalix.gui.kit;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;

public final class ToastLayer extends JLayeredPane {
    public ToastLayer() { setOpaque(false); }
    public void showToast(String message, Color color, int durationMillis) {
        if (durationMillis <= 0) throw new IllegalArgumentException("Toast duration must be positive");
        JLabel toast = new JLabel(message);
        toast.setForeground(Color.WHITE); toast.setFont(toast.getFont().deriveFont(Font.BOLD, 13f));
        toast.setOpaque(true); toast.setBackground(color);
        toast.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        add(toast, JLayeredPane.PALETTE_LAYER);
        int width = Math.min(Math.max(180, toast.getPreferredSize().width), Math.max(180, getWidth() - 24));
        toast.setBounds((getWidth() - width) / 2, Math.max(12, getHeight() - 72), width, toast.getPreferredSize().height);
        Timer timer = new Timer(durationMillis, event -> { remove(toast); revalidate(); repaint(); });
        timer.setRepeats(false); timer.start(); revalidate(); repaint();
    }
}
