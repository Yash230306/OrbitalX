package com.orbitalix.gui.kit;

import javax.swing.JComponent;

public final class Tooltip {
    private Tooltip() {}
    public static void install(JComponent component, String text) {
        if (component == null || text == null || text.isBlank()) throw new IllegalArgumentException("Component and tooltip text are required");
        component.setToolTipText(text);
        component.getAccessibleContext().setAccessibleDescription(text);
    }
}
