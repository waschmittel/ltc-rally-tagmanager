package de.flubba.tagmanager.ui;

import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

class ConnectivityLabel extends JLabel {
    private static final Color CONNECTED_BACKGROUND = new Color(0, 100, 0);
    private static final Color CONNECTED_FOREGROUND = Color.GREEN;
    private static final Color DISCONNECTED_BACKGROUND = new Color(139, 0, 0);
    private static final Color DISCONNECTED_FOREGROUND = Color.RED;

    ConnectivityLabel() {
        super("checking...");
        setHorizontalAlignment(CENTER);
        setOpaque(true);
        setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() * 1.5f));
        setPreferredSize(new Dimension(100, 50));

        ConnectivityMonitor.getInstance().addListener(this::updateConnectionState);
    }

    private void updateConnectionState(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
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
