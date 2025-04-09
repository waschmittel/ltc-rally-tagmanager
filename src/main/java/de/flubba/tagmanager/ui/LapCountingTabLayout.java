package de.flubba.tagmanager.ui;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

@Slf4j
abstract class LapCountingTabLayout extends CardActionPanel {
    protected final JLabel runnerNumber;
    protected final JLabel runnerName;

    public LapCountingTabLayout() {
        var title = buildTitle();
        runnerNumber = buildRunnerNumber();
        runnerName = buildRunnerName();

        add(title);
        add(runnerName);
        add(runnerNumber);

        layout(title);
    }

    private void layout(JLabel title) {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        // title on top center
        springLayout.putConstraint(EAST, title, 0, EAST, this);
        springLayout.putConstraint(WEST, title, 0, WEST, this);
        springLayout.putConstraint(NORTH, title, 15, NORTH, this);

        // horizontally and vertically  center runner number
        springLayout.putConstraint(NORTH, runnerNumber, 1, SOUTH, title);
        springLayout.putConstraint(EAST, runnerNumber, 0, EAST, this);
        springLayout.putConstraint(WEST, runnerNumber, 0, WEST, this);
        springLayout.putConstraint(SOUTH, runnerNumber, 1, NORTH, runnerName);

        // horizontally center runner name
        springLayout.putConstraint(EAST, runnerName, 0, EAST, this);
        springLayout.putConstraint(WEST, runnerName, 0, WEST, this);
        springLayout.putConstraint(SOUTH, runnerName, -15, SOUTH, this);
    }

    private JLabel buildRunnerName() {
        JLabel runnerName = new JLabel("waiting for runner");
        runnerName.setFont(new Font(
                runnerName.getFont().getName(),
                runnerName.getFont().getStyle(),
                50
        ));
        runnerName.setHorizontalAlignment(CENTER);
        runnerName.setEnabled(false);
        return runnerName;
    }

    private JLabel buildRunnerNumber() {
        final JLabel runnerNumber = new JLabel("#");
        runnerNumber.setFont(new Font(
                runnerNumber.getFont().getName(),
                runnerNumber.getFont().getStyle(),
                300
        ));
        runnerNumber.setHorizontalAlignment(CENTER);
        return runnerNumber;
    }

    private static JLabel buildTitle() {
        JLabel title = new JLabel("🏃");
        title.setFont(new Font(
                "Arial",
                title.getFont().getStyle(),
                50 // Swing on macOS will only display emojis for size 50 or less.
        ));
        title.setHorizontalAlignment(CENTER);
        return title;
    }

    @Override
    public String getTitle() {
        return "🏃‍ Lap Counting";
    }
}
