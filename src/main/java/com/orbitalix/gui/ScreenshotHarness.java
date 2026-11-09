package com.orbitalix.gui;

import com.orbitalix.api.FaultType;
import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.core.View;
import com.orbitalix.gui.theme.Themes;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Headless screenshot harness covers every view, theme, and required resolution. */
public final class ScreenshotHarness {
    private static final List<String> VIEWS = List.of("Planner", "Dashboard", "Cutaway", "Orbit",
            "Systems", "Crew", "Replay", "Console", "Reports", "Settings");
    private ScreenshotHarness() {}

    public static void capture(Path directory, SimulationController controller) throws IOException {
        Files.createDirectories(directory);
        SimulationController renderController = controller.fork();
        renderController.injectFault("Propulsion", FaultType.FUEL_LEAK);
        for (int i = 0; i < 24; i++) renderController.tick();
        int count = 0;
        for (Themes theme : Themes.all()) {
            for (String view : VIEWS) {
                for (int[] size : new int[][]{{1280, 720}, {1920, 1080}}) {
                    View panel = view.equals("Dashboard")
                            ? new DashboardView(renderController, theme)
                            : view.equals("Cutaway") ? new CutawayView(renderController, theme)
                            : view.equals("Orbit") ? new OrbitView(renderController, theme)
                            : view.equals("Planner") ? new PlannerView(renderController, theme)
                            : view.equals("Console") ? new ConsoleView(renderController, theme)
                            : view.equals("Settings") ? new SettingsView(renderController, theme, ignored -> {})
                            : view.equals("Systems") ? new SystemsView(renderController, theme)
                            : view.equals("Crew") ? new CrewView(renderController, theme)
                            : view.equals("Replay") ? new ReplayView(renderController, theme)
                            : view.equals("Reports") ? new ReportsView(renderController, theme)
                            : new MissionView(view, renderController, theme);
                    panel.setSize(size[0], size[1]);
                    layoutRecursively(panel);
                    panel.animateFrame(.5);
                    BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = image.createGraphics();
                    panel.paint(graphics);
                    graphics.dispose();
                    if (!hasVisualVariance(image)) throw new IOException("Blank screenshot: " + view + " / " + theme.displayName());
                    Path output = directory.resolve(view.toLowerCase() + "-" + theme.name().toLowerCase()
                            + "-" + size[0] + "x" + size[1] + ".png");
                    if (!ImageIO.write(image, "png", output.toFile())) throw new IOException("PNG writer unavailable");
                    panel.close();
                    count++;
                }

            }
        }
        System.out.println("Captured and verified " + count + " screenshots in " + directory.toAbsolutePath());
    }

    private static void layoutRecursively(Container container) {
        container.doLayout();
        for (java.awt.Component child : container.getComponents())
            if (child instanceof Container nested) layoutRecursively(nested);
    }

    private static boolean hasVisualVariance(BufferedImage image) {
        int first = image.getRGB(0, 0);
        for (int y = 0; y < image.getHeight(); y += 12)
            for (int x = 0; x < image.getWidth(); x += 12)
                if (different(first, image.getRGB(x, y))) return true;
        return false;
    }

    private static boolean different(int first, int current) {
        Color a = new Color(first, true), b = new Color(current, true);
        return Math.abs(a.getRed() - b.getRed()) + Math.abs(a.getGreen() - b.getGreen())
                + Math.abs(a.getBlue() - b.getBlue()) > 30;
    }
}
