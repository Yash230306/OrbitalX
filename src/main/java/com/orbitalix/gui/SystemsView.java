package com.orbitalix.gui;

import com.orbitalix.api.DependencySnapshot;
import com.orbitalix.api.HealthStatus;
import com.orbitalix.api.SensorSnapshot;
import com.orbitalix.api.SimulationController;
import com.orbitalix.api.SubsystemSnapshot;
import com.orbitalix.gui.theme.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Locale;

/**
 * Aerospace systems bus view: Subsystem diagnostics tree, telemetry sparklines,
 * and animated cascading fault dependency matrix.
 */
public final class SystemsView extends MissionView {
    private String selectedSubsystem;
    private double pulsePhase;

    public SystemsView(SimulationController controller, Theme theme) {
        super("Systems", controller, theme);
        selectedSubsystem = snapshot.subsystems().isEmpty() ? "" : snapshot.subsystems().get(0).name();
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                int row = (event.getY() - 120) / 34;
                if (event.getX() >= 28 && event.getX() <= 365 && row >= 0 && row < snapshot.subsystems().size()) {
                    selectedSubsystem = snapshot.subsystems().get(row).name();
                    repaint();
                }
            }
        });
    }

    public String selectedSubsystem() { return selectedSubsystem; }

    @Override protected void drawSystems(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int x = 28, y = 84, treeWidth = 340, graphY = height - 210, graphHeight = 180;
        int upperHeight = graphY - y - 16;

        // --- LEFT PANEL: Subsystems List Card ---
        card(g, x, y, treeWidth, upperHeight);
        drawCardHeader(g, x, y, treeWidth, "SPACECRAFT SUBSYSTEM BUS", "[SYS-01..06]");

        int rowY = y + 52;
        for (int i = 0; i < snapshot.subsystems().size(); i++) {
            SubsystemSnapshot subsystem = snapshot.subsystems().get(i);
            int currentY = rowY + i * 34;
            boolean selected = subsystem.name().equals(selectedSubsystem);
            HealthStatus status = effectiveStatus(subsystem);
            Color statColor = statusColor(status);

            if (selected) {
                g.setPaint(new GradientPaint(x + 8, currentY - 14, theme.panelRaised(), x + treeWidth - 16, currentY + 16, theme.panel()));
                g.fill(new RoundRectangle2D.Double(x + 8, currentY - 14, treeWidth - 16, 30, 7, 7));
                g.setColor(theme.accent());
                g.fillRect(x + 8, currentY - 14, 3, 30);
                g.draw(new RoundRectangle2D.Double(x + 8, currentY - 14, treeWidth - 16, 30, 7, 7));
            }

            // Status LED with aura
            g.setColor(new Color(statColor.getRed(), statColor.getGreen(), statColor.getBlue(), 40));
            g.fillOval(x + 18, currentY - 8, 12, 12);
            g.setColor(statColor);
            g.fillOval(x + 20, currentY - 6, 8, 8);

            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g.setColor(selected ? theme.accent() : theme.foreground());
            g.drawString(subsystem.name().toUpperCase(Locale.ROOT), x + 38, currentY + 1);

            String fault = subsystem.activeFaults().isEmpty() ? "HEALTH: " + status.name() : "FAULT: " + String.join(", ", subsystem.activeFaults());
            g.setFont(new Font("Consolas", Font.PLAIN, 10));
            g.setColor(subsystem.activeFaults().isEmpty() ? theme.muted() : theme.warning());
            g.drawString(fitText(g, fault, treeWidth - 60), x + 38, currentY + 14);

            g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 50));
            g.drawLine(x + 18, currentY + 20, x + treeWidth - 18, currentY + 20);
        }

        // --- RIGHT PANEL: Detailed Subsystem Telemetry ---
        int detailX = x + treeWidth + 16, detailWidth = width - detailX - 28;
        card(g, detailX, y, detailWidth, upperHeight);
        SubsystemSnapshot detail = snapshot.subsystems().stream()
                .filter(item -> item.name().equals(selectedSubsystem)).findFirst()
                .orElse(snapshot.subsystems().isEmpty() ? null : snapshot.subsystems().get(0));

        String titleHeader = detail == null ? "SUBSYSTEM DETAIL" : detail.name().toUpperCase(Locale.ROOT) + " TELEMETRY & CHANNELS";
        drawCardHeader(g, detailX, y, detailWidth, titleHeader, "[CH-MONITOR]");

        if (detail != null) {
            HealthStatus effStat = effectiveStatus(detail);
            Color dColor = statusColor(effStat);

            // Status chip badge
            int badgeW = 100, badgeH = 22;
            g.setColor(new Color(dColor.getRed(), dColor.getGreen(), dColor.getBlue(), 35));
            g.fillRoundRect(detailX + 18, y + 42, badgeW, badgeH, 6, 6);
            g.setColor(dColor);
            g.drawRoundRect(detailX + 18, y + 42, badgeW, badgeH, 6, 6);
            g.setFont(new Font("Consolas", Font.BOLD, 10));
            g.drawString(effStat.name(), detailX + 28, y + 57);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g.setColor(theme.foreground());
            String faultStr = detail.activeFaults().isEmpty() ? "NO ACTIVE FAILURES REPORTED" : "ALERTS: " + String.join(" | ", detail.activeFaults());
            g.drawString(faultStr, detailX + 130, y + 58);

            int sensorY = y + 84;
            for (SensorSnapshot sensor : detail.sensors()) {
                g.setColor(theme.panelRaised());
                g.fillRoundRect(detailX + 18, sensorY - 8, detailWidth - 36, 56, 8, 8);
                g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 60));
                g.drawRoundRect(detailX + 18, sensorY - 8, detailWidth - 36, 56, 8, 8);

                g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g.setColor(theme.foreground());
                g.drawString(sensor.sensorId().toUpperCase(Locale.ROOT), detailX + 30, sensorY + 12);

                g.setFont(new Font("Consolas", Font.BOLD, 14));
                g.setColor(statusColor(sensor.status()));
                g.drawString(String.format(Locale.ROOT, "%.2f %s", sensor.value(), sensor.unit()), detailX + 30, sensorY + 34);

                int sparkW = Math.max(120, detailWidth - 260);
                drawSparkline(g, sensor.recent(), detailX + detailWidth - sparkW - 32, sensorY - 2, sparkW, 44,
                        statusColor(sensor.status()));
                sensorY += 66;
            }

            // Dependencies footer
            g.setFont(new Font("Consolas", Font.PLAIN, 10));
            g.setColor(theme.muted());
            List<String> deps = snapshot.dependencies().stream()
                    .filter(item -> item.from().equals(detail.name()) || item.to().equals(detail.name()))
                    .map(item -> item.from().equals(detail.name()) ? "→ " + item.to() : "← " + item.from()).distinct().toList();
            g.drawString("INTER-SUBSYSTEM COUPLING: " + (deps.isEmpty() ? "STANDALONE" : String.join("  ", deps)),
                    detailX + 20, y + upperHeight - 16);
        }

        // --- BOTTOM PANEL: Cascade Dependency Matrix ---
        card(g, x, graphY, width - 56, graphHeight);
        drawCardHeader(g, x, graphY, width - 56, "DEPENDENCY GRAPH & PROPAGATION VECTOR", "[REALTIME CASCADE]");

        List<DependencySnapshot> dependencies = snapshot.dependencies();
        int linkY = graphY + 44, linkGap = Math.max(18, (graphHeight - 56) / Math.max(1, dependencies.size()));
        for (int i = 0; i < dependencies.size(); i++) {
            DependencySnapshot dependency = dependencies.get(i);
            int currentY = linkY + i * linkGap;
            int startX = x + 24, endX = width - 82;

            g.setFont(new Font("Consolas", Font.BOLD, 11));
            g.setColor(theme.foreground());
            g.drawString(dependency.from().toUpperCase(Locale.ROOT), startX, currentY + 3);
            g.drawString(dependency.to().toUpperCase(Locale.ROOT), endX - 110, currentY + 3);

            int lineStart = startX + 130, lineEnd = endX - 130;
            g.setColor(dependency.propagating() ? theme.warning() : new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 120));
            g.setStroke(new BasicStroke(dependency.propagating() ? 2.2f : 1f));
            g.drawLine(lineStart, currentY, lineEnd, currentY);

            // Flow arrows
            g.drawString("▶", lineStart + (lineEnd - lineStart) / 2 - 4, currentY + 4);

            if (dependency.propagating()) {
                int pulseX = lineStart + (int)((lineEnd - lineStart) * pulsePhase);
                g.setColor(new Color(theme.critical().getRed(), theme.critical().getGreen(), theme.critical().getBlue(), 70));
                g.fillOval(pulseX - 8, currentY - 8, 16, 16);
                g.setColor(theme.critical());
                g.fillOval(pulseX - 4, currentY - 4, 8, 8);
            }
        }
    }

    private void drawCardHeader(Graphics2D g, int x, int y, int width, String title, String tag) {
        g.setColor(new Color(theme.panelRaised().getRed(), theme.panelRaised().getGreen(), theme.panelRaised().getBlue(), 120));
        g.fillRoundRect(x + 2, y + 2, width - 4, 30, 14, 14);
        g.setColor(theme.accent());
        g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g.drawString(title, x + 16, y + 20);
        g.setColor(theme.muted());
        g.setFont(new Font("Consolas", Font.PLAIN, 10));
        g.drawString(tag, x + width - g.getFontMetrics().stringWidth(tag) - 16, y + 20);
        g.setColor(theme.grid());
        g.drawLine(x, y + 32, x + width, y + 32);
    }

    @Override protected void animate(double elapsedSeconds) {
        pulsePhase = (pulsePhase + elapsedSeconds * .65) % 1;
    }

    private HealthStatus effectiveStatus(SubsystemSnapshot subsystem) {
        return subsystem.status() == HealthStatus.NOMINAL && !subsystem.activeFaults().isEmpty()
                ? HealthStatus.WARNING : subsystem.status();
    }

    private Color statusColor(HealthStatus status) {
        return switch (status) {
            case NOMINAL -> theme.nominal();
            case WARNING -> theme.warning();
            case CRITICAL, FAILED -> theme.critical();
        };
    }

    private void drawSparkline(Graphics2D g, List<Double> values, int x, int y, int width, int height, Color color) {
        if (values.size() < 2) return;
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(min + 1);

        g.setColor(new Color(theme.background().getRed(), theme.background().getGreen(), theme.background().getBlue(), 180));
        g.fillRoundRect(x, y, width, height, 4, 4);
        g.setColor(theme.grid());
        g.drawRoundRect(x, y, width, height, 4, 4);

        Path2D line = new Path2D.Double();
        for (int i = 0; i < values.size(); i++) {
            double px = x + i * (double) width / (values.size() - 1);
            double py = y + height - 4 - (values.get(i) - min) * (height - 8) / Math.max(1e-9, max - min);
            if (i == 0) line.moveTo(px, py); else line.lineTo(px, py);
        }

        // Fill glow under sparkline
        Path2D fill = new Path2D.Double(line);
        fill.lineTo(x + width, y + height);
        fill.lineTo(x, y + height);
        fill.closePath();
        g.setPaint(new GradientPaint(x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), 40),
                x, y + height, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)));
        g.fill(fill);

        g.setColor(color);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(line);
    }
}

