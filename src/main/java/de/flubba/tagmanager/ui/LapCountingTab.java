package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.ServerCommunication;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LapCountingTab extends LapCountingTabLayout {
    @Override
    public void doWithTagId(String tagId) {
        try {
            log.info("Counting lap for token {}", tagId);
            ServerCommunication.countLap(tagId).ifPresent(runner -> {
                log.info("Lap counted for {} ({})", runner.name(), runner.id());
                runnerName.setText(runner.name());
                runnerNumber.setText(runner.id().toString());
            });
        } catch (WebApplicationException e) {
            ServerCommunication.logWebApplicationException(e);
        } catch (RuntimeException e) {
            log.error("Could not count lap: {}", e.getMessage(), e);
        }
    }
}
