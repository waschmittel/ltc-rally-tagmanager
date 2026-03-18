package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.ServerCommunication;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
final class ConnectivityMonitor {
    private static final ConnectivityMonitor INSTANCE = new ConnectivityMonitor();

    private final List<Consumer<Boolean>> listeners = new ArrayList<>();
    private Boolean lastConnectedState = null;

    private ConnectivityMonitor() {
        var pingThread = new Thread(this::runConnectivityCheck);
        pingThread.setDaemon(true);
        pingThread.start();
    }

    static ConnectivityMonitor getInstance() {
        return INSTANCE;
    }

    void addListener(Consumer<Boolean> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        // Immediately notify the listener of the current state if known
        if (lastConnectedState != null) {
            listener.accept(lastConnectedState);
        }
    }

    private void runConnectivityCheck() {
        while (true) {
            checkConnectivity();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void checkConnectivity() {
        log.debug("Checking connectivity...");
        boolean currentConnectedState = ServerCommunication.ping();
        log.debug("Connectivity check finished: {}", currentConnectedState);

        if (lastConnectedState == null || lastConnectedState != currentConnectedState) {
            lastConnectedState = currentConnectedState;
            notifyListeners(currentConnectedState);
        }
    }

    private void notifyListeners(boolean connected) {
        synchronized (listeners) {
            for (Consumer<Boolean> listener : listeners) {
                listener.accept(connected);
            }
        }
    }
}
