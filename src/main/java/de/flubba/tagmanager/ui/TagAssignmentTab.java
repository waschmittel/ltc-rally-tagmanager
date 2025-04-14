package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.ServerCommunication;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagAssignmentTab extends TagAssignmentTabLayout {
    @Override
    public void doWithTagId(String tagId) {
        try {
            var runnerNumber = getRunnerNumber();
            log.info("Pushing {} for runner {}", tagId, runnerNumber);
            ServerCommunication.assignTag(tagId, runnerNumber, overwrite.isSelected()).ifPresent(response -> {
                log.info(response);
                numberSpinner.setValue(runnerNumber + 1L);
            });
        } catch (WebApplicationException e) {
            ServerCommunication.logWebApplicationException(e);
        } catch (NumberFormatException e) {
            log.error("Cannot register tag without a valid runner number: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Could not assign tag: {}", e.getMessage(), e);
        }
    }

    private Long getRunnerNumber() {
        return switch (numberSpinner.getValue()) {
            case Long longValue -> longValue;
            case Integer integerValue -> integerValue.longValue();
            case Double doubleValue -> doubleValue.longValue();
            default -> throw new NumberFormatException("value of field is not a number");
        };
    }
}
