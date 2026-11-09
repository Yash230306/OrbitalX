package com.orbitalix.gui;

import com.orbitalix.api.ScoreSnapshot;
import com.orbitalix.api.SimulationSnapshot;
import com.orbitalix.gui.theme.Theme;
import com.orbitalix.scoring.Grade;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/** Report preview, grade stamp, score breakdown, and full achievement gallery. */
public final class ReportsView extends MissionView {
    private final JEditorPane reportPreview = new JEditorPane();
    private final JScrollPane previewScroll;
    private String exportMessage = "HTML preview is live; exports are written to the out folder.";
    private String previewContent = "";
    private double gradeReveal;

    public ReportsView(com.orbitalix.api.SimulationController controller, Theme theme) {
        super("Reports", controller, theme);
        setLayout(null);
        reportPreview.setContentType("text/html");
        reportPreview.setEditable(false);
        reportPreview.setBackground(theme.background());
        reportPreview.setForeground(theme.foreground());
        reportPreview.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        previewScroll = new JScrollPane(reportPreview,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        previewScroll.setBorder(BorderFactory.createLineBorder(theme.grid()));
        add(previewScroll);
        refreshPreview();
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) { handleClick(event.getX(), event.getY()); }
        });
    }

    public String previewHtml() { return previewContent; }
    public String exportMessage() { return exportMessage; }
    public double gradeReveal() { return gradeReveal; }
    public int achievementCount() { return snapshot.score().earnedAchievements().size() + snapshot.score().lockedAchievements().size(); }

    @Override public void doLayout() {
        int contentWidth = Math.max(100, getWidth() - 104);
        int previewWidth = (int)(contentWidth * .58);
        previewScroll.setBounds(52, 144, previewWidth, Math.max(120, getHeight() - 250));
    }

    @Override protected void onSnapshot(SimulationSnapshot value) { refreshPreview(); }

    @Override protected void animate(double elapsedSeconds) {
        if (gradeReveal < 1) gradeReveal = Math.min(1, gradeReveal + elapsedSeconds * 2.4);
    }

    @Override protected void paintView(Graphics2D g) {
        int width = getWidth(), height = getHeight();
        g.setPaint(new java.awt.GradientPaint(0, 0, theme.background(), width, height, theme.panel()));
        g.fillRect(0, 0, width, height);
        g.setColor(theme.foreground()); g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.drawString("MISSION REPORT", 28, 40);
        g.setColor(theme.muted()); g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("FLIGHT DATA  /  T+" + String.format(java.util.Locale.ROOT, "%03d", snapshot.tick()), 30, 62);

        int panelX = 28, panelY = 88, panelWidth = width - 56, panelHeight = height - 126;
        card(g, panelX, panelY, panelWidth, panelHeight);
        int contentWidth = Math.max(100, width - 104);
        int previewWidth = (int)(contentWidth * .58);
        int rightX = 52 + previewWidth + 24;
        int rightWidth = width - rightX - 28;
        label(g, "HTML REPORT PREVIEW", 52, 122, theme.accent());
        label(g, "MISSION GRADE", rightX, 122, theme.accent());
        drawGradeStamp(g, rightX + 52, 184, snapshot.score().grade());
        label(g, snapshot.mission().outcome(), rightX + 104, 170, theme.foreground());
        small(g, "SCORE  /  " + snapshot.score().points() + " POINTS", rightX + 104, 192);
        small(g, Grade.valueOf(snapshot.score().grade()).description(), rightX + 104, 210);
        small(g, "TICKS  /  " + snapshot.tick() + "    PROGRESS  /  "
                + String.format(java.util.Locale.ROOT, "%.1f", snapshot.mission().progressPercent()) + "%",
                rightX, 232);
        small(g, "HUMAN ACTIONS  /  " + eventCount(com.orbitalix.api.EventType.ACTION_BY_HUMAN)
                + "    AUTOPILOT  /  " + eventCount(com.orbitalix.api.EventType.ACTION_BY_AUTOPILOT), rightX, 252);
        small(g, "SELF-HEALING  /  " + eventCount(com.orbitalix.api.EventType.SELF_HEALING)
                + "    CREW TASKS  /  " + eventCount(com.orbitalix.api.EventType.CREW), rightX, 272);
        label(g, "ACHIEVEMENT GALLERY", rightX, 310, theme.accent());
        drawAchievements(g, rightX, rightWidth, 330, snapshot.score());

        int exportY = panelY + panelHeight - 54;
        drawButton(g, 52, exportY, 138, 36, "EXPORT HTML", true);
        drawButton(g, 202, exportY, 138, 36, "EXPORT CSV", false);
        drawButton(g, 352, exportY, 120, 36, "REFRESH", false);
        small(g, fitText(g, exportMessage, Math.max(100, previewWidth - 12)), 52, exportY + 53);
    }

    private void drawGradeStamp(Graphics2D g, int centerX, int centerY, String grade) {
        int radius = (int)(35 * gradeReveal);
        if (radius <= 0) return;
        Color stamp = grade.equals("A") || grade.equals("S") ? theme.nominal()
                : grade.equals("B") ? theme.accent() : grade.equals("C") ? theme.warning() : theme.critical();
        g.setColor(stamp); g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        g.setFont(new Font("Segoe UI", Font.BOLD, Math.max(12, radius)));
        int textWidth = g.getFontMetrics().stringWidth(grade);
        g.drawString(grade, centerX - textWidth / 2, centerY + radius / 3);
    }

    private void drawAchievements(Graphics2D g, int x, int width, int y, ScoreSnapshot score) {
        List<String> all = new ArrayList<>(score.earnedAchievements());
        all.addAll(score.lockedAchievements());
        int columns = 2, gap = 8, itemWidth = Math.max(90, (width - gap) / columns);
        for (int i = 0; i < all.size(); i++) {
            String achievement = all.get(i);
            boolean earned = score.earnedAchievements().contains(achievement);
            int col = i % columns, row = i / columns;
            int bx = x + col * (itemWidth + gap), by = y + row * 38;
            g.setColor(earned ? theme.panelRaised() : theme.panel());
            g.fillRoundRect(bx, by, itemWidth, 30, 8, 8);
            g.setColor(earned ? theme.nominal() : theme.grid());
            g.drawRoundRect(bx, by, itemWidth, 30, 8, 8);
            g.setColor(earned ? theme.foreground() : theme.muted());
            g.setFont(new Font("Segoe UI", Font.BOLD, 9));
            String title = fitText(g, achievement.toUpperCase(java.util.Locale.ROOT), itemWidth - 14);
            g.drawString(title, bx + 7, by + 13);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g.drawString(earned ? "UNLOCKED" : "LOCKED", bx + 7, by + 25);
        }
    }

    private long eventCount(com.orbitalix.api.EventType type) {
        return controller.getEvents().stream().filter(event -> event.type() == type).count();
    }

    private void refreshPreview() {
        previewContent = controller.generateReportHtml();
        reportPreview.setText(previewContent);
        reportPreview.setCaretPosition(0);
    }

    private void handleClick(int x, int y) {
        int panelHeight = getHeight() - 126;
        int exportY = 88 + panelHeight - 54;
        if (y < exportY || y > exportY + 36) return;
        if (x >= 52 && x <= 190) export(true);
        else if (x >= 202 && x <= 340) export(false);
        else if (x >= 352 && x <= 472) {
            refreshPreview();
            exportMessage = "Report preview refreshed.";
            repaint();
        }
    }

    private void export(boolean html) {
        Path destination = Path.of("out", html ? "mission-report.html" : "telemetry.csv");
        try {
            Files.createDirectories(destination.getParent());
            Files.writeString(destination, html ? controller.generateReportHtml() : controller.exportTelemetryCsv());
            exportMessage = "EXPORTED  /  " + destination.toAbsolutePath();
        } catch (IOException ex) {
            exportMessage = "EXPORT FAILED  /  " + ex.getMessage();
        }
        repaint();
    }

    private void drawButton(Graphics2D g, int x, int y, int width, int height, String text, boolean primary) {
        g.setColor(primary ? theme.accent() : theme.panelRaised());
        g.fillRoundRect(x, y, width, height, 9, 9);
        g.setColor(primary ? theme.background() : theme.foreground());
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.drawString(text, x + (width - g.getFontMetrics().stringWidth(text)) / 2, y + 23);
    }
}
