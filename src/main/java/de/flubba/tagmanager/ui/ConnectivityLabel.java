package de.flubba.tagmanager.ui;

import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

class ConnectivityLabel extends JLabel {
    private static final Color CONNECTED_BACKGROUND = new Color(0, 100, 0);
    private static final Color CONNECTED_FOREGROUND = Color.GREEN;

    ConnectivityLabel() {
        super("connected");
        setHorizontalAlignment(CENTER);
        setOpaque(true);
        setBackground(CONNECTED_BACKGROUND);
        setForeground(CONNECTED_FOREGROUND);
        setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() * 1.5f));
        setPreferredSize(new Dimension(100, 50));
        setBorder(new FlatLineBorder(new Insets(5, 5, 5, 5), CONNECTED_BACKGROUND, 1, 20));
    }
}
