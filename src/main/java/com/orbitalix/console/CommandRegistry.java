package com.orbitalix.console;

import java.util.*;

public final class CommandRegistry {
    private final Map<String, ConsoleCommand> commands = new LinkedHashMap<>();
    public synchronized void register(String name, ConsoleCommand command) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Command name is required");
        if (commands.putIfAbsent(name.toLowerCase(Locale.ROOT), Objects.requireNonNull(command)) != null)
            throw new IllegalArgumentException("Command is already registered: " + name);
    }
    public synchronized String execute(String name, String input) {
        ConsoleCommand command = commands.get(name.toLowerCase(Locale.ROOT));
        if (command == null) throw new IllegalArgumentException("Unknown command: " + name);
        return command.execute(input);
    }
    public synchronized Set<String> names() { return Set.copyOf(commands.keySet()); }
}
