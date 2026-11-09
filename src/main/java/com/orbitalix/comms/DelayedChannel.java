package com.orbitalix.comms;

import com.orbitalix.api.UplinkSnapshot;
import java.util.*;
import java.util.function.Consumer;

/** DelayedChannel proxies commands through a FIFO uplink with a per-command countdown. */
public final class DelayedChannel implements CommandChannel {
    private final ArrayDeque<QueuedCommand> commands = new ArrayDeque<>();
    public DelayedChannel() {}
    private DelayedChannel(Collection<UplinkSnapshot> pending) {
        pending.forEach(item -> commands.addLast(new QueuedCommand(item.commandText(), item.ticksUntilExecution())));
    }
    @Override public synchronized void send(String command, int delay, Consumer<String> executor) {
        Objects.requireNonNull(command);
        Objects.requireNonNull(executor);
        if (delay < 1) throw new IllegalArgumentException("Delayed uplink must be at least one tick");
        commands.addLast(new QueuedCommand(command, delay));
    }
    @Override public synchronized void advance(Consumer<String> executor) {
        Objects.requireNonNull(executor);
        int ready = commands.size();
        for (int i = 0; i < ready; i++) {
            QueuedCommand command = commands.removeFirst();
            if (command.ticksRemaining() <= 1) executor.accept(command.text());
            else commands.addLast(new QueuedCommand(command.text(), command.ticksRemaining() - 1));
        }
    }
    @Override public synchronized List<UplinkSnapshot> queue() {
        return commands.stream().map(command -> new UplinkSnapshot(command.text(), command.ticksRemaining())).toList();
    }
    @Override public synchronized DelayedChannel copy() { return new DelayedChannel(queue()); }
    @Override public synchronized List<UplinkSnapshot> drain() {
        List<UplinkSnapshot> pending = queue();
        commands.clear();
        return pending;
    }
}
