package com.orbitalix.event;

import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.MissionEventListener;
import java.io.PrintStream;
import java.util.Objects;

public final class ConsoleEventListener implements MissionEventListener {
    private final PrintStream output;
    public ConsoleEventListener() { this(System.out); }
    public ConsoleEventListener(PrintStream output) { this.output = Objects.requireNonNull(output); }
    @Override public void onEvent(MissionEvent event) {
        output.printf("[T+%d][%s][%s] %s%n", event.tick(), event.severity(), event.type(), event.message());
    }
}
