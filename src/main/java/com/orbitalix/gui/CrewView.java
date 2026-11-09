package com.orbitalix.gui;

import com.orbitalix.api.CrewSnapshot;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.theme.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.Locale;

/** Crew health and tasking console with four direct assignment controls per member. */
public final class CrewView extends MissionView {
    private String taskMessage = "Select a crew task to assign an onboard action.";

    public CrewView(SimulationController controller, Theme theme) {
        super("Crew", controller, theme);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) { assignTaskAt(event.getX(), event.getY()); }
        });
    }

    public String taskMessage() { return taskMessage; }

    @Override protected void drawCrew(Graphics2D g, int width, int height) {
        int margin = 28, top = 88, gap = 14, columns = 2;
        int cardWidth = (width - 2 * margin - gap) / columns;
        int cardHeight = (height - top - 48 - gap) / 2;
        for (int i = 0; i < snapshot.crew().size(); i++) {
            CrewSnapshot person = snapshot.crew().get(i);
            int x = margin + (i % columns) * (cardWidth + gap);
            int y = top + (i / columns) * (cardHeight + gap);
            card(g, x, y, cardWidth, cardHeight);
            g.setColor(person.alive() ? theme.accent() : theme.critical());
            g.fillOval(x + 18, y + 18, 48, 48);
            g.setColor(theme.background()); g.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g.drawString(person.name().substring(0, 1), x + 36, y + 50);
            label(g, person.name(), x + 80, y + 32, theme.foreground());
            small(g, person.role().toUpperCase(Locale.ROOT) + "  /  SKILL: " + skillFor(person.role()), x + 80, y + 51);
            drawMeter(g, x + 18, y + 82, cardWidth - 36, "HEALTH", person.health(), theme.nominal());
            drawMeter(g, x + 18, y + 108, cardWidth - 36, "STRESS", person.stress(), theme.warning());
            drawMeter(g, x + 18, y + 134, cardWidth - 36, "RADIATION DOSE", Math.min(100, person.radiationDose()), theme.critical());
            small(g, "TASK  /  " + (person.taskTicksLeft() > 0 ? person.task() + "  ·  " + person.taskTicksLeft() + " TICKS" : "READY"),
                    x + 18, y + 164);
            int cx = x + cardWidth - 34, cy = y + 36;
            g.setColor(theme.grid()); g.setStroke(new BasicStroke(4)); g.drawOval(cx - 16, cy - 16, 32, 32);
            if (person.taskTicksLeft() > 0) {
                g.setColor(theme.accent());
                double progress = 1 - Math.min(1, person.taskTicksLeft() / 20d);
                g.draw(new Arc2D.Double(cx - 16, cy - 16, 32, 32, 90, -360 * progress, Arc2D.OPEN));
            }
            CrewTaskType[] tasks = CrewTaskType.values();
            int buttonWidth = (cardWidth - 48) / 2, buttonY = y + cardHeight - 58;
            for (int task = 0; task < tasks.length; task++) {
                int bx = x + 18 + (task % 2) * (buttonWidth + 10);
                int by = buttonY + (task / 2) * 25;
                boolean enabled = person.alive() && person.taskTicksLeft() == 0;
                g.setColor(enabled ? theme.panelRaised() : theme.grid());
                g.fillRoundRect(bx, by, buttonWidth, 21, 7, 7);
                g.setColor(enabled ? theme.foreground() : theme.muted());
                g.setFont(new Font("Segoe UI", Font.BOLD, 8));
                String taskLabel = tasks[task].name().replace('_', ' ');
                g.drawString(taskLabel, bx + Math.max(5, (buttonWidth - g.getFontMetrics().stringWidth(taskLabel)) / 2), by + 14);
            }
        }
        small(g, taskMessage, margin, height - 18);
    }

    private void assignTaskAt(int x, int y) {
        int margin = 28, top = 88, gap = 14, columns = 2, width = getWidth(), height = getHeight();
        int cardWidth = (width - 2 * margin - gap) / columns;
        int cardHeight = (height - top - 48 - gap) / 2;
        int col = (x - margin) / (cardWidth + gap), row = (y - top) / (cardHeight + gap);
        if (col < 0 || col >= columns || row < 0 || row > 1) return;
        int memberIndex = row * columns + col;
        if (memberIndex >= snapshot.crew().size()) return;
        int localX = x - (margin + col * (cardWidth + gap)) - 18;
        int localY = y - (top + row * (cardHeight + gap)) - (cardHeight - 58);
        int buttonWidth = (cardWidth - 48) / 2;
        int taskCol = localX / (buttonWidth + 10), taskRow = localY / 25;
        if (taskCol < 0 || taskCol > 1 || taskRow < 0 || taskRow > 1
                || localX % (buttonWidth + 10) > buttonWidth || localY % 25 > 21) return;
        CrewSnapshot person = snapshot.crew().get(memberIndex);
        if (!person.alive() || person.taskTicksLeft() > 0) return;
        CrewTaskType task = CrewTaskType.values()[taskRow * 2 + taskCol];
        try {
            controller.assignCrewTask(person.name(), task);
            taskMessage = "ASSIGNED  /  " + person.name() + "  /  " + task.name().replace('_', ' ');
        } catch (IllegalArgumentException | IllegalStateException ex) {
            taskMessage = "TASK REJECTED  /  " + ex.getMessage();
        }
        repaint();
    }

    private static String skillFor(String role) {
        return switch (role.toLowerCase(Locale.ROOT)) {
            case "engineer" -> "MECHANICAL";
            case "medical" -> "MEDICAL";
            case "pilot" -> "NAVIGATION";
            default -> "LEADERSHIP";
        };
    }
}
