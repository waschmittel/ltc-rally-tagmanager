package de.flubba.tagmanager;

import com.formdev.flatlaf.FlatIntelliJLaf;
import de.flubba.tagmanager.ui.UI;
import de.flubba.tagmanager.util.OsType;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static de.flubba.tagmanager.util.OsType.MacOS;

public final class TagManager {
    private TagManager() {
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        if (OsType.get() == MacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "LTC Rallye Tag Manager");
        }
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(UI::createAndShow);
    }
}
