package de.flubba.tagmanager.ui;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

public class UI {
    public static final LogTable LOG_TABLE = new LogTable();
    
    public UI(JFrame frame) {
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
}
