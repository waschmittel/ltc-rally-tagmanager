package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.TagAssignmentInformation;
import de.flubba.tagmanager.smartcard.WebTargetBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

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
            var tagAssignmentInformation = getTagAssignmentInformation();
            log.info("Pushing {} for runner {}", tagId, tagAssignmentInformation.runnerNumber());
            String response = WebTargetBuilder.build()
                    .path("setTagAssignment")
                    .queryParam("tagId", tagId)
                    .queryParam("runnerId", tagAssignmentInformation.runnerNumber())
                    .queryParam("overwrite", tagAssignmentInformation.overwrite())
                    .request()
                    .post(Entity.entity(String.class, MediaType.APPLICATION_JSON), String.class);
            log.info(response);
            numberSpinner.setValue(tagAssignmentInformation.runnerNumber() + 1L);
        } catch (WebApplicationException e) {
            log.error(WebTargetBuilder.getErrorMessageFrom(e), e);
        } catch (NumberFormatException e) {
            log.error("Cannot register tag without a valid runner number: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Could not assign tag: {}", e.getMessage(), e);
        }
    }

    private TagAssignmentInformation getTagAssignmentInformation() {
        Long runnerNumber = switch (numberSpinner.getValue()) {
            case Long longValue -> longValue;
            case Integer integerValue -> integerValue.longValue();
            case Double doubleValue -> doubleValue.longValue();
            default -> throw new NumberFormatException("value of field is not a number");
        };
        return new TagAssignmentInformation(
                runnerNumber,
                overwrite.isSelected()
        );
    }
}
