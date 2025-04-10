package de.flubba.tagmanager.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

public class HostAndPortConfigLayout extends JPanel {
    protected final JTextField hostField = new JTextField("localhost");
    protected final JTextField portField = new JTextField("8080");
    Color defaultBackground = portField.getBackground();

    public HostAndPortConfigLayout() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        hostField.setHorizontalAlignment(CENTER);
        portField.setHorizontalAlignment(CENTER);

        // to prevent accidental configuration changes (these have happened in the past)
        requireDoubleClickToFocus(hostField);
        requireDoubleClickToFocus(portField);

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

    void requireDoubleClickToFocus(JTextField jTextField) {
        jTextField.setFocusable(false);
        jTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    jTextField.setFocusable(true);
                    jTextField.grabFocus();
                }
            }
        });
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                jTextField.setFocusable(false);
            }
        });

    }
}
