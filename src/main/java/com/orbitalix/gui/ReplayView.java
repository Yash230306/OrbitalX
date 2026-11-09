package com.orbitalix.gui;

import com.orbitalix.api.EventType;
import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.SimulationSnapshot;
import com.orbitalix.gui.theme.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Historical timeline with event markers, stepping, playback, and action attribution. */
public final class ReplayView extends MissionView {
    private boolean playing;
    private boolean scrubbing;
    private double playbackElapsed;

    public ReplayView(com.orbitalix.api.SimulationController controller, Theme theme) {
        super("Replay", controller, theme);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if (insideTimeline(event.getX(), event.getY())) {
                    scrubbing = true;
                    scrub(event.getX());
                }
            }
            @Override public void mouseReleased(MouseEvent event) { scrubbing = false; }
            @Override public void mouseClicked(MouseEvent event) { handleControls(event.getX(), event.getY()); }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent event) {
                if (scrubbing) scrub(event.getX());
            }
        });
    }

    public boolean isPlaying() { return playing; }
    public int selectedTick() { return snapshot.tick(); }
    public int eventMarkerCount() { return controller.getEvents().size(); }

    @Override protected void paintView(Graphics2D g) {
        int width = getWidth(), height = getHeight();
        g.setPaint(new java.awt.GradientPaint(0, 0, theme.background(), width, height, theme.panel()));
        g.fillRect(0, 0, width, height);
        g.setColor(theme.foreground()); g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.drawString("REPLAY", 28, 40);
        g.setColor(theme.muted()); g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("MISSION TELEMETRY  /  T+" + String.format(Locale.ROOT, "%03d", snapshot.tick())
                + (snapshot.replaying() ? "  /  REPLAY MODE" : "  /  LIVE"), 30, 62);

        int x = 28, y = 88, panelWidth = width - 56, panelHeight = height - 116;
        card(g, x, y, panelWidth, panelHeight);
        label(g, "MISSION TIMELINE  /  EVENT MARKERS", x + 24, y + 30, theme.accent());
        List<SimulationSnapshot> frames = frames();
        int lineX = timelineLeft(), lineWidth = timelineWidth(), lineY = timelineY();
        g.setColor(theme.grid()); g.fillRoundRect(lineX, lineY - 2, lineWidth, 4, 4, 4);
        if (!frames.isEmpty()) {
            for (MissionEvent event : controller.getEvents()) {
                int markerX = tickX(frames, event.tick());
                g.setColor(eventColor(event));
                g.fillOval(markerX - 4, lineY - 5, 8, 8);
            }
            int currentX = tickX(frames, snapshot.tick());
            g.setColor(theme.foreground()); g.fillOval(currentX - 7, lineY - 8, 14, 14);
            g.setColor(theme.accent()); g.fillOval(currentX - 4, lineY - 5, 8, 8);
            small(g, "T+" + frames.get(0).tick(), lineX, lineY + 22);
            String endTick = "T+" + frames.get(frames.size() - 1).tick();
            small(g, endTick, lineX + lineWidth - g.getFontMetrics().stringWidth(endTick), lineY + 22);
        }

        int[] buttonX = buttonPositions();
        int buttonY = y + 126;
        drawButton(g, buttonX[0], buttonY, buttonWidth(), 34, "STEP BACK", false);
        drawButton(g, buttonX[1], buttonY, buttonWidth(), 34, playing ? "PAUSE" : "PLAY", true);
        drawButton(g, buttonX[2], buttonY, buttonWidth(), 34, "STEP FORWARD", false);
        drawButton(g, buttonX[3], buttonY, buttonWidth(), 34, "RESUME LIVE", false);

        int chartX = x + 28, chartY = y + 206, chartWidth = panelWidth - 56;
        int chartHeight = Math.max(90, panelHeight - 292);
        card(g, chartX, chartY, chartWidth, chartHeight);
        label(g, "RECORDED TELEMETRY  /  PROGRESS AND FUEL", chartX + 16, chartY + 24, theme.accent());
        drawHistoryChart(g, frames, chartX + 16, chartY + 40, chartWidth - 32, chartHeight - 56);

        int tallyY = y + panelHeight - 42;
        label(g, "ACTION TALLY", x + 24, tallyY, theme.muted());
        drawTally(g, x + 148, tallyY, EventType.ACTION_BY_HUMAN, "HUMAN", theme.accent());
        drawTally(g, x + 320, tallyY, EventType.ACTION_BY_AUTOPILOT, "AUTOPILOT", theme.nominal());
        drawTally(g, x + 520, tallyY, EventType.SELF_HEALING, "SELF-HEAL", theme.warning());
        drawTally(g, x + 700, tallyY, EventType.CREW, "CREW", theme.critical());
    }

    @Override protected void animate(double elapsedSeconds) {
        if (!playing) return;
        playbackElapsed += elapsedSeconds;
        if (playbackElapsed < .35) return;
        playbackElapsed = 0;
        List<SimulationSnapshot> frames = frames();
        int current = selectedIndex(frames);
        if (current >= frames.size() - 1) {
            playing = false;
        } else {
            seek(frames, current + 1);
        }
    }

    @Override protected void onSnapshot(SimulationSnapshot value) {
        if (!value.replaying()) playing = false;
    }

    private void drawHistoryChart(Graphics2D g, List<SimulationSnapshot> frames, int x, int y, int width, int height) {
        for (int i = 0; i <= 4; i++) {
            int gridY = y + height * i / 4;
            g.setColor(theme.grid()); g.drawLine(x, gridY, x + width, gridY);
        }
        if (frames.size() < 2) {
            small(g, "Record more ticks to build the replay chart.", x + 8, y + Math.max(20, height / 2));
            return;
        }
        Path2D progress = new Path2D.Double(), fuel = new Path2D.Double();
        for (int i = 0; i < frames.size(); i++) {
            SimulationSnapshot frame = frames.get(i);
            double px = x + width * i / (double)(frames.size() - 1);
            double progressY = y + height * (1 - frame.mission().progressPercent() / 100d);
            double fuelY = y + height * (1 - fuelPercent(frame));
            if (i == 0) { progress.moveTo(px, progressY); fuel.moveTo(px, fuelY); }
            else { progress.lineTo(px, progressY); fuel.lineTo(px, fuelY); }
        }
        g.setStroke(new BasicStroke(2));
        g.setColor(theme.accent()); g.draw(progress);
        g.setColor(theme.warning()); g.draw(fuel);
        int markerX = x + width * selectedIndex(frames) / Math.max(1, frames.size() - 1);
        g.setColor(new Color(theme.foreground().getRed(), theme.foreground().getGreen(),
                theme.foreground().getBlue(), 100));
        g.drawLine(markerX, y, markerX, y + height);
    }

    private void drawTally(Graphics2D g, int x, int y, EventType type, String label, Color color) {
        long count = controller.getEvents().stream().filter(event -> event.type() == type).count();
        g.setColor(color); g.fillOval(x, y - 9, 8, 8);
        small(g, label + "  " + count, x + 13, y);
    }

    private double fuelPercent(SimulationSnapshot frame) {
        return frame.resources().stream().filter(resource -> resource.name().equalsIgnoreCase("Fuel"))
                .findFirst().map(resource -> resource.capacity() <= 0 ? 0
                        : Math.max(0, Math.min(1, resource.level() / resource.capacity())))
                .orElse(0d);
    }

    private Color eventColor(MissionEvent event) {
        return switch (event.type()) {
            case ACTION_BY_HUMAN -> theme.accent();
            case ACTION_BY_AUTOPILOT -> theme.nominal();
            case SELF_HEALING -> theme.warning();
            default -> switch (event.severity()) {
                case INFO -> theme.muted();
                case LOW, MEDIUM -> theme.warning();
                case HIGH, CRITICAL -> theme.critical();
            };
        };
    }

    private List<SimulationSnapshot> frames() {
        Map<Integer, SimulationSnapshot> byTick = new LinkedHashMap<>();
        for (SimulationSnapshot item : controller.getHistory()) byTick.put(item.tick(), item);
        return new ArrayList<>(byTick.values());
    }

    private int selectedIndex(List<SimulationSnapshot> frames) {
        for (int i = frames.size() - 1; i >= 0; i--)
            if (frames.get(i).tick() == snapshot.tick()) return i;
        return Math.max(0, frames.size() - 1);
    }

    private int tickX(List<SimulationSnapshot> frames, int tick) {
        int index = 0;
        for (int i = 0; i < frames.size(); i++)
            if (frames.get(i).tick() <= tick) index = i; else break;
        return timelineLeft() + timelineWidth() * index / Math.max(1, frames.size() - 1);
    }

    private void scrub(int x) {
        List<SimulationSnapshot> frames = frames();
        if (frames.isEmpty()) return;
        int index = Math.round((Math.max(timelineLeft(), Math.min(timelineLeft() + timelineWidth(), x))
                - timelineLeft()) * (frames.size() - 1f) / timelineWidth());
        seek(frames, index);
    }

    private void handleControls(int x, int y) {
        int buttonY = 88 + 126;
        if (y < buttonY || y > buttonY + 34) return;
        int[] positions = buttonPositions();
        int selectedButton = -1;
        for (int i = 0; i < positions.length; i++)
            if (x >= positions[i] && x <= positions[i] + buttonWidth()) selectedButton = i;
        List<SimulationSnapshot> frames = frames();
        if (selectedButton == 0) seek(frames, selectedIndex(frames) - 1);
        else if (selectedButton == 1) {
            playing = !playing;
            playbackElapsed = 0;
            if (playing && selectedIndex(frames) >= frames.size() - 1 && !frames.isEmpty()) seek(frames, 0);
        } else if (selectedButton == 2) seek(frames, selectedIndex(frames) + 1);
        else if (selectedButton == 3) {
            playing = false;
            controller.resumeLive();
        }
    }

    private void seek(List<SimulationSnapshot> frames, int index) {
        if (frames.isEmpty()) return;
        int selected = Math.max(0, Math.min(frames.size() - 1, index));
        controller.seekTo(frames.get(selected).tick());
    }

    private boolean insideTimeline(int x, int y) {
        return x >= timelineLeft() - 8 && x <= timelineLeft() + timelineWidth() + 8
                && y >= timelineY() - 14 && y <= timelineY() + 16;
    }

    private int timelineLeft() { return 56; }
    private int timelineWidth() { return Math.max(1, getWidth() - 112); }
    private int timelineY() { return 88 + 96; }
    private int buttonWidth() { return Math.min(150, (getWidth() - 112 - 36) / 4); }
    private int[] buttonPositions() {
        int width = buttonWidth(), total = width * 4 + 36;
        int first = 56 + Math.max(0, (getWidth() - 112 - total) / 2);
        return new int[]{first, first + width + 12, first + (width + 12) * 2, first + (width + 12) * 3};
    }

    private void drawButton(Graphics2D g, int x, int y, int width, int height, String text, boolean primary) {
        g.setColor(primary ? theme.accent() : theme.panelRaised());
        g.fillRoundRect(x, y, width, height, 9, 9);
        g.setColor(primary ? theme.background() : theme.foreground());
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        int textX = x + (width - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, textX, y + 22);
    }
}
