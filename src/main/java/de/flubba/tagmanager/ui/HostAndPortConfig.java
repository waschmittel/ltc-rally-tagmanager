package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.discovery.BackendDiscoveredEvent;
import de.flubba.tagmanager.discovery.BackendDiscoveredEventListener;
import de.flubba.tagmanager.discovery.BackendDiscoveredEventPublisher;
import de.flubba.tagmanager.smartcard.ServerCommunication;

import java.awt.Color;
import java.util.prefs.Preferences;

import static de.flubba.tagmanager.util.SingleCallbackDocumentListenerBuilder.onEveryEvent;

public class HostAndPortConfig extends HostAndPortConfigLayout implements BackendDiscoveredEventListener {
    private static final Color INVALID_BACKGROUND = new Color(255, 160, 160);
    private static final Preferences PREFS = Preferences.userNodeForPackage(HostAndPortConfig.class);
    private static final String LAST_HOSTNAME_KEY = "lastHostname";
    private static final String LAST_PORT_KEY = "lastPort";

    public HostAndPortConfig() {
        super();
        loadSavedHostAndPort();
        hostField.getDocument().addDocumentListener(onEveryEvent(this::updateServerConfig));
        portField.getDocument().addDocumentListener(onEveryEvent(this::updateServerConfig));
        BackendDiscoveredEventPublisher.register(this);
        BackendDiscoveredEventPublisher.getLastBackendDiscoveredEvent().ifPresent(this::listen);
        updateServerConfig();
    }

    @Override
    public void listen(BackendDiscoveredEvent backendDiscoveredEvent) {
        portField.setText(Integer.toString(backendDiscoveredEvent.port()));
        hostField.setText(backendDiscoveredEvent.server());
    }

    private void loadSavedHostAndPort() {
        String savedHostname = PREFS.get(LAST_HOSTNAME_KEY, "localhost");
        String savedPort = PREFS.get(LAST_PORT_KEY, "8080");
        hostField.setText(savedHostname);
        portField.setText(savedPort);
    }

    private void updateServerConfig() {
        String hostname = hostField.getText();
        Integer port = getPort();
        ServerCommunication.setHostAndPort(hostname, port);
        if (hostname != null && port != null) {
            PREFS.put(LAST_HOSTNAME_KEY, hostname);
            PREFS.put(LAST_PORT_KEY, port.toString());
        }
    }

    private Integer getPort() {
        try {
            int port = Integer.parseInt(portField.getText());
            if (port < 80 || port > 65535) {
                throw new IllegalArgumentException();
            }
            portField.setBackground(defaultBackground);
            portField.setToolTipText(null);
            return port;
        } catch (IllegalArgumentException e) {
            portField.setBackground(INVALID_BACKGROUND);
            portField.setToolTipText("this is not a valid port");
            return null;
        }
    }
}
