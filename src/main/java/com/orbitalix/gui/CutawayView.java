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
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Interactive pseudo-3D spacecraft cutaway with subsystem picking and health legend. */
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
        int x = 28, y = 82, boxWidth = width - 56, boxHeight = height - 112;
        g.setColor(theme.panel()); g.fillRoundRect(x, y, boxWidth, boxHeight, 18, 18);
        g.setColor(theme.grid()); g.drawRoundRect(x, y, boxWidth, boxHeight, 18, 18);
        int legendWidth = 210, modelWidth = boxWidth - legendWidth - 28;
        g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 22));
        g.fillOval(x + 24, y + 34, modelWidth - 40, boxHeight - 80);
        renderer.render(g, model.build(snapshot), camera, x + 12, y + 34, modelWidth, boxHeight - 68);
        g.setColor(theme.grid()); g.drawLine(x + modelWidth + 2, y + 18, x + modelWidth + 2, y + boxHeight - 18);
        g.setColor(theme.accent()); g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString("SUBSYSTEM HEALTH", x + modelWidth + 18, y + 32);
        int rowY = y + 58;
        for (SubsystemSnapshot subsystem : snapshot.subsystems()) {
            HealthStatus status = subsystem.status() == HealthStatus.NOMINAL && !subsystem.activeFaults().isEmpty()
                    ? HealthStatus.WARNING : subsystem.status();
            Color color = switch (status) {
                case NOMINAL -> theme.nominal();
                case WARNING -> theme.warning();
                case CRITICAL, FAILED -> theme.critical();
            };
            g.setColor(color); g.fillOval(x + modelWidth + 18, rowY - 9, 9, 9);
            g.setColor(subsystem.name().equals(selected) ? theme.accent() : theme.foreground());
            g.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g.drawString(subsystem.name().toUpperCase(java.util.Locale.ROOT), x + modelWidth + 34, rowY);
            g.setColor(theme.muted()); g.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g.drawString(status.name() + (subsystem.activeFaults().isEmpty() ? "" : " / FAULT"),
                    x + modelWidth + 34, rowY + 13);
            rowY += 39;
        }
        g.setColor(theme.foreground()); g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g.drawString("SELECTED  /  " + selected.toUpperCase(java.util.Locale.ROOT), x + 18, y + boxHeight - 24);
        g.setColor(autoRotate ? theme.accent() : theme.panelRaised());
        g.fillRoundRect(x + boxWidth - 190, y + boxHeight - 48, 170, 30, 8, 8);
        g.setColor(theme.foreground()); g.drawString("AUTO-ROTATE: " + (autoRotate ? "ON" : "OFF"),
                x + boxWidth - 174, y + boxHeight - 29);
        g.setColor(theme.muted()); g.setStroke(new BasicStroke(1));
        g.drawString("DRAG TO ROTATE  /  SCROLL TO ZOOM", x + 18, y + boxHeight - 6);
    }

    @Override protected void animate(double elapsedSeconds) {
        if (autoRotate) camera.rotate(elapsedSeconds * .22, 0);
    }
}
