package com.orbitalix.comms;

import com.orbitalix.api.UplinkSnapshot;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class DirectChannel implements CommandChannel {
    @Override public void send(String command, int delay, Consumer<String> executor) {
        Objects.requireNonNull(command);
        Objects.requireNonNull(executor).accept(command);
    }
    @Override public void advance(Consumer<String> executor) {}
    @Override public List<UplinkSnapshot> queue() { return List.of(); }
    @Override public List<UplinkSnapshot> drain() { return List.of(); }
    @Override public DirectChannel copy() { return new DirectChannel(); }
}
