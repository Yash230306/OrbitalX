package com.orbitalix.gui;

import com.orbitalix.api.OrbitSnapshot;
import com.orbitalix.api.SimulationController;
import com.orbitalix.api.ThreatSnapshot;
import com.orbitalix.api.Vec2;
import com.orbitalix.gui.theme.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Pannable and zoomable orbit map with planned, actual, and predicted tracks. */
public final class OrbitView extends MissionView {
    private double zoom = 1;
    private double panX, panY;
    private int lastX, lastY;
    private boolean dragging;

    public OrbitView(SimulationController controller, Theme theme) {
        super("Orbit", controller, theme);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) { lastX = event.getX(); lastY = event.getY(); dragging = true; }
            @Override public void mouseReleased(MouseEvent event) { dragging = false; }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent event) {
                if (!dragging) return;
                panX += event.getX() - lastX; panY += event.getY() - lastY;
                lastX = event.getX(); lastY = event.getY(); repaint();
            }
        });
        addMouseWheelListener(event -> {
            zoom = Math.max(.65, Math.min(3.2, zoom * Math.pow(1.12, -event.getPreciseWheelRotation())));
            repaint();
        });
    }

    public double zoomLevel() { return zoom; }
    public double panX() { return panX; }
    public double panY() { return panY; }

    @Override protected void drawOrbit(Graphics2D g, int width, int height) {
        int x = 28, y = 86, boxWidth = width - 56, boxHeight = height - 120;
        g.setColor(theme.panel()); g.fillRoundRect(x, y, boxWidth, boxHeight, 18, 18);
        g.setColor(theme.grid()); g.drawRoundRect(x, y, boxWidth, boxHeight, 18, 18);
        int plotX = x + 34, plotY = y + 54, plotWidth = boxWidth - 68, plotHeight = boxHeight - 116;
        Graphics2D plot = (Graphics2D)g.create();
        plot.clipRect(plotX, plotY, plotWidth, plotHeight);
        plot.translate(plotX + plotWidth / 2d + panX, plotY + plotHeight / 2d + panY);
        plot.scale(zoom, zoom);
        plot.translate(-(plotX + plotWidth / 2d), -(plotY + plotHeight / 2d));

        OrbitSnapshot orbit = snapshot.orbit();
        double span = Math.max(1, orbit.destination().x() - orbit.earth().x());
        double verticalScale = plotHeight * .42 / Math.max(40, span * .22);
        plot.setColor(theme.grid());
        for (int i = 1; i < 5; i++) plot.drawLine(plotX, plotY + plotHeight * i / 5,
                plotX + plotWidth, plotY + plotHeight * i / 5);
        Path2D planned = path(orbit.plannedPath(), orbit, plotX, plotY, plotWidth, plotHeight, verticalScale, span);
        plot.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 125));
        plot.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{7, 6}, 0));
        plot.draw(planned);
        drawTrack(plot, orbit.trail(), orbit, plotX, plotY, plotWidth, plotHeight, verticalScale, span,
                theme.nominal(), new BasicStroke(2.6f));
        drawTrack(plot, orbit.predictedPath(), orbit, plotX, plotY, plotWidth, plotHeight, verticalScale, span,
                theme.warning(), new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1, new float[]{3, 5}, 0));
        int earthX = mapX(orbit.earth(), orbit, plotX, plotWidth, span);
        int shipX = mapX(orbit.position(), orbit, plotX, plotWidth, span);
        int destinationX = mapX(orbit.destination(), orbit, plotX, plotWidth, span);
        int earthY = mapY(orbit.earth(), plotY, plotHeight, verticalScale);
        int shipY = mapY(orbit.position(), plotY, plotHeight, verticalScale);
        int destinationY = mapY(orbit.destination(), plotY, plotHeight, verticalScale);
        plot.setColor(snapshot.comms().blackout() ? theme.critical() : theme.nominal());
        plot.setStroke(new BasicStroke(1.5f));
        plot.drawLine(earthX, earthY, shipX, shipY);
        plot.setColor(theme.nominal()); plot.fillOval(earthX - 7, earthY - 7, 14, 14);
        plot.setColor(theme.accent()); plot.fillOval(shipX - 8, shipY - 6, 18, 12);
        plot.setColor(theme.warning()); plot.fillOval(destinationX - 9, destinationY - 9, 18, 18);
        plot.setColor(theme.muted()); plot.drawString("EARTH", Math.max(plotX + 4, earthX + 10), earthY + 24);
        String destinationLabel = snapshot.config().destination().name().replace('_', ' ');
        int destinationLabelX = Math.max(plotX + 4, destinationX - plot.getFontMetrics().stringWidth(destinationLabel) - 5);
        plot.drawString(destinationLabel, destinationLabelX, destinationY + 25);
        for (ThreatSnapshot threat : snapshot.threats()) {
            int tx = mapX(threat.position(), orbit, plotX, plotWidth, span);
            int ty = mapY(threat.position(), plotY, plotHeight, verticalScale);
            plot.setColor(theme.critical()); plot.setStroke(new BasicStroke(2));
            plot.drawOval(tx - 16, ty - 16, 32, 32);
        }
        plot.dispose();

        g.setColor(theme.accent()); g.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        g.drawString("PLANNED  /  ACTUAL  /  PREDICTED", x + 18, y + 28);
        g.setColor(snapshot.comms().blackout() ? theme.critical() : theme.nominal());
        g.drawString("COMMS " + (snapshot.comms().blackout() ? "BLACKOUT" : "LINK ACTIVE")
                + "  /  " + snapshot.comms().delayTicks() + " TICKS", x + boxWidth - 206, y + 28);
        g.setColor(theme.foreground());
        g.drawString(String.format(java.util.Locale.ROOT, "DISTANCE  %.1f  /  ETA  %d TICKS  /  COURSE ERROR  %.1f°",
                snapshot.mission().distanceLeft(), snapshot.mission().etaTicks(), orbit.courseErrorDegrees()),
                x + 18, y + boxHeight - 18);
        g.setColor(theme.muted());
        g.drawString("ZOOM " + String.format(java.util.Locale.ROOT, "%.1fx", zoom) + "  ·  DRAG TO PAN  ·  SCROLL TO ZOOM",
                x + boxWidth - 275, y + boxHeight - 18);
    }

    private static Path2D path(List<Vec2> points, OrbitSnapshot orbit, int x, int y, int width, int height,
                               double verticalScale, double span) {
        Path2D path = new Path2D.Double();
        if (points.isEmpty()) {
            path.moveTo(x, y + height / 2d);
            path.curveTo(x + width * .3, y + height * .18, x + width * .7, y + height * .82, x + width, y + height / 2d);
            return path;
        }
        for (int i = 0; i < points.size(); i++) {
            Vec2 point = points.get(i);
            int px = mapX(point, orbit, x, width, span), py = mapY(point, y, height, verticalScale);
            if (i == 0) path.moveTo(px, py); else path.lineTo(px, py);
        }
        return path;
    }
    private static void drawTrack(Graphics2D g, List<Vec2> points, OrbitSnapshot orbit, int x, int y, int width,
                                  int height, double verticalScale, double span, Color color, BasicStroke stroke) {
        if (points.size() < 2) return;
        g.setColor(color); g.setStroke(stroke);
        g.draw(path(points, orbit, x, y, width, height, verticalScale, span));
    }
    private static int mapX(Vec2 point, OrbitSnapshot orbit, int x, int width, double span) {
        return x + (int)Math.round((point.x() - orbit.earth().x()) / span * width);
    }
    private static int mapY(Vec2 point, int y, int height, double verticalScale) {
        return y + height / 2 - (int)Math.round(point.y() * verticalScale);
    }
}
