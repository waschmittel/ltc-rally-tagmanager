package de.flubba.tagmanager.ui;

import com.formdev.flatlaf.ui.FlatLineBorder;
import de.flubba.tagmanager.smartcard.ServerCommunication;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

@Slf4j
class ConnectivityLabel extends JLabel {
    private static final Color CONNECTED_BACKGROUND = new Color(0, 100, 0);
    private static final Color CONNECTED_FOREGROUND = Color.GREEN;
    private static final Color DISCONNECTED_BACKGROUND = new Color(139, 0, 0);
    private static final Color DISCONNECTED_FOREGROUND = Color.RED;

    private final Thread pingThread;
    private final boolean running = true;

    ConnectivityLabel() {
        super("checking...");
        setHorizontalAlignment(CENTER);
        setOpaque(true);
        setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() * 1.5f));
        setPreferredSize(new Dimension(100, 50));

        pingThread = new Thread(this::runConnectivityCheck);
        pingThread.setDaemon(true);
        pingThread.start();
    }

    private void runConnectivityCheck() {
        while (running) {
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
        log.info("Checking connectivity...");
        boolean connected = ServerCommunication.ping();
        log.info("Connectivity check finished: {}", connected);
        SwingUtilities.invokeLater(() -> {
            if (connected) { // TODO: only update things in the UI if the connection state changes
                setText("connected");
                setBackground(CONNECTED_BACKGROUND);
                setForeground(CONNECTED_FOREGROUND);
                setBorder(buildRoundedBorder(CONNECTED_BACKGROUND));
            } else {
                setText("disconnected");
                setBackground(DISCONNECTED_BACKGROUND);
                setForeground(DISCONNECTED_FOREGROUND);
                setBorder(buildRoundedBorder(DISCONNECTED_BACKGROUND));
            }
        });
    }

    private static FlatLineBorder buildRoundedBorder(Color connectedBackground) {
        return new FlatLineBorder(new Insets(5, 5, 5, 5), connectedBackground, 1, 30);
    }
}
