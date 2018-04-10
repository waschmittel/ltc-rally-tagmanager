package de.flubba.tagmanager.cardaction;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import de.flubba.tagmanager.RunnerDto;
import de.flubba.tagmanager.UI;

public class LapCountAction extends CardAction {
    @Override
    public void doWithTagId(String tagId) {
        try {
            UI.addMessage("Counting lap for token " + tagId);
            WebTarget target = clientConfig.path("countLap");
            target = target.queryParam("tagId", tagId);
            RunnerDto runner = target.request().post(Entity.entity(String.class, MediaType.APPLICATION_JSON), RunnerDto.class);
            UI.addMessage(String.format("Lap counted for %s (%s)", runner.name, runner.id));
            UI.setLastRunner(runner);
        }
        catch (WebApplicationException e) {
            UI.addErrorMessage(getErrorMessageFrom(e));
        }
        catch (Exception e) {
            UI.addErrorMessage("Could not count lap: " + e.getMessage());
        }
    }

}
