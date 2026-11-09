package com.orbitalix.gui.core;

import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.MissionEventListener;
import com.orbitalix.api.Severity;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.theme.Theme;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.BorderFactory;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;

/** Clickable, bounded event ticker with severity-filtered recent-event drawer. */
public final class BottomTicker extends JLabel implements MissionEventListener {
    private final Theme theme;
    private final SimulationController controller;
    private final Deque<MissionEvent> recent = new ArrayDeque<>();
    private Severity filter;

    public BottomTicker(SimulationController controller, Theme theme) {
        this.controller = controller;
        this.theme = theme;
        setFont(getFont().deriveFont(Font.PLAIN, 11f));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setForeground(theme.muted());
        setText("MISSION EVENT LOG  /  click to browse");
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) { showDrawer(); }
        });
        controller.addEventListener(this);
    }

    public void close() { controller.removeEventListener(this); }

    @Override public void onEvent(MissionEvent event) {
        recent.addFirst(event);
        while (recent.size() > 40) recent.removeLast();
        if (filter == null || filter == event.severity()) showEvent(event);
    }

    private void showEvent(MissionEvent event) {
        setForeground(switch (event.severity()) {
            case INFO -> theme.accent();
            case LOW, MEDIUM -> theme.warning();
            case HIGH -> theme.critical();
            case CRITICAL -> theme.critical();
        });
        setText("T+" + event.tick() + "  /  " + event.severity() + "  /  " + event.message());
        setToolTipText(event.message());
    }

    private void showDrawer() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem all = new JMenuItem("All severities");
        all.addActionListener(event -> { filter = null; showLatest(); });
        menu.add(all);
        for (Severity severity : Severity.values()) {
            JMenuItem item = new JMenuItem(severity.name());
            item.addActionListener(event -> { filter = severity; showLatest(); });
            menu.add(item);
        }
        menu.addSeparator();
        recent.stream().filter(event -> filter == null || filter == event.severity()).limit(12).forEach(event -> {
            JMenuItem item = new JMenuItem("T+" + event.tick() + "  " + event.severity() + "  " + event.message());
            item.addActionListener(ignored -> showEvent(event));
            menu.add(item);
        });
        menu.show(this, 0, -menu.getPreferredSize().height);
    }

    private void showLatest() {
        recent.stream().filter(event -> filter == null || filter == event.severity()).findFirst().ifPresent(this::showEvent);
    }
}
