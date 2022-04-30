package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.AssignmentInformation;
import de.flubba.tagmanager.smartcard.WebClient;
import de.flubba.tagmanager.ui.LogTable.LogMessage.Level;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import java.awt.Font;

import static de.flubba.tagmanager.ui.LogTable.LogMessage.Level.INFO;
import static de.flubba.tagmanager.ui.UI.LOG_TABLE;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.CENTER;

public class TagAssignmentTab extends CardActionPanel {

    private final JSpinner nextNumber = new JSpinner(new SpinnerNumberModel(1, 1, 30000, 1));
    private final JCheckBox overwrite = new JCheckBox("overwrite existing assignment");

    public TagAssignmentTab() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);
        var nextNumberLabel = new JLabel("next number to assign:");
        nextNumberLabel.setHorizontalAlignment(CENTER);
        nextNumberLabel.setVerticalAlignment(BOTTOM);
        nextNumber.setFont(new Font(
                nextNumber.getFont().getName(),
                nextNumber.getFont().getStyle(),
                100));
        if (nextNumber.getEditor() instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setHorizontalAlignment(CENTER);
        }

        add(nextNumberLabel);
        add(nextNumber);
        add(overwrite);

        springLayout.putConstraint(NORTH, nextNumberLabel, 0, NORTH, this);
        springLayout.putConstraint(SOUTH, nextNumberLabel, 0, NORTH, nextNumber);
        springLayout.putConstraint(EAST, nextNumberLabel, 0, EAST, this);
        springLayout.putConstraint(WEST, nextNumberLabel, 0, WEST, this);
        springLayout.putConstraint(EAST, nextNumber, 0, EAST, this);
        springLayout.putConstraint(WEST, nextNumber, 0, WEST, this);
        springLayout.putConstraint(SOUTH, nextNumber, 0, NORTH, overwrite);
        springLayout.putConstraint(SOUTH, overwrite, 0, SOUTH, this);
    }

    @Override
    public String getTitle() {
        return "Tag Assignment";
    }

    @Override
    public void doWithTagId(String tagId) {
        try {
            var runnerNumber = nextNumber.getValue() instanceof Integer integer ? integer.longValue() : null;
            if (runnerNumber == null) {
                throw new NumberFormatException("value of field is not an Integer");
            }
            AssignmentInformation assignmentInformation = new AssignmentInformation(
                    runnerNumber,
                    overwrite.isSelected()
            );
            LOG_TABLE.addMessage(INFO, "Pushing " + tagId + " for runner " + assignmentInformation.runnerNumber());
            WebTarget target = WebClient.getClient().path("setTagAssignment");
            target = target.queryParam("tagId", tagId)
                    .queryParam("runnerId", assignmentInformation.runnerNumber())
                    .queryParam("overwrite", assignmentInformation.overwrite());
            String response = target.request().post(Entity.entity(String.class, MediaType.APPLICATION_JSON), String.class);
            LOG_TABLE.addMessage(INFO, response);
            nextNumber.setValue(assignmentInformation.runnerNumber() + 1);
        } catch (WebApplicationException e) {
            LOG_TABLE.addMessage(Level.ERROR, WebClient.getErrorMessageFrom(e));
        } catch (NumberFormatException e) {
            LOG_TABLE.addMessage(Level.ERROR, "Cannot register tag without a valid runner number: " + e.getMessage());
        }
    }
}
