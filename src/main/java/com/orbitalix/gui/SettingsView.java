package com.orbitalix.gui;

import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.core.View;
import com.orbitalix.gui.theme.Theme;
import com.orbitalix.gui.theme.Themes;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.function.Consumer;

/** Persisted display, audio, animation, and accessibility settings. */
public final class SettingsView extends View {
    private final Consumer<Themes> onThemeSelected;
    private final JComboBox<Themes> themes = new JComboBox<>(Themes.values());
    private final JLabel fpsLabel = new JLabel("FPS  /  --");
    private long lastFpsUpdate;
    private int framesSinceUpdate;

    public SettingsView(SimulationController controller, Theme theme, Consumer<Themes> onThemeSelected) {
        super(controller, theme);
        this.onThemeSelected = onThemeSelected;
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.setOpaque(false);
        JLabel title = new JLabel("SYSTEM SETTINGS");
        title.setForeground(theme.foreground()); title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        JLabel subtitle = new JLabel("Preferences persist across application restarts.");
        subtitle.setForeground(theme.muted());
        heading.add(title); heading.add(subtitle);
        add(heading, BorderLayout.NORTH);
        JPanel settings = new JPanel(new GridLayout(0, 2, 12, 12));
        settings.setOpaque(false);
        try { themes.setSelectedItem(Themes.valueOf(controller.getSetting("theme", "DARK_MISSION"))); }
        catch (IllegalArgumentException ex) { themes.setSelectedItem(Themes.DARK_MISSION); }
        style(themes);
        themes.addActionListener(event -> onThemeSelected.accept((Themes)themes.getSelectedItem()));
        settings.add(row("COLOR THEME", themes));

        addToggle(settings, controller, "Sound alerts", "sound", "true");
        addChoice(settings, controller, "Animation quality", "animationQuality", new String[]{"HIGH", "MEDIUM", "LOW"}, "HIGH");
        addToggle(settings, controller, "Screen effects", "effects", "true");
        addToggle(settings, controller, "Show FPS counter", "showFps", "false");
        addChoice(settings, controller, "UI scale", "uiScale", new String[]{"90%", "100%", "110%", "125%"}, "100%");
        addChoice(settings, controller, "Sound volume", "soundVolume", new String[]{"25%", "50%", "75%", "100%"}, "75%");
        fpsLabel.setForeground(theme.accent()); fpsLabel.setFont(fpsLabel.getFont().deriveFont(Font.BOLD, 12f));
        settings.add(row("RENDER PERFORMANCE", fpsLabel));
        add(settings, BorderLayout.CENTER);
    }

    public JComboBox<Themes> themeSelector() { return themes; }

    @Override protected void paintView(Graphics2D g) {
        long now = System.nanoTime();
        framesSinceUpdate++;
        if (now - lastFpsUpdate >= 1_000_000_000L) {
            if (Boolean.parseBoolean(controller.getSetting("showFps", "false")))
                fpsLabel.setText("FPS  /  " + framesSinceUpdate);
            framesSinceUpdate = 0;
            lastFpsUpdate = now;
        }
        g.setColor(theme.background()); g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(theme.panel()); g.fillRoundRect(0, 90, getWidth(), getHeight() - 96, 18, 18);
        g.setColor(theme.grid()); g.drawRoundRect(0, 90, getWidth(), getHeight() - 96, 18, 18);
    }

    private void addToggle(JPanel parent, SimulationController controller, String label, String key, String fallback) {
        JToggleButton toggle = new JToggleButton();
        toggle.setSelected(Boolean.parseBoolean(controller.getSetting(key, fallback)));
        toggle.setText(toggle.isSelected() ? "ON" : "OFF");
        style(toggle);
        toggle.addActionListener(event -> {
            toggle.setText(toggle.isSelected() ? "ON" : "OFF");
            controller.setSetting(key, Boolean.toString(toggle.isSelected()));
            if (key.equals("showFps")) fpsLabel.setText(toggle.isSelected() ? "FPS  /  SAMPLING" : "FPS  /  DISABLED");
        });
        parent.add(row(label, toggle));
    }

    private void addChoice(JPanel parent, SimulationController controller, String label, String key,
                           String[] choices, String fallback) {
        JComboBox<String> choice = new JComboBox<>(choices);
        choice.setSelectedItem(controller.getSetting(key, fallback));
        style(choice);
        choice.addActionListener(event -> controller.setSetting(key, (String)choice.getSelectedItem()));
        parent.add(row(label, choice));
    }

    private JPanel row(String label, java.awt.Component control) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(true); row.setBackground(theme.panelRaised());
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(theme.grid()),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel title = new JLabel(label);
        title.setForeground(theme.foreground()); title.setFont(title.getFont().deriveFont(Font.BOLD, 11f));
        JPanel controlSlot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlSlot.setOpaque(false); controlSlot.add(control);
        row.add(title, BorderLayout.WEST); row.add(controlSlot, BorderLayout.EAST);
        return row;
    }

    private void style(JComboBox<?> choice) {
        choice.setBackground(theme.panel()); choice.setForeground(theme.foreground());
    }
    private void style(JToggleButton toggle) {
        toggle.setBackground(theme.panel()); toggle.setForeground(theme.accent()); toggle.setFocusPainted(false);
    }

}
