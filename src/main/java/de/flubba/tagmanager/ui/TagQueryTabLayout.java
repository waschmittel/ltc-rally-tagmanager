package de.flubba.tagmanager.ui;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JLabel;
import javax.swing.SpringLayout;
import java.awt.Font;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

@Slf4j
abstract class TagQueryTabLayout extends CardActionPanel {
    protected final JLabel runnerNumberLabel;
    protected final JLabel tagIdLabel;

    TagQueryTabLayout() {
        var title = buildTitle();
        tagIdLabel = buildTagIdLabel();
        runnerNumberLabel = buildNumberLabel();

        add(title);
        add(runnerNumberLabel);
        add(tagIdLabel);

        layout(title);
    }

    private void layout(JLabel title) {
        var springLayout = new SpringLayout();
        setLayout(springLayout);
        springLayout.putConstraint(EAST, title, 0, EAST, this);
        springLayout.putConstraint(WEST, title, 0, WEST, this);
        springLayout.putConstraint(NORTH, title, 15, NORTH, this);

        springLayout.putConstraint(NORTH, runnerNumberLabel, 1, SOUTH, title);
        springLayout.putConstraint(EAST, runnerNumberLabel, 0, EAST, this);
        springLayout.putConstraint(WEST, runnerNumberLabel, 0, WEST, this);
        springLayout.putConstraint(SOUTH, runnerNumberLabel, 0, NORTH, tagIdLabel);

        springLayout.putConstraint(EAST, tagIdLabel, 0, EAST, this);
        springLayout.putConstraint(WEST, tagIdLabel, 0, WEST, this);
        springLayout.putConstraint(SOUTH, tagIdLabel, -15, SOUTH, this);
    }

    private JLabel buildNumberLabel() {
        JLabel runnerNumberLabel = new JLabel("#");
        runnerNumberLabel.setHorizontalAlignment(CENTER);
        runnerNumberLabel.setFont(new Font(
                runnerNumberLabel.getFont().getName(),
                runnerNumberLabel.getFont().getStyle(),
                200
        ));
        return runnerNumberLabel;
    }

    private JLabel buildTagIdLabel() {
        JLabel tagIdLabel = new JLabel("waiting for tag");
        tagIdLabel.setHorizontalAlignment(CENTER);
        tagIdLabel.setFont(new Font(
                tagIdLabel.getFont().getName(),
                tagIdLabel.getFont().getStyle(),
                40
        ));
        return tagIdLabel;
    }

    private static JLabel buildTitle() {
        JLabel title = new JLabel("💡");
        title.setHorizontalAlignment(CENTER);
        title.setFont(new Font(
                "Arial",
                title.getFont().getStyle(),
                50 // Swing on macOS will only display emojis for size 50 or less.
        ));
        return title;
    }

    @Override
    public String getTitle() {
        return "💡 Tag Query";
    }
}
