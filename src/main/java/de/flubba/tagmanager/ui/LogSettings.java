package de.flubba.tagmanager.ui;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.util.prefs.Preferences;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

public class LogSettings extends JPanel {
    private static final Preferences PREFS = Preferences.userNodeForPackage(LogSettings.class);
    private static final String DEBUG_LOG_KEY = "debugLogEnabled";
    private final JCheckBox debugLogCheckbox = new JCheckBox("Show debug logs");

    public LogSettings() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        debugLogCheckbox.addActionListener(e -> saveDebugLogPreference());
        add(debugLogCheckbox);

        springLayout.putConstraint(NORTH, this, 0, NORTH, debugLogCheckbox);
        springLayout.putConstraint(SOUTH, this, 0, SOUTH, debugLogCheckbox);
        springLayout.putConstraint(WEST, debugLogCheckbox, 10, WEST, this);
        springLayout.putConstraint(EAST, this, 10, EAST, debugLogCheckbox);

        loadDebugLogPreference();
    }

    private void loadDebugLogPreference() {
        boolean debugLogEnabled = PREFS.getBoolean(DEBUG_LOG_KEY, false);
        debugLogCheckbox.setSelected(debugLogEnabled);
        setDebugLogEnabled(debugLogEnabled);
    }

    private void saveDebugLogPreference() {
        boolean debugLogEnabled = debugLogCheckbox.isSelected();
        PREFS.putBoolean(DEBUG_LOG_KEY, debugLogEnabled);
        setDebugLogEnabled(debugLogEnabled);
    }

    public static void setDebugLogEnabled(boolean enabled) {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(enabled ? Level.DEBUG : Level.INFO);
    }
}
