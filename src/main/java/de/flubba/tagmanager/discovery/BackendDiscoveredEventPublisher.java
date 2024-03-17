package de.flubba.tagmanager.discovery;

import lombok.Getter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class BackendDiscoveredEventPublisher {
    @Getter
    private static Optional<BackendDiscoveredEvent> lastBackendDiscoveredEvent = Optional.empty();
    private static final Set<BackendDiscoveredEventListener> LISTENERS = new HashSet<>();

    private BackendDiscoveredEventPublisher() {}

    public static void register(BackendDiscoveredEventListener newListener) {
        LISTENERS.add(newListener);
    }

    public static void publish(BackendDiscoveredEvent backendDiscoveredEvent) {
        lastBackendDiscoveredEvent = Optional.of(backendDiscoveredEvent);
        LISTENERS.forEach(listener -> {
            listener.listen(backendDiscoveredEvent);
        });
    }
}
