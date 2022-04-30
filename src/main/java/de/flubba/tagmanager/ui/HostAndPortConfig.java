package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.WebClient;
import de.flubba.tagmanager.util.SimpleDocumentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Dimension;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

public class HostAndPortConfig extends JPanel {

    public static final Color INVALID_BACKGROUND = new Color(255, 160, 160);
    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("8080");
    Color defaultBackground = portField.getBackground();

    public HostAndPortConfig() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        hostField.setHorizontalAlignment(CENTER);
        hostField.getDocument().addDocumentListener((SimpleDocumentListener) this::updateWebClient);

        portField.setHorizontalAlignment(CENTER);
        portField.getDocument().addDocumentListener((SimpleDocumentListener) this::updateWebClient);

        var label = new JLabel("host/port:");
        portField.setPreferredSize(new Dimension(100, portField.getPreferredSize().height));
        portField.setMaximumSize(new Dimension(100, portField.getPreferredSize().height));

        add(label);
        add(hostField);
        add(portField);

        springLayout.putConstraint(VERTICAL_CENTER, label, 0, VERTICAL_CENTER, this);
        springLayout.putConstraint(NORTH, this, 0, NORTH, portField);
        springLayout.putConstraint(SOUTH, this, 0, SOUTH, portField);
        springLayout.putConstraint(WEST, label, 10, WEST, this);
        springLayout.putConstraint(EAST, this, 5, EAST, portField);
        springLayout.putConstraint(WEST, hostField, 10, EAST, label);
        springLayout.putConstraint(WEST, portField, 0, EAST, hostField);


    }

    private void updateWebClient() {
        Integer port = getPort();
        String host = getHost();

        WebClient.setHostAndPort(hostField.getText(), port);
    }

    private String getHost() {
        String host = hostField.getText();
        if (host.matches("[a-z\\d.]+")
                && !host.matches("\\..*")
                && !host.matches(".*\\.")
                && !host.matches(".*\\.\\..*")
        ) {
            hostField.setBackground(defaultBackground);
            hostField.setToolTipText(null);
            return host;
        }
        hostField.setBackground(INVALID_BACKGROUND);
        hostField.setToolTipText("this is not a valid host");
        return null;
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
