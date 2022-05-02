package de.flubba.tagmanager;

import de.flubba.tagmanager.ui.UI;
import de.flubba.tagmanager.util.OsType;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static de.flubba.tagmanager.util.OsType.MacOS;

public final class TagManager {
    private TagManager() {
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (OsType.get() == MacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "LTC Rallye Tag Manager");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
        }

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(UI::createAndShow);
    }
}
