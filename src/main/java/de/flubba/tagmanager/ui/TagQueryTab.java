package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.ServerCommunication;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagQueryTab extends TagQueryTabLayout {
    @Override
    public void doWithTagId(String tagId) {
        try {
            log.info("Getting information for tag {}", tagId);
            ServerCommunication.getTagAssignment(tagId).ifPresentOrElse(tagAssignment -> {
                runnerNumberLabel.setText(tagAssignment.runnerId().toString());
                log.info("Tag {} is assigned to runner {}", tagAssignment.tagId(), tagAssignment.runnerId());
            }, () -> {
                runnerNumberLabel.setText("-");
                log.warn("No assignment found for tag {}", tagId);
            });
            tagIdLabel.setText(tagId);
        } catch (WebApplicationException e) {
            ServerCommunication.logWebApplicationException(e);
        } catch (RuntimeException e) {
            log.error("Could not query tag: {}", e.getMessage(), e);
        }
    }

}
