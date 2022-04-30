package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.TagAssignment;
import de.flubba.tagmanager.smartcard.WebClient;
import de.flubba.tagmanager.ui.LogTable.LogMessage.Level;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.WebTarget;

import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Font;

import static de.flubba.tagmanager.smartcard.WebClient.getErrorMessageFrom;
import static de.flubba.tagmanager.ui.LogTable.LogMessage.Level.INFO;
import static de.flubba.tagmanager.ui.UI.LOG_TABLE;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

public class TagQueryTab extends CardActionPanel {
    JTextField tagId = new JTextField();
    JTextField runnerNumber = new JTextField();

    public TagQueryTab() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        tagId.setHorizontalAlignment(CENTER);
        tagId.setFont(new Font(
                tagId.getFont().getName(),
                tagId.getFont().getStyle(),
                40
        ));
        tagId.setEnabled(false);

        runnerNumber.setHorizontalAlignment(CENTER);
        runnerNumber.setFont(new Font(
                runnerNumber.getFont().getName(),
                runnerNumber.getFont().getStyle(),
                200
        ));
        runnerNumber.setEnabled(false);

        add(tagId);
        add(runnerNumber);

        springLayout.putConstraint(EAST, tagId, 0, EAST, this);
        springLayout.putConstraint(WEST, tagId, 0, WEST, this);
        springLayout.putConstraint(EAST, runnerNumber, 0, EAST, this);
        springLayout.putConstraint(WEST, runnerNumber, 0, WEST, this);
        springLayout.putConstraint(NORTH, runnerNumber, 6, SOUTH, tagId);
        springLayout.putConstraint(SOUTH, runnerNumber, 0, SOUTH, this);

        tagId.setText("waiting for tag");
        runnerNumber.setText("#");
    }

    @Override
    public String getTitle() {
        return "Tag Query";
    }

    @Override
    public void doWithTagId(String tagId) {
        try {
            LOG_TABLE.addMessage(INFO, "Getting information for tag " + tagId);
            WebTarget target = WebClient.getClient().path("getTagAssignment");
            target = target.queryParam("tagId", tagId);
            TagAssignment assignment = target.request().get(TagAssignment.class);
            LOG_TABLE.addMessage(INFO, String.format("Tag %s is assigned to runner %s", assignment.tagId(), assignment.runnerId()));
        } catch (WebApplicationException e) {
            LOG_TABLE.addMessage(Level.ERROR, getErrorMessageFrom(e));
        } catch (RuntimeException e) {
            LOG_TABLE.addMessage(Level.ERROR, "Could not query tag: " + e.getMessage());
        }
    }
}
