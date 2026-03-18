package de.flubba.tagmanager.discovery;

import lombok.extern.slf4j.Slf4j;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

@Slf4j
public final class MDNSListener {
    private MDNSListener() {}

    public static void listen() {
        try {
            var jmDNS = JmDNS.create(getIPv4Address());
            jmDNS.addServiceListener("_ltc-rallye._tcp.flubba.", new EventPublishingListener());
        } catch (IOException e) {
            throw new UncheckedIOException("There is a problem with mDNS.", e);
        }
    }

    private static InetAddress getIPv4Address() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        var ipv4s = new ArrayList<Inet4Address>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback()
                    || !networkInterface.isUp()
                    || networkInterface.isVirtual()
                    || networkInterface.getName().startsWith("bridge") // on macOS bridge is virtual, but isVirtual() returns false for some readon
            ) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    ipv4s.add((Inet4Address) address);
                }
            }
        }

        ipv4s.forEach(v4 -> log.info("Found IPv4 address for mDNS: {}", v4.getHostAddress()));

        return ipv4s.stream().findFirst().orElseThrow(() -> new IOException("No IPv4 address found"));
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
