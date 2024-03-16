package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.AssignmentInformation;
import de.flubba.tagmanager.smartcard.WebTargetBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import java.awt.Font;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.CENTER;

@Slf4j
public class TagAssignmentTab extends CardActionPanel {

    private final JSpinner numberSpinner = new JSpinner(new SpinnerNumberModel(1L, 1L, 30000, 1L));
    private final JCheckBox overwrite = new JCheckBox("overwrite existing assignment");

    public TagAssignmentTab() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);
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

        add(nextNumberLabel);
        add(numberSpinner);
        add(overwrite);
        overwrite.setHorizontalAlignment(CENTER);

        springLayout.putConstraint(SOUTH, nextNumberLabel, 0, NORTH, numberSpinner);
        springLayout.putConstraint(EAST, nextNumberLabel, -30, EAST, this);
        springLayout.putConstraint(WEST, nextNumberLabel, 30, WEST, this);
        springLayout.putConstraint(EAST, numberSpinner, -30, EAST, this);
        springLayout.putConstraint(WEST, numberSpinner, 30, WEST, this);
        springLayout.putConstraint(SOUTH, numberSpinner, 0, NORTH, overwrite);

        springLayout.putConstraint(EAST, overwrite, 0, EAST, this);
        springLayout.putConstraint(WEST, overwrite, 0, WEST, this);
        springLayout.putConstraint(SOUTH, overwrite, -15, SOUTH, this);
    }

    @Override
    public String getTitle() {
        return "🔩 Tag Assignment";
    }

    @Override
    public void doWithTagId(String tagId) {
        try {
            Long runnerNumber = numberSpinner.getValue() instanceof Long number ? number :
                    numberSpinner.getValue() instanceof Integer integer ? integer.longValue() :
                            numberSpinner.getValue() instanceof Double doubleNum ? doubleNum.longValue() : null;
            if (runnerNumber == null) {
                throw new NumberFormatException("value of field is not an Integer");
            }
            AssignmentInformation assignmentInformation = new AssignmentInformation(
                    runnerNumber,
                    overwrite.isSelected()
            );
            log.info("Pushing {} for runner {}", tagId, assignmentInformation.runnerNumber());
            WebTarget target = WebTargetBuilder.getClient().path("setTagAssignment");
            target = target.queryParam("tagId", tagId)
                    .queryParam("runnerId", assignmentInformation.runnerNumber())
                    .queryParam("overwrite", assignmentInformation.overwrite());
            String response = target.request().post(Entity.entity(String.class, MediaType.APPLICATION_JSON), String.class);
            log.info(response);
            numberSpinner.setValue(assignmentInformation.runnerNumber() + 1L);
        } catch (WebApplicationException e) {
            log.error(WebTargetBuilder.getErrorMessageFrom(e), e);
        } catch (NumberFormatException e) {
            log.error("Cannot register tag without a valid runner number: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Could not assign tag: {}", e.getMessage(), e);
        }
    }
}
