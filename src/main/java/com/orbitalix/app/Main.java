package com.orbitalix.app;

import com.orbitalix.api.*;
import com.orbitalix.config.*;
import com.orbitalix.gui.ScreenshotHarness;
import com.orbitalix.gui.MainWindow;
import com.orbitalix.event.MissionRecorder;
import com.orbitalix.simulation.DefaultSimulationController;
import com.orbitalix.simulation.SelfTest;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Locale;

/** Application entry point for the desktop mission-control GUI and CLI tools. */
public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--selftest")) {
            System.exit(SelfTest.run());
        } else if (args.length > 0 && args[0].equals("--screenshots")) {
            if (args.length < 2) throw new IllegalArgumentException("Usage: --screenshots <directory>");
            ScreenshotHarness.capture(Path.of(args[1]), new DefaultSimulationController());
        } else if (args.length > 0 && args[0].equals("--cli")) {
            runConsole();
        } else {
            SwingUtilities.invokeLater(() -> new MainWindow(new DefaultSimulationController()).setVisible(true));
        }
    }

    private static void runConsole() throws Exception {
        DefaultSimulationController controller = new DefaultSimulationController();
        try (MissionRecorder recorder = new MissionRecorder(Path.of("out", "mission-events.log"))) {
            controller.addEventListener(recorder);
            controller.addEventListener(event -> System.out.printf("[T+%d][%s] %s%n", event.tick(), event.severity(), event.message()));
            System.out.println("OrbitalX Mission Control CLI - type help, tick [n], or quit");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                String command = line.trim();
                if (command.equalsIgnoreCase("quit")) break;
                if (command.equalsIgnoreCase("tick") || command.toLowerCase(Locale.ROOT).startsWith("tick ")) {
                    try {
                        int count = command.equalsIgnoreCase("tick") ? 1 : Integer.parseInt(command.substring(5).trim());
                        if (count < 1) throw new IllegalArgumentException("Tick count must be at least 1");
                        for (int i = 0; i < count; i++) controller.tick();
                        System.out.println(controller.executeCommand("status"));
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                } else if (command.equalsIgnoreCase("report")) {
                    System.out.println(controller.executeCommand("report"));
                } else {
                    try {
                        System.out.println(controller.executeCommand(command));
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
        }
    }
}
