package com.orbitalix.gui.core;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/** Installs the mission-control global keyboard bindings on a root component. */
public final class KeyboardShortcuts {
    private KeyboardShortcuts() {}

    public static void install(JComponent root, List<String> views, Runnable play,
            Consumer<String> openView, Runnable focusConsole, Runnable screenshot,
            Runnable fullScreen, Runnable clearCommand) {
        Objects.requireNonNull(root);
        if (views.size() != 10) throw new IllegalArgumentException("Exactly ten views are required");
        InputMap input = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = root.getActionMap();
        bind(input, actions, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "play", play);
        for (int digit = 0; digit < 10; digit++) {
            int viewIndex = digit;
            bind(input, actions, KeyStroke.getKeyStroke(KeyEvent.VK_0 + digit, 0), "view" + digit,
                    () -> openView.accept(views.get(viewIndex)));
        }
        bind(input, actions, KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_DOWN_MASK),
                "focusConsole", focusConsole);
        bind(input, actions, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                "screenshot", screenshot);
        bind(input, actions, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "fullscreen", fullScreen);
        bind(input, actions, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape", clearCommand);
    }

    private static void bind(InputMap input, ActionMap actions, KeyStroke key, String name, Runnable callback) {
        input.put(key, name);
        actions.put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { callback.run(); }
        });
    }
}
