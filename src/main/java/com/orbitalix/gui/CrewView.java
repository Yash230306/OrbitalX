package com.orbitalix.gui;

import com.orbitalix.api.CrewSnapshot;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.theme.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.Locale;

/** Interactive crew command console with responsive task buttons, live hover feedback, and avionics guidance. */
public final class CrewView extends MissionView {
    private String taskMessage = "Select a crew task to assign an onboard astronaut action.";
    private int hoverMember = -1;
    private int hoverTask = -1;

    public CrewView(SimulationController controller, Theme theme) {
        super("Crew", controller, theme);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                handleActionAt(event.getX(), event.getY());
            }

            @Override public void mousePressed(MouseEvent event) {
                handleActionAt(event.getX(), event.getY());
            }

            @Override public void mouseMoved(MouseEvent event) {
                updateHover(event.getX(), event.getY());
            }

            @Override public void mouseExited(MouseEvent event) {
                if (hoverMember != -1 || hoverTask != -1) {
                    hoverMember = -1;
                    hoverTask = -1;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
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

            // Crew avatar with status glow ring
            Color healthColor = person.alive()
                    ? (person.health() > 70 ? theme.nominal() : person.health() > 30 ? theme.warning() : theme.critical())
                    : theme.critical();

            g.setColor(new Color(healthColor.getRed(), healthColor.getGreen(), healthColor.getBlue(), 35));
            g.fillOval(x + 14, y + 14, 52, 52);
            g.setColor(theme.panelRaised());
            g.fillOval(x + 17, y + 17, 46, 46);
            g.setColor(healthColor);
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(x + 17, y + 17, 46, 46);

            g.setColor(theme.foreground());
            g.setFont(new Font("Segoe UI", Font.BOLD, 19));
            g.drawString(person.name().substring(0, 1), x + 33, y + 46);

            g.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g.setColor(theme.foreground());
            g.drawString(person.name().toUpperCase(Locale.ROOT), x + 76, y + 32);

            g.setFont(new Font("Consolas", Font.PLAIN, 10));
            g.setColor(theme.accent());
            g.drawString(person.role().toUpperCase(Locale.ROOT) + "  //  CLASS " + skillFor(person.role()), x + 76, y + 48);

            // Health, Stress, Radiation meters
            drawMeter(g, x + 18, y + 74, cardWidth - 36, "VITALS / HEALTH", person.health(), healthColor);
            drawMeter(g, x + 18, y + 98, cardWidth - 36, "PSYCH / STRESS", person.stress(), theme.warning());
            drawMeter(g, x + 18, y + 122, cardWidth - 36, "RAD ACCUMULATION", Math.min(100, person.radiationDose()), theme.critical());

            // Active task status pill
            boolean busy = person.taskTicksLeft() > 0;
            g.setColor(busy ? new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 32) : theme.panelRaised());
            g.fillRoundRect(x + 18, y + 146, cardWidth - 76, 22, 6, 6);
            g.setColor(busy ? theme.accent() : theme.grid());
            g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(x + 18, y + 146, cardWidth - 76, 22, 6, 6);
            g.setFont(new Font("Consolas", Font.BOLD, 9));
            g.setColor(busy ? theme.accent() : theme.muted());
            g.drawString(busy ? "TASK: " + person.task() + " [" + person.taskTicksLeft() + "T]" : "STATUS: ON-STANDBY / READY", x + 26, y + 161);

            // Circular progress indicator
            int cx = x + cardWidth - 34, cy = y + 36;
            g.setColor(theme.grid());
            g.setStroke(new BasicStroke(3));
            g.drawOval(cx - 16, cy - 16, 32, 32);
            if (busy) {
                g.setColor(theme.accent());
                double progress = 1 - Math.min(1, person.taskTicksLeft() / 20d);
                g.draw(new Arc2D.Double(cx - 16, cy - 16, 32, 32, 90, -360 * progress, Arc2D.OPEN));
            }

            // Task Action Buttons (EVA REPAIR, MEDICAL CARE, MANUAL REBOOT, REST)
            CrewTaskType[] tasks = CrewTaskType.values();
            int buttonWidth = (cardWidth - 48) / 2;
            int buttonHeight = 22;
            int buttonY = y + cardHeight - 58;

            for (int task = 0; task < tasks.length; task++) {
                int bx = x + 18 + (task % 2) * (buttonWidth + 10);
                int by = buttonY + (task / 2) * 25;
                boolean enabled = person.alive() && person.taskTicksLeft() == 0;
                boolean isHovered = (hoverMember == i && hoverTask == task);

                if (!enabled) {
                    g.setColor(new Color(theme.panel().getRed(), theme.panel().getGreen(), theme.panel().getBlue(), 120));
                    g.fillRoundRect(bx, by, buttonWidth, buttonHeight, 6, 6);
                    g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 40));
                    g.drawRoundRect(bx, by, buttonWidth, buttonHeight, 6, 6);
                    g.setColor(theme.muted());
                } else if (isHovered) {
                    g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 45));
                    g.fillRoundRect(bx, by, buttonWidth, buttonHeight, 6, 6);
                    g.setColor(theme.accent());
                    g.setStroke(new BasicStroke(1.5f));
                    g.drawRoundRect(bx, by, buttonWidth, buttonHeight, 6, 6);
                    g.setColor(theme.foreground());
                } else {
                    g.setColor(theme.panelRaised());
                    g.fillRoundRect(bx, by, buttonWidth, buttonHeight, 6, 6);
                    g.setColor(theme.grid());
                    g.setStroke(new BasicStroke(1f));
                    g.drawRoundRect(bx, by, buttonWidth, buttonHeight, 6, 6);
                    g.setColor(theme.foreground());
                }

                g.setFont(new Font("Segoe UI", Font.BOLD, 9));
                String taskLabel = tasks[task].name().replace('_', ' ');
                int textX = bx + Math.max(5, (buttonWidth - g.getFontMetrics().stringWidth(taskLabel)) / 2);
                g.drawString(taskLabel, textX, by + 15);
            }
        }

        // Bottom status & guidance bar
        int barY = height - 26;
        g.setColor(theme.panel());
        g.fillRoundRect(margin, barY - 14, width - 2 * margin, 24, 6, 6);
        g.setColor(theme.grid());
        g.drawRoundRect(margin, barY - 14, width - 2 * margin, 24, 6, 6);
        g.setColor(theme.accent());
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.drawString("AVIONICS // CREW COMMAND: ", margin + 12, barY + 3);
        g.setColor(theme.foreground());
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.drawString(taskMessage, margin + 165, barY + 3);
    }

    private void updateHover(int mouseX, int mouseY) {
        int margin = 28, top = 88, gap = 14, columns = 2, width = getWidth(), height = getHeight();
        int cardWidth = (width - 2 * margin - gap) / columns;
        int cardHeight = (height - top - 48 - gap) / 2;

        int prevHoverMember = hoverMember;
        int prevHoverTask = hoverTask;
        hoverMember = -1;
        hoverTask = -1;

        for (int i = 0; i < snapshot.crew().size(); i++) {
            int cx = margin + (i % columns) * (cardWidth + gap);
            int cy = top + (i / columns) * (cardHeight + gap);
            int buttonWidth = (cardWidth - 48) / 2;
            int buttonHeight = 22;
            int buttonY = cy + cardHeight - 58;

            for (int task = 0; task < CrewTaskType.values().length; task++) {
                int bx = cx + 18 + (task % 2) * (buttonWidth + 10);
                int by = buttonY + (task / 2) * 25;
                if (mouseX >= bx && mouseX <= bx + buttonWidth && mouseY >= by && mouseY <= by + buttonHeight) {
                    hoverMember = i;
                    hoverTask = task;
                    break;
                }
            }
            if (hoverMember != -1) break;
        }

        if (hoverMember != -1 && hoverTask != -1) {
            CrewSnapshot person = snapshot.crew().get(hoverMember);
            CrewTaskType task = CrewTaskType.values()[hoverTask];
            setCursor(person.alive() && person.taskTicksLeft() == 0
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());

            if (!person.alive()) {
                taskMessage = person.name() + " is deceased / incapacitated.";
            } else if (person.taskTicksLeft() > 0) {
                taskMessage = person.name() + " is currently performing " + person.task() + " (" + person.taskTicksLeft() + " ticks left).";
            } else {
                taskMessage = switch (task) {
                    case EVA_REPAIR -> "EVA REPAIR // Spacewalk: Repairs hardware failures (Hull breach, Fuel leak, Thrusters, Solar array).";
                    case MEDICAL_CARE -> "MEDICAL CARE // Stasis & Trauma Meds: Restores astronaut vitals (+30 HP).";
                    case MANUAL_REBOOT -> "MANUAL REBOOT // Flight Avionics Recycle: Clears CPU lockups and resets computer fault registers.";
                    case REST -> "REST IN QUARTERS // Psychological Recovery: Reduces stress by 30%, preventing mental breakdown.";
                };
            }
        } else {
            setCursor(Cursor.getDefaultCursor());
            if (taskMessage.startsWith("EVA") || taskMessage.startsWith("MEDICAL") || taskMessage.startsWith("MANUAL") || taskMessage.startsWith("REST")) {
                taskMessage = "Select a crew task to assign an onboard astronaut action.";
            }
        }

        if (hoverMember != prevHoverMember || hoverTask != prevHoverTask) {
            repaint();
        }
    }

    private void handleActionAt(int mouseX, int mouseY) {
        int margin = 28, top = 88, gap = 14, columns = 2, width = getWidth(), height = getHeight();
        int cardWidth = (width - 2 * margin - gap) / columns;
        int cardHeight = (height - top - 48 - gap) / 2;

        for (int i = 0; i < snapshot.crew().size(); i++) {
            int cx = margin + (i % columns) * (cardWidth + gap);
            int cy = top + (i / columns) * (cardHeight + gap);
            int buttonWidth = (cardWidth - 48) / 2;
            int buttonHeight = 22;
            int buttonY = cy + cardHeight - 58;

            for (int taskIdx = 0; taskIdx < CrewTaskType.values().length; taskIdx++) {
                int bx = cx + 18 + (taskIdx % 2) * (buttonWidth + 10);
                int by = buttonY + (taskIdx / 2) * 25;

                if (mouseX >= bx && mouseX <= bx + buttonWidth && mouseY >= by && mouseY <= by + buttonHeight) {
                    CrewSnapshot person = snapshot.crew().get(i);
                    CrewTaskType task = CrewTaskType.values()[taskIdx];

                    if (!person.alive()) {
                        taskMessage = "ACTION REJECTED // " + person.name() + " is deceased / incapacitated.";
                        repaint();
                        return;
                    }
                    if (person.taskTicksLeft() > 0) {
                        taskMessage = "ACTION REJECTED // " + person.name() + " is already executing " + person.task() + " (" + person.taskTicksLeft() + "T left).";
                        repaint();
                        return;
                    }

                    try {
                        controller.assignCrewTask(person.name(), task);
                        this.snapshot = controller.snapshot();
                        taskMessage = "ASSIGNED // " + person.name() + " -> " + task.name().replace('_', ' ') + " (In progress)";
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        taskMessage = "TASK REJECTED // " + ex.getMessage();
                    }
                    repaint();
                    return;
                }
            }
        }
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
