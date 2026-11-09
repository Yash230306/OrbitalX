package com.orbitalix.gui;

import com.orbitalix.api.MissionConfig;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.core.View;
import com.orbitalix.gui.theme.Theme;
import com.orbitalix.config.CraftType;
import com.orbitalix.config.Destination;
import com.orbitalix.config.Difficulty;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.concurrent.ThreadLocalRandom;

/** Mission configuration planner with a validated seed and launch countdown. */
public final class PlannerView extends View {
    private final JComboBox<Destination> destination = new JComboBox<>(Destination.values());
    private final JComboBox<CraftType> craft = new JComboBox<>(CraftType.values());
    private final JComboBox<Difficulty> difficulty = new JComboBox<>(Difficulty.values());
    private final JComboBox<String> scenario;
    private final JTextField seed = new JTextField();
    private final JLabel brief = new JLabel();
    private final JButton launch = new JButton("LAUNCH MISSION");
    private final javax.swing.JToggleButton chaos = new javax.swing.JToggleButton("CHAOS DIRECTOR OFF");
    private Timer countdown;
    private int countdownValue;

    public PlannerView(SimulationController controller, Theme theme) {
        super(controller, theme);
        scenario = new JComboBox<>(controller.getScenarioNames().toArray(String[]::new));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.setOpaque(false);
        JLabel title = new JLabel("MISSION PLANNER");
        title.setForeground(theme.foreground()); title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        JLabel subtitle = new JLabel("Configure trajectory, vehicle, mission conditions, and deterministic scenario.");
        subtitle.setForeground(theme.muted());
        heading.add(title); heading.add(subtitle);
        add(heading, BorderLayout.NORTH);
        style(destination); style(craft); style(difficulty); style(scenario);
        seed.setText(Long.toString(snapshot.config().seed()));
        seed.setBackground(theme.panel()); seed.setForeground(theme.foreground());
        seed.setCaretColor(theme.accent()); seed.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.grid()), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        destination.setSelectedItem(snapshot.config().destination());
        craft.setSelectedItem(snapshot.config().craftType());
        difficulty.setSelectedItem(snapshot.config().difficulty());
        scenario.setSelectedIndex(Math.min(snapshot.config().scenarioIndex(), scenario.getItemCount() - 1));

        JPanel options = new JPanel(new GridLayout(2, 2, 14, 14));
        options.setOpaque(false);
        options.add(option("DESTINATION", destination));
        options.add(option("SPACECRAFT", craft));
        options.add(option("DIFFICULTY", difficulty));
        options.add(option("SCRIPTED SCENARIO", scenario));
        JPanel seedPanel = option("DETERMINISTIC SEED", seed);
        JButton dice = new JButton("ROLL DICE");
        style(dice);
        dice.addActionListener(event -> seed.setText(Long.toString(ThreadLocalRandom.current().nextLong(1_000_000_000L))));
        seedPanel.add(dice, BorderLayout.EAST);

        chaos.setSelected(snapshot.config().chaosDirector());
        style(chaos);
        chaos.addActionListener(event -> {
            chaos.setText("CHAOS DIRECTOR " + (chaos.isSelected() ? "ON" : "OFF"));
            updateBrief();
        });
        JPanel lower = new JPanel(new BorderLayout(16, 0));
        lower.setOpaque(false);
        lower.add(seedPanel, BorderLayout.CENTER);
        lower.add(chaos, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(options, BorderLayout.CENTER);
        content.add(lower, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        brief.setForeground(theme.muted());
        brief.setFont(brief.getFont().deriveFont(Font.PLAIN, 13f));
        brief.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        footer.add(brief, BorderLayout.CENTER);
        launch.setForeground(theme.foreground()); launch.setBackground(theme.accent());
        launch.setFocusPainted(false); launch.setFont(launch.getFont().deriveFont(Font.BOLD, 14f));
        launch.setMargin(new Insets(14, 26, 14, 26));
        launch.addActionListener(event -> beginLaunch());
        footer.add(launch, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
        java.awt.event.ActionListener updateBrief = event -> updateBrief();
        destination.addActionListener(updateBrief); craft.addActionListener(updateBrief);
        difficulty.addActionListener(updateBrief); scenario.addActionListener(updateBrief);
        updateBrief();
    }

    public JTextField seedField() { return seed; }
    public JButton launchButton() { return launch; }
    public JComboBox<Destination> destinationSelector() { return destination; }
    public JComboBox<CraftType> craftSelector() { return craft; }
    public JComboBox<Difficulty> difficultySelector() { return difficulty; }
    public JComboBox<String> scenarioSelector() { return scenario; }

    @Override protected void paintView(Graphics2D g) {
        g.setColor(theme.background()); g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(theme.panel()); g.fillRoundRect(0, 90, getWidth(), getHeight() - 97, 18, 18);
        g.setColor(theme.grid()); g.drawRoundRect(0, 90, getWidth(), getHeight() - 97, 18, 18);
        if (countdownValue > 0) {
            g.setColor(new Color(0, 0, 0, 160)); g.fillRoundRect(getWidth() / 2 - 100, getHeight() / 2 - 80, 200, 150, 18, 18);
            g.setColor(theme.accent()); g.setFont(new Font("Segoe UI", Font.BOLD, 64));
            String value = Integer.toString(countdownValue);
            g.drawString(value, getWidth() / 2 - g.getFontMetrics().stringWidth(value) / 2, getHeight() / 2 + 25);
        }
    }

    private JPanel option(String title, java.awt.Component component) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(true); panel.setBackground(theme.panelRaised());
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(theme.grid()),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel label = new JLabel(title);
        label.setForeground(theme.accent()); label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        JPanel control = new JPanel(new GridBagLayout());
        control.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; constraints.gridy = 0; constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        control.add(component, constraints);
        panel.add(label, BorderLayout.NORTH); panel.add(control, BorderLayout.CENTER);
        return panel;
    }

    private void style(JComboBox<?> combo) {
        combo.setForeground(theme.foreground()); combo.setBackground(theme.panel());
        combo.setFont(combo.getFont().deriveFont(Font.PLAIN, 14f));
    }
    private void style(JButton button) {
        button.setForeground(theme.foreground()); button.setBackground(theme.panelRaised());
        button.setFocusPainted(false);
    }
    private void style(javax.swing.JToggleButton button) {
        button.setForeground(theme.foreground()); button.setBackground(theme.panelRaised());
        button.setFocusPainted(false);
    }

    private void updateBrief() {
        Destination target = (Destination)destination.getSelectedItem();
        CraftType vehicle = (CraftType)craft.getSelectedItem();
        Difficulty level = (Difficulty)difficulty.getSelectedItem();
        String scenarioName = (String)scenario.getSelectedItem();
        if (target == null || vehicle == null || level == null || scenarioName == null) return;
        brief.setText(String.format(java.util.Locale.ROOT,
                "BRIEF  /  %s  /  %s  /  %s  /  %d CREW  /  %s SCENARIO  /  CHAOS %s",
                target.name().replace('_', ' '), vehicle.name().replace('_', ' '), level.name(),
                vehicle.crew(), scenarioName.toUpperCase(java.util.Locale.ROOT), chaos.isSelected() ? "ON" : "OFF"));
        brief.setToolTipText(target.profile() + " / " + level.guidance());
    }

    private void beginLaunch() {
        final long seedValue;
        try { seedValue = Long.parseLong(seed.getText().trim()); }
        catch (NumberFormatException ex) {
            seed.setBorder(BorderFactory.createLineBorder(theme.critical(), 2));
            brief.setText("INVALID SEED  /  ENTER A SIGNED 64-BIT INTEGER");
            brief.setForeground(theme.critical());
            return;
        }
        seed.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(theme.grid()),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        brief.setForeground(theme.muted());
        countdownValue = 3;
        launch.setEnabled(false);
        launch.setText("LAUNCHING IN 3...");
        if (countdown != null) countdown.stop();
        countdown = new Timer(900, this::countdownStep);
        countdown.start();
    }

    private void countdownStep(ActionEvent event) {
        countdownValue--;
        if (countdownValue > 0) {
            launch.setText("LAUNCHING IN " + countdownValue + "...");
            repaint();
            return;
        }
        countdown.stop();
        MissionConfig config = new MissionConfig((Destination)destination.getSelectedItem(),
                (CraftType)craft.getSelectedItem(), (Difficulty)difficulty.getSelectedItem(),
                Long.parseLong(seed.getText().trim()), chaos.isSelected(), scenario.getSelectedIndex());
        controller.configureMission(config);
        controller.start();
        launch.setEnabled(true);
        launch.setText("MISSION LAUNCHED");
        brief.setText("MISSION CONFIGURED  /  " + config.destination().name() + "  /  T+" + controller.snapshot().tick());
        repaint();
    }

    @Override public void close() {
        if (countdown != null) countdown.stop();
        super.close();
    }
}
