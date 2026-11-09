package com.orbitalix.console;

@FunctionalInterface
public interface ConsoleCommand {
    String execute(String input);
}
