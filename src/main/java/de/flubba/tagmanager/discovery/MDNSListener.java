package de.flubba.tagmanager.discovery;

import lombok.extern.slf4j.Slf4j;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

@Slf4j
public final class MDNSListener {
    private MDNSListener() {}

    public static void listen() {
        try {
            JmDNS jmDNS = JmDNS.create();
            jmDNS.addServiceListener("_ltc-rallye._tcp.local.", new EventPublishingListener());
        } catch (IOException e) {
            throw new UncheckedIOException("There is a problem with mDNS.", e);
        }
    }

    private static final class EventPublishingListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            // not needed, serviceResolved() is sufficient
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            log.warn("Backend removed from mDNS");
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            if (event.getInfo().getHostAddresses().length > 0) {
                if (event.getInfo().getHostAddresses().length > 1) {
                    log.warn("More than one host address found: {}", Arrays.stream(event.getInfo().getHostAddresses()).toList());
                }
                var backendDiscoveredEvent = new BackendDiscoveredEvent(event.getInfo().getHostAddresses()[0], event.getInfo().getPort());
                log.info("Backend discovered: {}:{}", backendDiscoveredEvent.server(), backendDiscoveredEvent.port());
                BackendDiscoveredEventPublisher.publish(backendDiscoveredEvent);
            } else {
                log.error("Could not Service Resolved Event - no address found");
            }
        }
    }
}
