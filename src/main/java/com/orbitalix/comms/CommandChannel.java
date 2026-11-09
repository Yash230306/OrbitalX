package com.orbitalix.comms;

import com.orbitalix.api.UplinkSnapshot;
import java.util.List;
import java.util.function.Consumer;

public interface CommandChannel {
    void send(String command, int delay, Consumer<String> executor);
    void advance(Consumer<String> executor);
    List<UplinkSnapshot> queue();
    List<UplinkSnapshot> drain();
    CommandChannel copy();
}
