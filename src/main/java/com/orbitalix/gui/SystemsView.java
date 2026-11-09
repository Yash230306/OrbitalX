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
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;

/** Selectable subsystem tree, sensor sparklines, and animated dependency links. */
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
        int x = 28, y = 86, treeWidth = 330, graphY = height - 220, graphHeight = 192;
        int upperHeight = graphY - y - 14;
        card(g, x, y, treeWidth, upperHeight);
        label(g, "SPACECRAFT / SUBSYSTEMS", x + 16, y + 26, theme.accent());
        int rowY = y + 52;
        for (int i = 0; i < snapshot.subsystems().size(); i++) {
            SubsystemSnapshot subsystem = snapshot.subsystems().get(i);
            int currentY = rowY + i * 34;
            boolean selected = subsystem.name().equals(selectedSubsystem);
            if (selected) {
                g.setColor(theme.panelRaised()); g.fillRoundRect(x + 9, currentY - 18, treeWidth - 18, 30, 7, 7);
            }
            HealthStatus status = effectiveStatus(subsystem);
            g.setColor(statusColor(status)); g.fillOval(x + 18, currentY - 10, 9, 9);
            label(g, subsystem.name(), x + 36, currentY, selected ? theme.accent() : theme.foreground());
            String fault = subsystem.activeFaults().isEmpty() ? status.name() : String.join(", ", subsystem.activeFaults());
            small(g, fitText(g, fault, treeWidth - 76), x + 36, currentY + 13);
            g.setColor(theme.grid()); g.drawLine(x + 18, currentY + 22, x + treeWidth - 18, currentY + 22);
        }

        int detailX = x + treeWidth + 16, detailWidth = width - detailX - 28;
        card(g, detailX, y, detailWidth, upperHeight);
        SubsystemSnapshot detail = snapshot.subsystems().stream()
                .filter(item -> item.name().equals(selectedSubsystem)).findFirst()
                .orElse(snapshot.subsystems().isEmpty() ? null : snapshot.subsystems().get(0));
        label(g, detail == null ? "SUBSYSTEM DETAIL" : detail.name().toUpperCase(Locale.ROOT) + " TELEMETRY",
                detailX + 18, y + 26, theme.accent());
        if (detail != null) {
            label(g, "STATUS  /  " + effectiveStatus(detail), detailX + 18, y + 54, statusColor(effectiveStatus(detail)));
            small(g, detail.activeFaults().isEmpty() ? "ACTIVE FAULTS  /  NONE" : "ACTIVE FAULTS  /  " + String.join(", ", detail.activeFaults()),
                    detailX + 18, y + 76);
            int sensorY = y + 112;
            for (SensorSnapshot sensor : detail.sensors()) {
                label(g, sensor.sensorId().toUpperCase(Locale.ROOT), detailX + 18, sensorY, theme.foreground());
                small(g, String.format(Locale.ROOT, "%.2f %s", sensor.value(), sensor.unit()), detailX + 18, sensorY + 18);
                drawSparkline(g, sensor.recent(), detailX + 190, sensorY - 10, Math.max(100, detailWidth - 220), 34,
                        statusColor(sensor.status()));
                sensorY += 64;
            }
            small(g, "DEPENDENCIES  /  " + snapshot.dependencies().stream()
                    .filter(item -> item.from().equals(detail.name()) || item.to().equals(detail.name()))
                    .map(item -> item.from().equals(detail.name()) ? item.to() : item.from()).distinct().toList(),
                    detailX + 18, y + upperHeight - 22);
        }

        card(g, x, graphY, width - 56, graphHeight);
        label(g, "DEPENDENCY GRAPH  /  CASCADE FLOW", x + 16, graphY + 24, theme.accent());
        List<DependencySnapshot> dependencies = snapshot.dependencies();
        int linkY = graphY + 48, linkGap = Math.max(18, (graphHeight - 62) / Math.max(1, dependencies.size()));
        for (int i = 0; i < dependencies.size(); i++) {
            DependencySnapshot dependency = dependencies.get(i);
            int currentY = linkY + i * linkGap;
            int startX = x + 20, endX = width - 78;
            g.setColor(dependency.propagating() ? theme.warning() : theme.grid());
            g.setStroke(new BasicStroke(dependency.propagating() ? 2 : 1));
            g.drawLine(startX + 110, currentY - 4, endX - 110, currentY - 4);
            label(g, dependency.from(), startX, currentY, theme.foreground());
            label(g, dependency.to(), endX - 94, currentY, theme.foreground());
            if (dependency.propagating()) {
                int pulseX = startX + 110 + (int)((endX - startX - 220) * pulsePhase);
                g.setColor(theme.critical()); g.fillOval(pulseX - 5, currentY - 9, 10, 10);
            }
        }
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
        Path2D line = new Path2D.Double();
        for (int i = 0; i < values.size(); i++) {
            double px = x + i * width / (double)(values.size() - 1);
            double py = y + height - 3 - (values.get(i) - min) * (height - 6) / Math.max(1e-9, max - min);
            if (i == 0) line.moveTo(px, py); else line.lineTo(px, py);
        }
        g.setColor(theme.grid()); g.drawRect(x, y, width, height);
        g.setColor(color); g.setStroke(new BasicStroke(2)); g.draw(line);
    }
}
