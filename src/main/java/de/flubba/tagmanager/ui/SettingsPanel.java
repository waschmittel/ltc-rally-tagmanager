package de.flubba.tagmanager.ui;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.flubba.tagmanager.discovery.MDNSListener;
import org.slf4j.LoggerFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.util.prefs.Preferences;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

public class SettingsPanel extends JPanel {
    private static final Preferences PREFS = Preferences.userNodeForPackage(SettingsPanel.class);
    private static final String DEBUG_LOG_KEY = "debugLogEnabled";
    private static final String MDNS_ENABLED_KEY = "mdnsEnabled";
    private final JCheckBox debugLogCheckbox = new JCheckBox("Show debug logs");
    private final JCheckBox mdnsCheckbox = new JCheckBox("Enable mDNS discovery");

    public SettingsPanel() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        debugLogCheckbox.addActionListener(e -> saveDebugLogPreference());
        mdnsCheckbox.addActionListener(e -> saveMdnsPreference());
        add(debugLogCheckbox);
        add(mdnsCheckbox);

        springLayout.putConstraint(NORTH, this, 0, NORTH, debugLogCheckbox);
        springLayout.putConstraint(WEST, debugLogCheckbox, 10, WEST, this);
        springLayout.putConstraint(NORTH, mdnsCheckbox, 0, NORTH, debugLogCheckbox);
        springLayout.putConstraint(WEST, mdnsCheckbox, 10, EAST, debugLogCheckbox);
        springLayout.putConstraint(SOUTH, this, 0, SOUTH, debugLogCheckbox);
        springLayout.putConstraint(EAST, this, 10, EAST, mdnsCheckbox);

        loadDebugLogPreference();
        loadMdnsPreference();
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

    private void loadMdnsPreference() {
        boolean mdnsEnabled = PREFS.getBoolean(MDNS_ENABLED_KEY, true);
        mdnsCheckbox.setSelected(mdnsEnabled);
        handleMdnsListener(mdnsEnabled);
    }

    private void saveMdnsPreference() {
        boolean mdnsEnabled = mdnsCheckbox.isSelected();
        PREFS.putBoolean(MDNS_ENABLED_KEY, mdnsEnabled);
        handleMdnsListener(mdnsEnabled);
    }

    private static void handleMdnsListener(boolean mdnsEnabled) {
        if (mdnsEnabled) {
            MDNSListener.listen();
        } else {
            MDNSListener.stop();
        }
    }

    public static void setDebugLogEnabled(boolean enabled) {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(enabled ? Level.DEBUG : Level.INFO);
    }
}
