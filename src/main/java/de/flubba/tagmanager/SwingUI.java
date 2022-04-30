package de.flubba.tagmanager;

import de.flubba.tagmanager.ui.UI;
import de.flubba.tagmanager.util.OsType;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static de.flubba.tagmanager.util.OsType.MacOS;

public class SwingUI {

    private final JSplitPane mainPane = new JSplitPane();

    private static void createAndShowGUI() {
        JFrame frame = setupWindow();
        new UI(frame);

        //Display the window.
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private static JFrame setupWindow() {
        JFrame frame = new JFrame("LTC Rallye Tag Manager");
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        if (OsType.get() == MacOS) {
            var rootPane = frame.getRootPane();
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
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
        javax.swing.SwingUtilities.invokeLater(SwingUI::createAndShowGUI);
    }
}
