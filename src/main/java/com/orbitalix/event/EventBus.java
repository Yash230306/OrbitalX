package com.orbitalix.event;

import com.orbitalix.api.MissionEvent;
import com.orbitalix.util.Identifiable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/** Typed mediator and observer hub; subscription handles can detach listeners safely. */
public final class EventBus implements Identifiable {
    private static final AtomicLong NEXT_ID;
    static { NEXT_ID = new AtomicLong(); }
    private final long instanceId;
    { instanceId = NEXT_ID.incrementAndGet(); }

    public record Topic<T>(String name, Class<T> payloadType) {
        public Topic {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Topic name is required");
            Objects.requireNonNull(payloadType);
        }
    }
    public static final Topic<MissionEvent> MISSION_EVENTS = new Topic<>("mission-events", MissionEvent.class);
    private final Map<Topic<?>, CopyOnWriteArrayList<Consumer<?>>> listeners = new ConcurrentHashMap<>();
    private final Map<com.orbitalix.api.MissionEventListener, AutoCloseable> missionSubscriptions = new ConcurrentHashMap<>();

    @Override public String id() { return "event-bus-" + instanceId; }

    public <T> AutoCloseable subscribe(Topic<T> topic, Consumer<? super T> listener) {
        Objects.requireNonNull(topic);
        Objects.requireNonNull(listener);
        CopyOnWriteArrayList<Consumer<?>> subscribers = listeners.computeIfAbsent(topic, ignored -> new CopyOnWriteArrayList<>());
        subscribers.add(listener);
        return () -> subscribers.remove(listener);
    }

    public <T> void publish(Topic<T> topic, T event) {
        Objects.requireNonNull(topic);
        if (event == null || !topic.payloadType().isInstance(event))
            throw new IllegalArgumentException("Event does not match topic " + topic.name());
        for (Consumer<?> raw : listeners.getOrDefault(topic, new CopyOnWriteArrayList<>())) {
            @SuppressWarnings("unchecked")
            Consumer<T> listener = (Consumer<T>) raw;
            listener.accept(event);
        }
    }

    public void addListener(com.orbitalix.api.MissionEventListener listener) {
        Objects.requireNonNull(listener);
        missionSubscriptions.computeIfAbsent(listener, value -> subscribe(MISSION_EVENTS, value::onEvent));
    }
    public void removeListener(com.orbitalix.api.MissionEventListener listener) {
        AutoCloseable subscription = missionSubscriptions.remove(listener);
        if (subscription == null) return;
        try { subscription.close(); }
        catch (Exception ex) { throw new IllegalStateException("Could not remove mission event listener", ex); }
    }
    public int subscriberCount(Topic<?> topic) {
        return listeners.getOrDefault(topic, new CopyOnWriteArrayList<>()).size();
    }
}
