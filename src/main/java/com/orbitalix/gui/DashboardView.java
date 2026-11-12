package com.orbitalix.gui;

import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.LimitSnapshot;
import com.orbitalix.api.LimitType;
import com.orbitalix.api.RecommendationSnapshot;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * High-performance Object-Oriented Mission Control Dashboard.
 * Encapsulates dynamic telemetry rendering, glowing HUD instruments, interactive thrust control,
 * predictive what-if previews, master caution annunciators, and operating limit overrides.
 */
public final class DashboardView extends MissionView {

    public DashboardView(SimulationController controller, Theme theme) {
        super("Dashboard", controller, theme);
    }

    @Override
    public String previewMessage() {
        return previewMessage;
    }

    @Override
    protected void paintView(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int width = getWidth(), height = getHeight();
        drawCockpitBackdrop(g, width, height);

        DashboardLayout layout = dashboardLayout(width, height);
        drawHeader(g, width);
        drawGaugesGrid(g, layout);
        drawThrustLever(g, layout);
        drawAdvisorSection(g, layout);
        drawProgressAndWhatIf(g, layout, height);
        drawLimitControls(g, layout, height);

        if (annunciatorExpanded) {
            drawAnnunciator(g, layout, height);
        }

        drawStatusFooter(g, height);
    }

    /** Renders the deep-space telemetry backdrop with HUD corner reticles. */
    private void drawCockpitBackdrop(Graphics2D g, int width, int height) {
        g.setPaint(new GradientPaint(0, 0, theme.background(), width, height, theme.panel()));
        g.fillRect(0, 0, width, height);

        // Ambient starlight grid
        Color starColor = new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 26);
        g.setColor(starColor);
        for (int i = 0; i < 60; i++) {
            int x = Math.floorMod(i * 193 + 37, Math.max(1, width));
            int y = Math.floorMod(i * 97 + 17, Math.max(1, height));
            g.fillOval(x, y, i % 5 == 0 ? 3 : (i % 2 == 0 ? 2 : 1), i % 5 == 0 ? 3 : (i % 2 == 0 ? 2 : 1));
        }

        // Aerospace HUD brackets on the outer canvas
        g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 80));
        g.setStroke(new BasicStroke(1.5f));
        int bSize = 14;
        // Top-left bracket
        g.drawLine(14, 14, 14 + bSize, 14);
        g.drawLine(14, 14, 14, 14 + bSize);
        // Top-right bracket
        g.drawLine(width - 14, 14, width - 14 - bSize, 14);
        g.drawLine(width - 14, 14, width - 14, 14 + bSize);
        // Bottom-left bracket
        g.drawLine(14, height - 14, 14 + bSize, height - 14);
        g.drawLine(14, height - 14, 14, height - 14 - bSize);
        // Bottom-right bracket
        g.drawLine(width - 14, height - 14, width - 14 - bSize, height - 14);
        g.drawLine(width - 14, height - 14, width - 14, height - 14 - bSize);
    }

    /** Top HUD header banner with pulse beacon and telemetry readout. */
    private void drawHeader(Graphics2D g, int width) {
        g.setColor(theme.foreground());
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("MISSION CONTROL / TELEMETRY CONSOLE", 28, 38);

        // Glowing live pulse beacon
        boolean pulseOn = (System.currentTimeMillis() / 450) % 2 == 0;
        Color beaconColor = snapshot.spacecraftState().equals("CRITICAL")
                ? theme.critical()
                : (snapshot.spacecraftState().equals("WARNING") ? theme.warning() : theme.nominal());
        g.setColor(beaconColor);
        g.fillOval(30, 52, 7, 7);
        if (pulseOn) {
            g.setColor(new Color(beaconColor.getRed(), beaconColor.getGreen(), beaconColor.getBlue(), 90));
            g.drawOval(28, 50, 11, 11);
        }

        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(theme.muted());
        String tickStr = String.format(Locale.ROOT, "%03d", snapshot.tick());
        String missionStatus = "T+" + tickStr + (snapshot.replaying() ? "  /  REPLAY ACTIVE" : "  /  LIVE FLIGHT STREAM")
                + "  ·  DEST: " + snapshot.config().destination().name().replace('_', ' ');
        g.drawString(missionStatus, 44, 59);

        // State pill badge at the top right
        String stateText = snapshot.spacecraftState();
        int stateW = g.getFontMetrics().stringWidth(stateText) + 24;
        int stateX = width - stateW - 32;
        g.setColor(new Color(beaconColor.getRed(), beaconColor.getGreen(), beaconColor.getBlue(), 35));
        g.fillRoundRect(stateX, 24, stateW, 24, 12, 12);
        g.setColor(beaconColor);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(stateX, 24, stateW, 24, 12, 12);
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.drawString(stateText, stateX + 12, 40);
    }

    /** Renders the 2x3 grid of primary telemetry instruments. */
    private void drawGaugesGrid(Graphics2D g, DashboardLayout layout) {
        List<DashboardGauge> gauges = dashboardGauges();
        int gap = layout.gap();
        int gaugeW = (layout.cardsW() - gap) / 2;
        int gaugeH = layout.gaugeH();

        for (int i = 0; i < Math.min(gauges.size(), 6); i++) {
            int col = i % 2, row = i / 2;
            int x = layout.cardsX() + col * (gaugeW + gap);
            int y = layout.top() + row * (gaugeH + gap);

            // Elevated card container
            card(g, x, y, gaugeW, gaugeH);

            DashboardGauge gauge = gauges.get(i);
            int gaugeRadius = Math.min(62, Math.max(34, (gaugeW - 150) / 2));
            int cx = x + gaugeRadius + 16;
            int cy = y + gaugeH / 2 + 5;

            // Gauge instrument
            boolean warningLevel = gauge.value() < gauge.maximum() * 0.25;
            Color gaugeColor = warningLevel ? theme.warning() : theme.accent();
            if (gauge.name().equalsIgnoreCase("Temperature") && gauge.value() > 85) gaugeColor = theme.critical();
            if (gauge.name().equalsIgnoreCase("Cabin Pressure") && gauge.value() < 80) gaugeColor = theme.critical();

            drawCyberArcGauge(g, cx, cy, gaugeRadius, gauge.name(), gauge.value(), gauge.maximum(), gaugeColor);

            // Metric digital readout
            int textX = x + gaugeRadius * 2 + 36;
            g.setFont(new Font("Segoe UI", Font.BOLD, 19));
            g.setColor(theme.foreground());
            g.drawString(number(gauge.value()) + " " + gauge.unit(), textX, cy - 3);

            // Status chip badge
            String statusNote = gauge.ticksToEmpty() >= 0
                    ? "EST. " + gauge.ticksToEmpty() + "T"
                    : (warningLevel ? "CAUTION" : "NOMINAL");
            g.setFont(new Font("Segoe UI", Font.BOLD, 10));
            Color badgeTone = warningLevel ? theme.warning() : theme.nominal();
            g.setColor(new Color(badgeTone.getRed(), badgeTone.getGreen(), badgeTone.getBlue(), 28));
            g.fillRoundRect(textX, cy + 9, 82, 18, 6, 6);
            g.setColor(badgeTone);
            g.drawString(statusNote, textX + 8, cy + 22);
        }
    }

    /** High-precision anti-aliased circular instrument with glow and tick rings. */
    private void drawCyberArcGauge(Graphics2D g, int cx, int cy, int radius, String label, double value, double max, Color color) {
        int d = radius * 2;
        double ratio = Math.max(0, Math.min(1.0, max <= 0 ? 0 : value / max));

        // Outer tick marks ring
        g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 120));
        g.setStroke(new BasicStroke(1f));
        for (int angle = 135; angle >= -135; angle -= 30) {
            double rad = Math.toRadians(angle);
            int tx1 = cx + (int) (Math.cos(rad) * (radius + 4));
            int ty1 = cy - (int) (Math.sin(rad) * (radius + 4));
            int tx2 = cx + (int) (Math.cos(rad) * (radius + 7));
            int ty2 = cy - (int) (Math.sin(rad) * (radius + 7));
            g.drawLine(tx1, ty1, tx2, ty2);
        }

        // Background track arc
        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(theme.grid());
        g.draw(new Arc2D.Double(cx - radius, cy - radius, d, d, 135, 270, Arc2D.OPEN));

        // Outer glow layer
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        g.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Double(cx - radius, cy - radius, d, d, 135, 270 * ratio, Arc2D.OPEN));

        // Crisp core active arc
        g.setColor(color);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Double(cx - radius, cy - radius, d, d, 135, 270 * ratio, Arc2D.OPEN));

        // Center needle / pip marker
        double tipAngle = Math.toRadians(135 - 270 * ratio);
        int px = cx + (int) (Math.cos(tipAngle) * (radius * 0.75));
        int py = cy - (int) (Math.sin(tipAngle) * (radius * 0.75));
        g.setColor(Color.WHITE);
        g.fillOval(px - 3, py - 3, 6, 6);

        // Label below gauge
        g.setColor(theme.muted());
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        String title = label.toUpperCase(Locale.ROOT);
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cx - titleW / 2, cy + radius + 17);
    }

    /** Interactive vertical thrust lever (throttle slider). Preserves hit target (40, 390). */
    private void drawThrustLever(Graphics2D g, DashboardLayout layout) {
        int x = layout.pad() + 18, top = layout.top() + 48, bottom = getHeight() - 78;
        int trackHeight = Math.max(90, bottom - top);

        // Metallic outer track bezel
        g.setColor(theme.panelRaised());
        g.fillRoundRect(x - 3, top - 4, 18, trackHeight + 8, 14, 14);
        g.setColor(theme.grid());
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x - 3, top - 4, 18, trackHeight + 8, 14, 14);

        int maxThrottle = (int) Math.round(snapshot.limits().stream()
                .filter(limit -> limit.type() == LimitType.MAX_THROTTLE).findFirst().orElseThrow().value());
        int sliderPercent = (int) Math.round(100d * snapshot.throttle() / Math.max(1, maxThrottle));
        int fillHeight = trackHeight * sliderPercent / 100;
        int fillY = top + trackHeight - fillHeight;

        // Glowing neon thrust column
        g.setColor(theme.accent());
        g.fillRoundRect(x, fillY, 12, fillHeight, 10, 10);

        // Segmented ladder LED marks along the track
        g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 160));
        g.setStroke(new BasicStroke(1));
        for (int p = 10; p <= 90; p += 10) {
            int tickY = top + trackHeight * (100 - p) / 100;
            g.drawLine(x - 10, tickY, x - 5, tickY);
        }

        // Slider thumb knob
        int thumbY = top + trackHeight * (100 - sliderPercent) / 100;
        g.setColor(theme.panelRaised());
        g.fillRoundRect(x - 7, thumbY - 6, 26, 16, 6, 6);
        g.setColor(theme.accent());
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x - 7, thumbY - 6, 26, 16, 6, 6);
        g.setColor(Color.WHITE);
        g.fillOval(x + 4, thumbY, 4, 4);

        // Descriptive annotations
        label(g, "THRUST", layout.pad() + 5, top - 14, theme.muted());
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(theme.foreground());
        g.drawString(snapshot.throttle() + "%", layout.pad() + 4, bottom + 18);
        small(g, "DRAG / SET", layout.pad() - 2, bottom + 32);
    }

    /** Mission advisor panel with priority ranking and action buttons. */
    private void drawAdvisorSection(Graphics2D g, DashboardLayout layout) {
        int sideX = layout.sideX(), top = layout.top(), sideW = layout.sideW();
        card(g, sideX, top, sideW, layout.advisorHeight());

        // Header with count badge
        label(g, "MISSION ADVISOR", sideX + 18, top + 26, theme.accent());
        List<RecommendationSnapshot> recs = snapshot.recommendations();
        String countBadge = recs.size() + " ACTIVE";
        int badgeX = sideX + sideW - 84;
        g.setColor(recs.isEmpty() ? new Color(theme.nominal().getRed(), theme.nominal().getGreen(), theme.nominal().getBlue(), 35)
                : new Color(theme.warning().getRed(), theme.warning().getGreen(), theme.warning().getBlue(), 35));
        g.fillRoundRect(badgeX, top + 13, 68, 18, 6, 6);
        g.setColor(recs.isEmpty() ? theme.nominal() : theme.warning());
        g.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g.drawString(countBadge, badgeX + 8, top + 26);

        int y = layout.rowsY();
        if (recs.isEmpty()) {
            label(g, "✓ All systems nominal", sideX + 18, y + 16, theme.nominal());
            small(g, "Supervisory autopilot monitoring telemetry. No active cautions.", sideX + 18, y + 38);
            return;
        }

        for (RecommendationSnapshot rec : recs.stream().limit(layout.visibleRecommendations()).toList()) {
            // Recommendation card
            g.setColor(theme.panelRaised());
            g.fillRoundRect(sideX + 12, y, sideW - 24, layout.rowHeight() - 4, 10, 10);
            g.setColor(theme.grid());
            g.setStroke(new BasicStroke(1));
            g.drawRoundRect(sideX + 12, y, sideW - 24, layout.rowHeight() - 4, 10, 10);

            // Risk indicator accent bar
            Color riskColor = rec.risk() == ActionRisk.COSTLY ? theme.warning() : theme.nominal();
            g.setColor(riskColor);
            g.fillRoundRect(sideX + 12, y, 4, layout.rowHeight() - 4, 4, 4);

            label(g, rec.title(), sideX + 24, y + 20, theme.foreground());
            small(g, fitText(g, rec.explanation(), sideW - 52), sideX + 24, y + 38);

            // Countdown chip
            g.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g.setColor(riskColor);
            g.drawString(rec.risk().name() + "  ·  " + rec.ticksLeft() + " TICKS REMAINING", sideX + 24, y + 53);

            // Interactive action buttons: PREVIEW, EXECUTE, DISMISS
            int buttonY = y + layout.rowHeight() - 25;
            int buttonW = (sideW - 42) / 3;
            drawButton(g, sideX + 20, buttonY, buttonW, 19, "PREVIEW", false);
            drawButton(g, sideX + 24 + buttonW, buttonY, buttonW, 19, "EXECUTE", true);
            drawButton(g, sideX + 28 + buttonW * 2, buttonY, buttonW, 19, "DISMISS", false);

            y += layout.rowHeight();
        }
    }

    /** Mission progress card and 30-tick what-if forecast popover. */
    private void drawProgressAndWhatIf(Graphics2D g, DashboardLayout layout, int height) {
        int sideX = layout.sideX(), sideW = layout.sideW();
        int plotY = layout.plotY();
        int plotH = height - plotY - layout.pad();

        card(g, sideX, plotY, sideW, plotH);
        label(g, "MISSION PROGRESS & TRAJECTORY", sideX + 18, plotY + 26, theme.accent());

        // Glowing progress bar
        drawProgress(g, sideX + 18, plotY + 44, sideW - 36, 12, snapshot.mission().progressPercent());

        g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g.setColor(theme.foreground());
        g.drawString(String.format(Locale.ROOT, "%.1f%% COMPLETED", snapshot.mission().progressPercent()), sideX + 18, plotY + 76);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.setColor(theme.muted());
        g.drawString("ETA: " + snapshot.mission().etaTicks() + " TICKS", sideX + 150, plotY + 76);

        // Mini telemetry trajectory track
        drawTimeline(g, sideX + 18, plotY + 92, sideW - 36, Math.max(26, plotH - 105));

        // What-if preview forecast display
        if (!previewMessage.isBlank()) {
            int previewY = plotY + 84;
            int previewH = Math.min(68, plotH - 92);
            g.setColor(theme.panel());
            g.fillRoundRect(sideX + 10, previewY, sideW - 20, previewH, 10, 10);
            g.setColor(theme.accent());
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(sideX + 10, previewY, sideW - 20, previewH, 10, 10);

            label(g, "WHAT-IF PREVIEW  /  30-TICK FORECAST", sideX + 18, previewY + 18, theme.accent());
            small(g, fitText(g, previewMessage, sideW - 40), sideX + 18, previewY + 36);
        }
    }

    /** Telemetry trend path inside progress card. */
    private void drawTimeline(Graphics2D g, int x, int y, int width, int height) {
        Path2D.Double path = new Path2D.Double();
        int samples = Math.max(8, Math.min(50, snapshot.tick() + 1));
        for (int i = 0; i < samples; i++) {
            double px = x + (width * i / (double) (samples - 1));
            double py = y + height * (0.78 - 0.58 * i / (double) (samples - 1));
            if (i == 0) path.moveTo(px, py);
            else path.lineTo(px, py);
        }
        g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 140));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(path);
    }

    private void drawButton(Graphics2D g, int x, int y, int width, int height, String text, boolean primary) {
        g.setColor(primary ? theme.accent() : theme.panelRaised());
        g.fillRoundRect(x, y, width, height, 10, 10);
        g.setColor(theme.grid());
        g.setStroke(new BasicStroke(1));
        g.drawRoundRect(x, y, width, height, 10, 10);
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.setColor(primary && theme.displayName().equals("Projector Light") ? Color.WHITE : theme.foreground());
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x + Math.max(8, (width - fm.stringWidth(text)) / 2), y + height / 2 + 4);
    }

    /** Status footer bar displaying overall operational state. */
    private void drawStatusFooter(Graphics2D g, int height) {
        Color stateTone = snapshot.spacecraftState().equals("CRITICAL")
                ? theme.critical()
                : (snapshot.spacecraftState().equals("WARNING") ? theme.warning() : theme.nominal());
        g.setColor(stateTone);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String autopilotStr = snapshot.autopilotOnline() ? "  /  AUTOPILOT ONLINE" : "  /  AUTOPILOT OFFLINE";
        g.drawString("SPACECRAFT STATE: " + snapshot.spacecraftState() + autopilotStr, 30, height - 18);
    }
}
