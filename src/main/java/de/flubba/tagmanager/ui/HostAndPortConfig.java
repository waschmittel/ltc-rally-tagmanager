package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.discovery.BackendDiscoveredEvent;
import de.flubba.tagmanager.discovery.BackendDiscoveredEventListener;
import de.flubba.tagmanager.discovery.BackendDiscoveredEventPublisher;
import de.flubba.tagmanager.smartcard.ServerCommunication;
import de.flubba.tagmanager.util.SimpleDocumentListener;

import java.awt.Color;

public class HostAndPortConfig extends HostAndPortConfigLayout implements BackendDiscoveredEventListener {
    private static final Color INVALID_BACKGROUND = new Color(255, 160, 160);

    public HostAndPortConfig() {
        super();
        hostField.getDocument().addDocumentListener((SimpleDocumentListener) this::updateServerConfig);
        portField.getDocument().addDocumentListener((SimpleDocumentListener) this::updateServerConfig);
        BackendDiscoveredEventPublisher.register(this);
        BackendDiscoveredEventPublisher.getLastBackendDiscoveredEvent().ifPresent(this::listen);
    }

    @Override
    public void listen(BackendDiscoveredEvent backendDiscoveredEvent) {
        portField.setText(Integer.toString(backendDiscoveredEvent.port()));
        hostField.setText(backendDiscoveredEvent.server());
    }

    private void updateServerConfig() {
        ServerCommunication.setHostAndPort(hostField.getText(), getPort());
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
