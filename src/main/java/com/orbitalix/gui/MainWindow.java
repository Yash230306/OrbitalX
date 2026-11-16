package com.orbitalix.gui;

import com.orbitalix.api.AutonomyMode;
import com.orbitalix.api.MissionEventListener;
import com.orbitalix.api.SimulationController;
import com.orbitalix.api.EventType;
import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.Severity;
import com.orbitalix.gui.core.View;
import com.orbitalix.gui.core.ViewRegistry;
import com.orbitalix.gui.core.SplashScreen;
import com.orbitalix.gui.core.BottomTicker;
import com.orbitalix.gui.core.KeyboardShortcuts;
import com.orbitalix.gui.kit.ToastLayer;
import com.orbitalix.gui.audio.SoundPlayer;
import com.orbitalix.gui.audio.SynthSoundPlayer;
import com.orbitalix.gui.theme.Themes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/** Custom-painted Swing mission-control shell; all simulation access crosses the public API. */
public final class MainWindow extends JFrame {
    private static final List<String> VIEWS = List.of("Planner", "Dashboard", "Cutaway", "Orbit",
            "Systems", "Crew", "Replay", "Console", "Reports", "Settings");
    private final SimulationController controller;
    private final ViewRegistry viewRegistry = new ViewRegistry();
    private final Timer simulationTimer;
    private final Timer animationTimer;
    private final SoundPlayer soundPlayer;
    private final MissionEffectsOverlay effectsOverlay;
    private int shakeX, shakeY;
    private final JLayeredPane stage = new JLayeredPane() {
        @Override public void doLayout() {
            for (Component component : getComponents()) {
                int x = component == activeView ? shakeX : 0;
                int y = component == activeView ? shakeY : 0;
                component.setBounds(x, y, getWidth(), getHeight());
            }
        }
    };
    private final ToastLayer toastLayer = new ToastLayer();
    private final JLabel status = new JLabel("SYSTEM NOMINAL");
    private JLabel replayIndicator;
    private Themes theme;
    private String selectedView = "Dashboard";
    private int speed = 2;
    private boolean fullScreen;
    private long lastAnimationNanos = System.nanoTime();
    private final JTextField commandField = new JTextField();
    private View activeView;
    private SplashScreen splashScreen;
    private JPanel root;
    private NavRail navRail;
    private BottomTicker bottomTicker;
    private MissionEventListener toastListener;

    public MainWindow(SimulationController controller) {
        super("OrbitalX — Space Mission Simulator");
        this.controller = controller;
        soundPlayer = SynthSoundPlayer.createDefault();
        effectsOverlay = new MissionEffectsOverlay(controller);
        this.theme = readTheme();
        for (String view : VIEWS) {
            if (view.equals("Dashboard")) viewRegistry.register(view, () -> new DashboardView(controller, theme));
            else if (view.equals("Cutaway")) viewRegistry.register(view, () -> new CutawayView(controller, theme));
            else if (view.equals("Orbit")) viewRegistry.register(view, () -> new OrbitView(controller, theme));
            else if (view.equals("Planner")) viewRegistry.register(view, () -> new PlannerView(controller, theme));
            else if (view.equals("Console")) viewRegistry.register(view, () -> new ConsoleView(controller, theme));
            else if (view.equals("Settings")) viewRegistry.register(view, () -> new SettingsView(controller, theme, this::selectTheme));
            else if (view.equals("Systems")) viewRegistry.register(view, () -> new SystemsView(controller, theme));
            else if (view.equals("Crew")) viewRegistry.register(view, () -> new CrewView(controller, theme));
            else if (view.equals("Replay")) viewRegistry.register(view, () -> new ReplayView(controller, theme));
            else if (view.equals("Reports")) viewRegistry.register(view, () -> new ReportsView(controller, theme));
            else viewRegistry.register(view, () -> new MissionView(view, controller, theme));
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));
        setSize(1500, 920);
        setLocationRelativeTo(null);

        stage.setLayout(null);
        stage.setOpaque(true);
        stage.setBackground(theme.background());
        root = createShell();
        setContentPane(root);
        showView(selectedView);
        installShortcuts(root);
        splashScreen = new SplashScreen(theme);
        stage.remove(activeView);
        stage.add(splashScreen, JLayeredPane.DRAG_LAYER);
        SplashScreen openingSplash = splashScreen;
        Timer splashTimer = new Timer(1100, event -> {
            if (splashScreen == openingSplash) {
                stage.remove(openingSplash);
                splashScreen = null;
                stage.add(activeView, JLayeredPane.DEFAULT_LAYER);
                stage.revalidate(); stage.repaint();
            }
        });
        splashTimer.setRepeats(false);
        splashTimer.start();
        toastListener = event -> {
            effectsOverlay.trigger(event);
            playEventSound(event);
            java.awt.Color color = switch (event.severity()) {
                case INFO -> theme.accent();
                case LOW, MEDIUM -> theme.warning();
                case HIGH -> theme.critical();
                case CRITICAL -> theme.critical();
            };
            toastLayer.showToast(event.message(), color, 2400);
        };
        controller.addEventListener(toastListener);

        simulationTimer = new Timer(1000 / speed, event -> {
            if (controller.isRunning()) controller.tick();
        });
        simulationTimer.start();
        animationTimer = new Timer(16, event -> {
            long now = System.nanoTime();
            double elapsed = Math.min(.1, (now - lastAnimationNanos) / 1_000_000_000d);
            if (activeView != null) activeView.animateFrame(elapsed);
            effectsOverlay.advanceFrame(elapsed);
            lastAnimationNanos = now;
            shakeX = effectsOverlay.shakeOffsetX();
            shakeY = effectsOverlay.shakeOffsetY();
            stage.doLayout();
            if (replayIndicator != null) replayIndicator.setVisible(controller.snapshot().replaying());
            root.repaint();
            if (activeView != null) activeView.repaint();
        });
        animationTimer.start();
    }

    private Themes readTheme() {
        String saved = controller.getSetting("theme", "DARK_MISSION");
        try { return Themes.valueOf(saved); }
        catch (IllegalArgumentException ex) { return Themes.DARK_MISSION; }
    }

    private void showView(String name) {
        selectedView = name;
        if (activeView != null) {
            activeView.close();
            stage.remove(activeView);
        }
        if (splashScreen != null) {
            stage.remove(splashScreen);
            splashScreen = null;
        }
        activeView = viewRegistry.create(name);
        stage.add(activeView, JLayeredPane.DEFAULT_LAYER);
        if (effectsOverlay.getParent() != stage) stage.add(effectsOverlay, JLayeredPane.PALETTE_LAYER);
        if (toastLayer.getParent() != stage) stage.add(toastLayer, JLayeredPane.PALETTE_LAYER);
        if (navRail != null) navRail.repaint();
        stage.revalidate();
        stage.repaint();
        revalidate();
        repaint();
    }

    private JPanel createShell() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(theme.background());
        root.add(new TopBar(), BorderLayout.NORTH);
        navRail = new NavRail();
        root.add(navRail, BorderLayout.WEST);
        root.add(stage, BorderLayout.CENTER);
        root.add(new Footer(), BorderLayout.SOUTH);
        return root;
    }

    private void cycleTheme() {
        selectTheme(Themes.values()[(theme.ordinal() + 1) % Themes.values().length]);
    }

    private void selectTheme(Themes selected) {
        theme = selected;
        controller.setSetting("theme", theme.name());
        if (bottomTicker != null) bottomTicker.close();
        root = createShell();
        setContentPane(root);
        showView(selectedView);
        installShortcuts(root);
    }

    private void toggleSelfHealing() {
        controller.setSelfHealing(!controller.snapshot().selfHealing());
        status.setText("SELF-HEALING " + (controller.snapshot().selfHealing() ? "ENABLED" : "DISABLED"));
    }

    private void toggleAutonomy() {
        AutonomyMode[] modes = AutonomyMode.values();
        int next = (controller.snapshot().autonomyMode().ordinal() + 1) % modes.length;
        controller.setAutonomyMode(modes[next]);
        status.setText("AUTONOMY " + modes[next]);
    }

    private void toggleRun() {
        if (controller.snapshot().mission().ended()) controller.reset();
        if (controller.isRunning()) {
            controller.pause();
            status.setText("MISSION PAUSED");
        } else {
            controller.start();
            status.setText("MISSION RUNNING");
        }
        repaint();
    }

    private void runCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) return;
        String output = controller.executeCommand(command);
        status.setText(output.isBlank() ? "COMMAND ACCEPTED" : output);
        commandField.setText("");
        if (selectedView.equals("Console")) showView("Console");
    }

    private void exportReport(boolean html) {
        try {
            Path folder = Path.of("out");
            Files.createDirectories(folder);
            Path destination = folder.resolve(html ? "mission-report.html" : "telemetry.csv");
            Files.writeString(destination, html ? controller.generateReportHtml() : controller.exportTelemetryCsv());
            status.setText("EXPORTED " + destination.toAbsolutePath());
        } catch (IOException e) {
            status.setText("EXPORT FAILED: " + e.getMessage());
        }
    }

    private void playClick() {
        if (Boolean.parseBoolean(controller.getSetting("sound", "true"))) soundPlayer.click();
    }

    private void playEventSound(MissionEvent event) {
        if (!Boolean.parseBoolean(controller.getSetting("sound", "true"))) return;
        if (event.type() == EventType.ACHIEVEMENT || event.type() == EventType.MISSION_END) soundPlayer.chime();
        else if (event.severity() == Severity.HIGH || event.severity() == Severity.CRITICAL) soundPlayer.alarm();
        else if (event.severity() == Severity.LOW || event.severity() == Severity.MEDIUM) soundPlayer.caution();
    }

    private void toggleFullScreen() {
        fullScreen = !fullScreen;
        super.dispose();
        setUndecorated(fullScreen);
        setVisible(true);
    }

    private void installShortcuts(JComponent root) {
        KeyboardShortcuts.install(root, VIEWS, this::toggleRun, this::showView,
                () -> { showView("Console"); commandField.requestFocusInWindow(); },
                () -> {
                    try {
                        ScreenshotHarness.capture(Path.of("out", "screenshots"), controller);
                        status.setText("SCREENSHOTS SAVED TO out\\screenshots");
                    } catch (IOException ex) {
                        status.setText("SCREENSHOT FAILED: " + ex.getMessage());
                    }
                }, this::toggleFullScreen, () -> commandField.setText(""));
    }

    private final class TopBar extends JPanel {
        private TopBar() {
            setLayout(new BorderLayout());
            setBackground(theme.panel());
            setBorder(new EmptyBorder(8, 18, 8, 18));

            JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            brandPanel.setOpaque(false);

            JLabel logo = new JLabel("ORBITALX");
            logo.setForeground(theme.accent());
            logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
            brandPanel.add(logo);

            JLabel subLogo = new JLabel("// MISSION CONTROL");
            subLogo.setForeground(theme.foreground());
            subLogo.setFont(new Font("Segoe UI", Font.BOLD, 13));
            brandPanel.add(subLogo);

            add(brandPanel, BorderLayout.WEST);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            controls.setOpaque(false);
            replayIndicator = new JLabel("● REPLAY ACTIVE");
            replayIndicator.setOpaque(true);
            replayIndicator.setForeground(theme.background());
            replayIndicator.setBackground(theme.warning());
            replayIndicator.setFont(new Font("Segoe UI", Font.BOLD, 11));
            replayIndicator.setBorder(new EmptyBorder(5, 9, 5, 9));
            replayIndicator.setVisible(controller.snapshot().replaying());
            controls.add(replayIndicator);

            addButton(controls, "LAUNCH", MainWindow.this::toggleRun);
            addButton(controls, "AUTONOMY", MainWindow.this::toggleAutonomy);
            addButton(controls, "SELF-HEAL", MainWindow.this::toggleSelfHealing);
            addButton(controls, "THEME", MainWindow.this::cycleTheme);
            addButton(controls, "HTML", () -> exportReport(true));
            addButton(controls, "CSV", () -> exportReport(false));
            addButton(controls, speed + " T/S", () -> {
                speed = speed == 20 ? 1 : speed + 1;
                controller.setSpeed(speed);
                simulationTimer.setDelay(1000 / speed);
                ((JButton) controls.getComponent(controls.getComponentCount() - 1)).setText(speed + " T/S");
            });
            add(controls, BorderLayout.EAST);
        }

        private void addButton(JPanel parent, String text, Runnable action) {
            JButton button = new JButton(text) {
                @Override protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isArmed()) {
                        g.setColor(theme.accent());
                    } else if (getModel().isRollover()) {
                        g.setColor(new Color(theme.panelRaised().getRed(), theme.panelRaised().getGreen(), theme.panelRaised().getBlue(), 240));
                    } else {
                        g.setColor(theme.panelRaised());
                    }
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g.setColor(getModel().isRollover() ? theme.accent() : theme.grid());
                    g.setStroke(new BasicStroke(1));
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                    g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g.getFontMetrics();
                    g.setColor(getModel().isArmed() ? theme.background() : theme.foreground());
                    g.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g.dispose();
                }
            };
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setPreferredSize(new Dimension(button.getPreferredSize().width + 16, 32));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.addActionListener(event -> { playClick(); action.run(); });
            parent.add(button);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(theme.grid()); g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        }
    }

    private final class NavRail extends JPanel {
        private NavRail() {
            setLayout(new GridLayout(VIEWS.size(), 1, 0, 4));
            setBackground(theme.panel());
            setBorder(new EmptyBorder(12, 8, 12, 8));
            setPreferredSize(new Dimension(185, 0));
            for (int i = 0; i < VIEWS.size(); i++) {
                final String view = VIEWS.get(i);
                final int hotkey = (i + 1) % 10;
                JButton button = new JButton() {
                    @Override protected void paintComponent(Graphics graphics) {
                        Graphics2D g = (Graphics2D) graphics.create();
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        boolean isCurrent = view.equalsIgnoreCase(selectedView);
                        if (isCurrent) {
                            g.setColor(new Color(theme.panelRaised().getRed(), theme.panelRaised().getGreen(), theme.panelRaised().getBlue(), 220));
                            g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                            // Glowing accent highlight fill
                            g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 32));
                            g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                            // Neon active indicator bar
                            g.setColor(theme.accent());
                            g.fillRoundRect(3, 4, 4, getHeight() - 8, 3, 3);
                            // Outer active border
                            g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 85));
                            g.setStroke(new BasicStroke(1f));
                            g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
                        } else if (getModel().isRollover()) {
                            g.setColor(new Color(theme.grid().getRed(), theme.grid().getGreen(), theme.grid().getBlue(), 75));
                            g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                            g.setColor(new Color(theme.accent().getRed(), theme.accent().getGreen(), theme.accent().getBlue(), 40));
                            g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
                        }
                        g.setColor(isCurrent ? theme.accent() : theme.muted());
                        g.setFont(new Font("Consolas", Font.BOLD, 10));
                        g.drawString("[" + hotkey + "]", 12, getHeight() / 2 + 4);

                        g.setColor(isCurrent ? theme.foreground() : (getModel().isRollover() ? theme.foreground() : theme.muted()));
                        g.setFont(new Font("Segoe UI", isCurrent ? Font.BOLD : Font.PLAIN, 12));
                        g.drawString(view.toUpperCase(Locale.ROOT), 42, getHeight() / 2 + 4);
                        g.dispose();
                    }
                };
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(false);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.addActionListener(event -> { playClick(); showView(view); });
                add(button);
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(theme.grid()); g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        }
    }

    private final class Footer extends JPanel {
        private Footer() {
            setLayout(new BorderLayout(0, 3));
            setBackground(theme.panel());
            setBorder(new EmptyBorder(4, 10, 8, 10));
            status.setForeground(theme.nominal());
            status.setFont(new Font("Segoe UI", Font.BOLD, 11));
            bottomTicker = new BottomTicker(controller, theme);
            add(bottomTicker, BorderLayout.NORTH);
            JPanel controls = new JPanel(new BorderLayout(12, 0));
            controls.setOpaque(false);
            controls.setBorder(new EmptyBorder(2, 6, 2, 6));

            JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            statusWrap.setOpaque(false);
            statusWrap.add(status);
            controls.add(statusWrap, BorderLayout.WEST);

            commandField.setPreferredSize(new Dimension(500, 34));
            commandField.setBackground(theme.background());
            commandField.setForeground(theme.foreground());
            commandField.setCaretColor(theme.accent());
            commandField.setFont(new Font("Consolas", Font.PLAIN, 12));
            commandField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(theme.grid()),
                    new EmptyBorder(4, 10, 4, 10)));
            commandField.setToolTipText("Enter onboard command (help for command list)");
            commandField.addActionListener(event -> runCommand());
            controls.add(commandField, BorderLayout.EAST);

            JButton execute = new JButton("SEND") {
                @Override protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(getModel().isRollover() ? theme.accent() : theme.panelRaised());
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g.setColor(theme.grid());
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g.setColor(getModel().isRollover() && theme.displayName().equals("Projector Light") ? Color.WHITE : theme.foreground());
                    g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString("SEND", (getWidth() - fm.stringWidth("SEND")) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g.dispose();
                }
            };
            execute.setFocusPainted(false);
            execute.setBorderPainted(false);
            execute.setContentAreaFilled(false);
            execute.setOpaque(false);
            execute.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            execute.addActionListener(event -> { playClick(); runCommand(); });
            controls.add(execute, BorderLayout.CENTER);
            add(controls, BorderLayout.CENTER);
        }
    }

    @Override public void dispose() {
        if (simulationTimer != null) simulationTimer.stop();
        if (animationTimer != null) animationTimer.stop();
        if (activeView != null) activeView.close();
        if (bottomTicker != null) bottomTicker.close();
        if (toastListener != null) controller.removeEventListener(toastListener);
        soundPlayer.close();
        super.dispose();
    }
}
