package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.util.OsType;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;

import static de.flubba.tagmanager.util.OsType.MacOS;
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

public final class UI {
    public static final LogTable LOG_TABLE = new LogTable();

    public static void createAndShow() {
        JFrame frame = setupWindow();
        layoutFrame(frame);
        display(frame);
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

    public static void layoutFrame(JFrame frame) {
        var pane = frame.getContentPane();
        var springLayout = new SpringLayout();
        pane.setLayout(springLayout);

        var splitPane = new JSplitPane(HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new LeftContent());
        splitPane.setRightComponent(LOG_TABLE);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);

        pane.add(splitPane);

        springLayout.putConstraint(NORTH, splitPane, 6, NORTH, pane);
        springLayout.putConstraint(WEST, splitPane, 6, WEST, pane);
        springLayout.putConstraint(SOUTH, pane, 6, SOUTH, splitPane);
        springLayout.putConstraint(EAST, pane, 6, EAST, splitPane);
    }

    private static void display(JFrame frame) {
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}
