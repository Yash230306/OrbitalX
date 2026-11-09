package com.orbitalix.console;

import java.util.Objects;

public final class CommandParser {
    private final CommandRegistry registry;
    public CommandParser(CommandRegistry registry) { this.registry = Objects.requireNonNull(registry); }
    public String execute(String input) {
        if (input == null || input.isBlank()) return "Enter a command. Type help.";
        String normalized = input.stripLeading();
        int end = 0;
        while (end < normalized.length() && !Character.isWhitespace(normalized.charAt(end))) end++;
        if (end == 0) return "Enter a command. Type help.";
        return registry.execute(normalized.substring(0, end), input);
    }
}
