package de.flubba.tagmanager.ui;

import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Font;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.CENTER;

@Slf4j
abstract class TagAssignmentTabLayout extends CardActionPanel {

    protected final JSpinner numberSpinner = new JSpinner(new SpinnerNumberModel(1L, 1L, 30000, 1L));
    protected final JCheckBox overwrite = new JCheckBox("overwrite existing assignment");

    TagAssignmentTabLayout() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        var title = buildTitle();
        var nextNumberLabel = new JLabel("next number to assign:");
        nextNumberLabel.setHorizontalAlignment(CENTER);
        nextNumberLabel.setVerticalAlignment(BOTTOM);
        numberSpinner.setFont(new Font(
                numberSpinner.getFont().getName(),
                numberSpinner.getFont().getStyle(),
                100));
        if (numberSpinner.getEditor() instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setHorizontalAlignment(CENTER);
        }

        add(title);
        add(nextNumberLabel);
        add(numberSpinner);
        add(overwrite);
        overwrite.setHorizontalAlignment(CENTER);

        springLayout.putConstraint(EAST, title, 0, EAST, this);
        springLayout.putConstraint(WEST, title, 0, WEST, this);
        springLayout.putConstraint(NORTH, title, 15, NORTH, this);

        springLayout.putConstraint(NORTH, nextNumberLabel, 5, SOUTH, title);
        springLayout.putConstraint(EAST, nextNumberLabel, -30, EAST, this);
        springLayout.putConstraint(WEST, nextNumberLabel, 30, WEST, this);
        springLayout.putConstraint(EAST, numberSpinner, -30, EAST, this);
        springLayout.putConstraint(WEST, numberSpinner, 30, WEST, this);
        springLayout.putConstraint(SOUTH, numberSpinner, 0, NORTH, overwrite);

        springLayout.putConstraint(EAST, overwrite, 0, EAST, this);
        springLayout.putConstraint(WEST, overwrite, 0, WEST, this);
        springLayout.putConstraint(SOUTH, overwrite, -15, SOUTH, this);
    }

    private static JLabel buildTitle() {
        FontIcon icon = FontIcon.of(FontAwesomeSolid.ID_CARD, 150, new Color(76, 175, 80));
        JLabel title = new JLabel(icon);
        title.setHorizontalAlignment(CENTER);
        return title;
    }

    @Override
    public String getTitle() {
        return "Tag Assignment";
    }
}
