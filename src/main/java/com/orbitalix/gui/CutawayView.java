package com.orbitalix.gui;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.api.SimulationController;
import com.orbitalix.api.SubsystemSnapshot;
import com.orbitalix.gui.render3d.Camera;
import com.orbitalix.gui.render3d.Renderer;
import com.orbitalix.gui.render3d.SpacecraftModel;
import com.orbitalix.gui.theme.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

/** Interactive pseudo-3D spacecraft cutaway with realistic aerospace model, HUD telemetry, and subsystem picking. */
public final class CutawayView extends MissionView {
    private final Camera camera = new Camera();
    private final Renderer renderer = new Renderer();
    private final SpacecraftModel model = new SpacecraftModel();
    private String selected = "Life Support";
    private boolean autoRotate = true;
    private int lastX, lastY;
    private boolean dragging;

    public CutawayView(SimulationController controller, Theme theme) {
        super("Cutaway", controller, theme);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) { lastX = event.getX(); lastY = event.getY(); dragging = true; }
            @Override public void mouseReleased(MouseEvent event) { dragging = false; }
            @Override public void mouseClicked(MouseEvent event) {
                if (event.getY() > getHeight() - 88 && event.getX() > getWidth() - 230) {
                    autoRotate = !autoRotate;
                } else {
                    String part = renderer.pick(event.getX(), event.getY());
                    if (part != null) selected = part;
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent event) {
                if (!dragging) return;
                camera.rotate((event.getX() - lastX) * .012, (event.getY() - lastY) * .01);
                lastX = event.getX(); lastY = event.getY(); repaint();
            }
        });
        addMouseWheelListener(event -> { camera.zoom(event.getPreciseWheelRotation() * .45); repaint(); });
    }

    public double cameraDistance() { return camera.distance(); }
    public boolean autoRotateEnabled() { return autoRotate; }

    @Override protected void drawCutaway(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 28, y = 82, boxWidth = width - 56, boxHeight = height - 112;

        // Viewport panel with dark space gradient
        g.setPaint(new GradientPaint(x, y, new Color(7, 12, 22), x, y + boxHeight, new Color(13, 22, 38)));
        g.fillRoundRect(x, y, boxWidth, boxHeight, 18, 18);
        g.setColor(theme.grid());
        g.drawRoundRect(x, y, boxWidth, boxHeight, 18, 18);

        int legendWidth = 230, modelWidth = boxWidth - legendWidth - 28;

        // Space starfield inside viewport
        drawViewportStarfield(g, x + 10, y + 10, modelWidth + 8, boxHeight - 20);

        // Render 3D realistic spacecraft model
        renderer.render(g, model.build(snapshot), camera, x + 12, y + 34, modelWidth, boxHeight - 68, selected);

        // Sci-Fi Aerospace HUD Overlays
        drawAerospaceHud(g, x + 14, y + 14, modelWidth, boxHeight - 28);

        // Subsystems divider
        g.setColor(theme.grid());
        g.drawLine(x + modelWidth + 14, y + 18, x + modelWidth + 14, y + boxHeight - 18);

        // Subsystem Telemetry List Header
        g.setColor(theme.accent());
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString("SUBSYSTEM TELEMETRY", x + modelWidth + 28, y + 32);

        int rowY = y + 58;
        for (SubsystemSnapshot subsystem : snapshot.subsystems()) {
            HealthStatus status = subsystem.status() == HealthStatus.NOMINAL && !subsystem.activeFaults().isEmpty()
                    ? HealthStatus.WARNING : subsystem.status();
            Color color = switch (status) {
                case NOMINAL -> theme.nominal();
                case WARNING -> theme.warning();
                case CRITICAL, FAILED -> theme.critical();
            };

            boolean isCurrent = subsystem.name().equalsIgnoreCase(selected);
            if (isCurrent) {
                g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 26));
                g.fillRoundRect(x + modelWidth + 20, rowY - 16, legendWidth - 10, 32, 6, 6);
                g.setColor(theme.accent());
                g.fillRoundRect(x + modelWidth + 20, rowY - 14, 3, 28, 2, 2);
            }

            g.setColor(color);
            g.fillOval(x + modelWidth + 28, rowY - 8, 8, 8);

            g.setColor(isCurrent ? theme.accent() : theme.foreground());
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g.drawString(subsystem.name().toUpperCase(Locale.ROOT), x + modelWidth + 44, rowY);

            g.setColor(theme.muted());
            g.setFont(new Font("Consolas", Font.PLAIN, 9));
            String faultTag = subsystem.activeFaults().isEmpty() ? "" : " // FAULT ACTIVE";
            g.drawString(status.name() + faultTag, x + modelWidth + 44, rowY + 12);

            rowY += 36;
        }

        // Selected Subsystem Inspector Card at bottom left
        drawSelectedInfoCard(g, x + 20, y + boxHeight - 74);

        // Auto-Rotate Button with glowing LED
        int btnW = 175, btnH = 32;
        int btnX = x + boxWidth - btnW - 14, btnY = y + boxHeight - btnH - 14;

        g.setColor(autoRotate ? new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 35) : theme.panelRaised());
        g.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
        g.setColor(autoRotate ? theme.accent() : theme.grid());
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);

        // LED Indicator Dot
        g.setColor(autoRotate ? theme.nominal() : theme.muted());
        g.fillOval(btnX + 14, btnY + 11, 10, 10);
        if (autoRotate) {
            g.setColor(new Color(theme.nominal().getRed(), theme.nominal().getGreen(), theme.nominal().getBlue(), 70));
            g.drawOval(btnX + 12, btnY + 9, 14, 14);
        }

        g.setColor(theme.foreground());
        g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g.drawString("AUTO-ROTATE: " + (autoRotate ? "ACTIVE" : "OFF"), btnX + 32, btnY + 20);
    }

    private void drawViewportStarfield(Graphics2D g, int x, int y, int w, int h) {
        // Subtle interstellar cosmic nebula gradient
        g.setPaint(new GradientPaint(x + w / 3f, y, new Color(18, 28, 55, 60), x + w, y + h, new Color(35, 15, 45, 0)));
        g.fillRect(x, y, w, h);

        // Twinkling background stars
        g.setColor(new Color(220, 235, 255, 160));
        for (int i = 0; i < 45; i++) {
            int sx = x + Math.floorMod(i * 137 + 29, Math.max(1, w));
            int sy = y + Math.floorMod(i * 89 + 17, Math.max(1, h));
            int sz = (i % 7 == 0) ? 2 : 1;
            g.fillRect(sx, sy, sz, sz);
        }
    }

    private void drawAerospaceHud(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 90));
        g.setStroke(new BasicStroke(1.2f));

        int bracketLen = 14;
        // Top-left
        g.drawLine(x, y, x + bracketLen, y);
        g.drawLine(x, y, x, y + bracketLen);
        // Top-right
        g.drawLine(x + w, y, x + w - bracketLen, y);
        g.drawLine(x + w, y, x + w, y + bracketLen);
        // Bottom-left
        g.drawLine(x, y + h, x + bracketLen, y + h);
        g.drawLine(x, y + h, x, y + h - bracketLen);
        // Bottom-right
        g.drawLine(x + w, y + h, x + w - bracketLen, y + h);
        g.drawLine(x + w, y + h, x + w, y + h - bracketLen);

        // Center reticle
        int cx = x + w / 2, cy = y + h / 2;
        g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 40));
        g.drawLine(cx - 16, cy, cx - 5, cy);
        g.drawLine(cx + 5, cy, cx + 16, cy);
        g.drawLine(cx, cy - 16, cx, cy - 5);
        g.drawLine(cx, cy + 5, cx, cy + 16);
        g.drawOval(cx - 10, cy - 10, 20, 20);

        // Telemetry coordinate badge
        g.setColor(theme.accent());
        g.setFont(new Font("Consolas", Font.BOLD, 10));
        g.drawString(String.format(Locale.ROOT, "RANGE: %.2fm  //  ORBITAL TRACKER: 3-AXIS GIMBAL", camera.distance()), x + 16, y + 22);

        // Navigation tips
        g.setColor(theme.muted());
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.drawString("CLICK & DRAG TO ORBIT  |  SCROLL WHEEL TO ZOOM  |  CLICK PART TO INSPECT", x + 16, y + h - 8);
    }

    private void drawSelectedInfoCard(Graphics2D g, int x, int y) {
        g.setColor(new Color(theme.panel().getRed(), theme.panel().getGreen(), theme.panel().getBlue(), 220));
        g.fillRoundRect(x, y, 320, 56, 8, 8);
        g.setColor(theme.grid());
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, 320, 56, 8, 8);

        g.setColor(theme.accent());
        g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g.drawString("INSPECTING: " + selected.toUpperCase(Locale.ROOT), x + 12, y + 18);

        String info = switch (selected.toLowerCase(Locale.ROOT)) {
            case "propulsion" -> "OMS Rocket Engine & Gimbal Assembly";
            case "power" -> "Twin Deployable Photovoltaic Solar Wings";
            case "thermal" -> "Dorsal/Ventral Active Radiator Panels";
            case "communication" -> "High-Gain Parabolic Deep Space Dish";
            case "attitude" -> "4-Station Reaction Control System Quads";
            case "life support" -> "Command Capsule & Service Module Fuselage";
            case "navigation" -> "Forward Star Tracker & Lidar Sensor Suite";
            case "computer" -> "Flight Guidance Core & Avionics Chassis";
            default -> "Spacecraft Component";
        };

        g.setColor(theme.foreground());
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.drawString(info, x + 12, y + 34);

        g.setColor(theme.nominal());
        g.setFont(new Font("Consolas", Font.BOLD, 9));
        g.drawString("TELEMETRY STATUS: LINK NOMINAL", x + 12, y + 47);
    }

    @Override protected void animate(double elapsedSeconds) {
        if (autoRotate) camera.rotate(elapsedSeconds * .22, 0);
    }
}
