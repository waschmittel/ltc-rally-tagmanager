package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.RunnerDto;
import de.flubba.tagmanager.smartcard.WebTargetBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
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
public class LapCountingTab extends CardActionPanel {
    private final JLabel runnerNumber;
    private final JLabel runnerName;

    public LapCountingTab() {
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

    @Override
    public void doWithTagId(String tagId) {
        try {
            log.info("Counting lap for token {}", tagId);
            WebTarget target = WebTargetBuilder.getClient().path("countLap");
            target = target.queryParam("tagId", tagId);
            RunnerDto runner = target.request().post(Entity.entity(String.class, MediaType.APPLICATION_JSON), RunnerDto.class);
            log.info(String.format("Lap counted for %s (%s)", runner.name(), runner.id()));
            runnerName.setText(runner.name());
            runnerNumber.setText(runner.id().toString());
        } catch (WebApplicationException e) {
            log.error(WebTargetBuilder.getErrorMessageFrom(e), e);
        } catch (RuntimeException e) {
            log.error("Could not count lap: {}", e.getMessage(), e);
        }

    }
}
