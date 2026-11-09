package com.orbitalix.event;

import com.orbitalix.api.MissionEvent;
import com.orbitalix.api.MissionEventListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/** Appends mission events to a line-oriented flight recording. */
public final class MissionRecorder implements MissionEventListener, AutoCloseable {
    private final BufferedWriter writer;

    public MissionRecorder(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override public synchronized void onEvent(MissionEvent event) {
        Objects.requireNonNull(event, "event");
        try {
            writer.write("T+" + event.tick() + " [" + event.severity() + "] " + event.message()
                    .replace('\r', ' ').replace('\n', ' '));
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            throw new UncheckedIOException("Unable to record mission event", ex);
        }
    }

    @Override public synchronized void close() throws IOException { writer.close(); }
}
