package com.orbitalix.comms;

import java.util.Objects;

public record QueuedCommand(String text, int ticksRemaining) {
    public QueuedCommand {
        Objects.requireNonNull(text);
        if (ticksRemaining < 1) throw new IllegalArgumentException("Queued command needs a positive delay");
    }
}
