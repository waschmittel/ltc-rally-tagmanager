package de.flubba.tagmanager.ui;

import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;

public class BatteryDisplay extends JPanel {
    private static final int ICON_SIZE = 24;
    private final JLabel batteryIcon;
    private final JLabel batteryText;
    private final BatteryBar batteryBar;
    private final BatteryMonitor batteryMonitor;

    public BatteryDisplay() {
        batteryIcon = new JLabel();
        batteryText = new JLabel("--");
        batteryText.setFont(batteryText.getFont().deriveFont(Font.BOLD));
        batteryBar = new BatteryBar();

        var springLayout = new SpringLayout();
        setLayout(springLayout);

        add(batteryIcon);
        add(batteryText);
        add(batteryBar);

        springLayout.putConstraint(NORTH, this, 0, NORTH, batteryIcon);
        springLayout.putConstraint(WEST, batteryIcon, 10, WEST, this);
        springLayout.putConstraint(VERTICAL_CENTER, batteryText, 0, VERTICAL_CENTER, batteryIcon);
        springLayout.putConstraint(WEST, batteryText, 5, EAST, batteryIcon);
        springLayout.putConstraint(VERTICAL_CENTER, batteryBar, 0, VERTICAL_CENTER, batteryIcon);
        springLayout.putConstraint(WEST, batteryBar, 10, EAST, batteryText);
        springLayout.putConstraint(SOUTH, this, 0, SOUTH, batteryIcon);
        springLayout.putConstraint(EAST, this, 10, EAST, batteryBar);

        batteryMonitor = new BatteryMonitor();
        batteryMonitor.addListener(this::updateDisplay);
    }

    private void updateDisplay(BatteryMonitor.BatteryStatus status) {
        if (!status.hasBattery()) {
            batteryIcon.setIcon(FontIcon.of(FontAwesomeSolid.PLUG, ICON_SIZE, Color.GRAY));
            batteryText.setText("AC Power");
            batteryText.setForeground(Color.GRAY);
            batteryBar.setVisible(false);
        } else {
            updateBatteryIcon(status);
            batteryText.setText(status.statusText());
            updateTextColor(status);
            batteryBar.setVisible(true);
            batteryBar.update(status.chargePercent(), status.isCharging());
        }
    }

    private void updateBatteryIcon(BatteryMonitor.BatteryStatus status) {
        FontAwesomeSolid iconType;
        Color iconColor;

        if (status.isCharging()) {
            iconType = FontAwesomeSolid.BATTERY_HALF;
            iconColor = new Color(34, 139, 34); // Green for charging
        } else {
            double chargePercent = status.chargePercent() * 100;
            if (chargePercent > 75) {
                iconType = FontAwesomeSolid.BATTERY_FULL;
                iconColor = new Color(34, 139, 34); // Green
            } else if (chargePercent > 50) {
                iconType = FontAwesomeSolid.BATTERY_THREE_QUARTERS;
                iconColor = new Color(100, 149, 237); // Blue
            } else if (chargePercent > 25) {
                iconType = FontAwesomeSolid.BATTERY_HALF;
                iconColor = new Color(255, 165, 0); // Orange
            } else if (chargePercent > 10) {
                iconType = FontAwesomeSolid.BATTERY_QUARTER;
                iconColor = new Color(255, 69, 0); // Red-Orange
            } else {
                iconType = FontAwesomeSolid.BATTERY_EMPTY;
                iconColor = new Color(220, 20, 60); // Crimson
            }
        }

        batteryIcon.setIcon(FontIcon.of(iconType, ICON_SIZE, iconColor));
    }

    private void updateTextColor(BatteryMonitor.BatteryStatus status) {
        Color textColor;
        double chargePercent = status.chargePercent() * 100;
        if (status.isCharging()) {
            textColor = new Color(34, 139, 34); // Green
        } else if (chargePercent <= 10) {
            textColor = new Color(220, 20, 60); // Crimson
        } else if (chargePercent <= 25) {
            textColor = new Color(255, 69, 0); // Red-Orange
        } else {
            textColor = getForeground(); // Default color
        }
        batteryText.setForeground(textColor);
    }

    private static class BatteryBar extends JPanel {
        private static final int BAR_HEIGHT = 16;
        private static final int BAR_WIDTH = 300;
        private double chargeLevel = 0.0;
        private boolean isCharging = false;

        BatteryBar() {
            setPreferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));
            setOpaque(false);
        }

        void update(double chargeLevel, boolean isCharging) {
            this.chargeLevel = chargeLevel;
            this.isCharging = isCharging;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Draw border
            g2d.setColor(Color.GRAY);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, 4, 4);

            // Calculate fill width
            int fillWidth = (int) (chargeLevel * (width - 2));

            // Determine fill color
            Color fillColor;
            if (isCharging) {
                fillColor = new Color(34, 139, 34); // Green
            } else if (chargeLevel > 0.75) {
                fillColor = new Color(34, 139, 34); // Green
            } else if (chargeLevel > 0.50) {
                fillColor = new Color(100, 149, 237); // Blue
            } else if (chargeLevel > 0.25) {
                fillColor = new Color(255, 165, 0); // Orange
            } else if (chargeLevel > 0.10) {
                fillColor = new Color(255, 69, 0); // Red-Orange
            } else {
                fillColor = new Color(220, 20, 60); // Crimson
            }

            // Draw fill
            g2d.setColor(fillColor);
            g2d.fillRoundRect(1, 1, fillWidth, height - 2, 4, 4);
        }
    }
}
