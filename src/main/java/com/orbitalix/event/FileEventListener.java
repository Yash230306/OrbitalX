package com.orbitalix.event;

import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.MissionEventListener;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Objects;

/** Appends events to a log file and surfaces I/O failures to the publisher. */
public final class FileEventListener implements MissionEventListener {
    private final Path path;
    public FileEventListener(Path path) { this.path = Objects.requireNonNull(path).toAbsolutePath(); }
    @Override public synchronized void onEvent(MissionEvent event) {
        String line = String.format("[T+%d][%s][%s] %s%n", event.tick(), event.severity(), event.type(), event.message());
        try {
            Files.writeString(path, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException failure) {
            throw new UncheckedIOException("Could not append mission event to " + path, failure);
        }
    }
}
