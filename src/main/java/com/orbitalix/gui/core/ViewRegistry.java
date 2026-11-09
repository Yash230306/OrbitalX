package com.orbitalix.gui.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Named view factories used by the shell and screenshot harness. */
public final class ViewRegistry {
    private final Map<String, Supplier<? extends View>> factories = new LinkedHashMap<>();

    public void register(String name, Supplier<? extends View> factory) {
        if (name == null || name.isBlank() || factory == null) throw new IllegalArgumentException("View name and factory are required");
        if (factories.putIfAbsent(name, factory) != null) throw new IllegalArgumentException("View already registered: " + name);
    }

    public View create(String name) {
        Supplier<? extends View> factory = factories.get(name);
        if (factory == null) throw new IllegalArgumentException("Unknown view: " + name);
        return factory.get();
    }

    public List<String> names() { return List.copyOf(factories.keySet()); }
}
