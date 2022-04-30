package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.RunnerDto;
import de.flubba.tagmanager.smartcard.WebClient;
import de.flubba.tagmanager.ui.LogTable.LogMessage.Level;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Font;

import static de.flubba.tagmanager.ui.LogTable.LogMessage.Level.INFO;
import static de.flubba.tagmanager.ui.UI.LOG_TABLE;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

public class LapCoutingTab extends CardActionPanel {

    private final JTextField runnerName = new JTextField();
    private final JTextField runnerNumber = new JTextField();

    public LapCoutingTab() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        runnerNumber.setFont(new Font(
                runnerNumber.getFont().getName(),
                runnerNumber.getFont().getStyle(),
                300
        ));
        runnerNumber.setHorizontalAlignment(CENTER);
        runnerNumber.setEditable(false);

        runnerName.setFont(new Font(
                runnerName.getFont().getName(),
                runnerName.getFont().getStyle(),
                50
        ));
        runnerName.setHorizontalAlignment(CENTER);
        runnerName.setEnabled(false);
        runnerName.setEditable(false);

        add(runnerName);
        add(runnerNumber);

        springLayout.putConstraint(EAST, runnerNumber, 0, EAST, this);
        springLayout.putConstraint(WEST, runnerNumber, 0, WEST, this);
        springLayout.putConstraint(NORTH, runnerName, 6, SOUTH, runnerNumber);
        springLayout.putConstraint(EAST, runnerName, 0, EAST, this);
        springLayout.putConstraint(WEST, runnerName, 0, WEST, this);
        springLayout.putConstraint(SOUTH, runnerName, 0, SOUTH, this);

        runnerName.setText("waiting for runner");
        runnerNumber.setText("#");
    }

    @Override
    public String getTitle() {
        return "Lap Counting";
    }

    @Override
    public void doWithTagId(String tagId) {
        try {
            LOG_TABLE.addMessage(INFO, "Counting lap for token " + tagId);
            WebTarget target = WebClient.getClient().path("countLap");
            target = target.queryParam("tagId", tagId);
            RunnerDto runner = target.request().post(Entity.entity(String.class, MediaType.APPLICATION_JSON), RunnerDto.class);
            LOG_TABLE.addMessage(INFO, String.format("Lap counted for %s (%s)", runner.name(), runner.id()));
            runnerName.setText(runner.name());
            runnerNumber.setText(runner.id().toString());
        } catch (WebApplicationException e) {
            LOG_TABLE.addMessage(Level.ERROR, WebClient.getErrorMessageFrom(e));
        } catch (RuntimeException e) {
            LOG_TABLE.addMessage(Level.ERROR, ("Could not count lap: " + e.getMessage()));
        }

    }
}
