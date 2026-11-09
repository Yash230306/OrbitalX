package com.orbitalix.gui.kit;

import com.orbitalix.gui.theme.Theme;
import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LineChart extends JComponent {
    private final Theme theme;
    private final Map<String, List<Double>> series = new LinkedHashMap<>();
    public LineChart(Theme theme) { this.theme = theme; setPreferredSize(new java.awt.Dimension(420, 180)); }
    public void setSeries(String name, List<Double> values) { series.put(name, List.copyOf(values)); repaint(); }
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create(); g.setColor(theme.grid());
        for (int i = 0; i <= 4; i++) g.drawLine(0, i * getHeight() / 4, getWidth(), i * getHeight() / 4);
        int palette = 0;
        for (List<Double> points : series.values()) {
            if (points.size() < 2) continue;
            double min = points.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max = points.stream().mapToDouble(Double::doubleValue).max().orElse(min + 1);
            g.setColor(palette++ % 2 == 0 ? theme.accent() : theme.nominal()); g.setStroke(new BasicStroke(2));
            for (int i = 1; i < points.size(); i++) {
                int x1 = (i - 1) * getWidth() / (points.size() - 1), x2 = i * getWidth() / (points.size() - 1);
                int y1 = getHeight() - 4 - (int)((points.get(i - 1) - min) * (getHeight() - 8) / Math.max(1e-9, max - min));
                int y2 = getHeight() - 4 - (int)((points.get(i) - min) * (getHeight() - 8) / Math.max(1e-9, max - min));
                g.drawLine(x1, y1, x2, y2);
            }
        }
        g.dispose();
    }
}
