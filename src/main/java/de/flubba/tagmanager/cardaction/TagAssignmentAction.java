package de.flubba.tagmanager.cardaction;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import de.flubba.tagmanager.AssignmentInformation;
import de.flubba.tagmanager.UI;

public class TagAssignmentAction extends CardAction {

    @Override
    public void doWithTagId(String tagId) {
        try {
            AssignmentInformation assignmentInformation = UI.getAssignmentInformation();
            UI.addMessage("Pushing " + tagId + " for runner " + assignmentInformation.runnerNumber);
            WebTarget target = clientConfig.path("setTagAssignment");
            target = target.queryParam("tagId", tagId)
                           .queryParam("runnerId", assignmentInformation.runnerNumber)
                           .queryParam("overwrite", assignmentInformation.overwrite);
            String response = target.request().post(Entity.entity(String.class, MediaType.APPLICATION_JSON), String.class);
            UI.addMessage(response);
            UI.setNextRunnerNumber(assignmentInformation.runnerNumber + 1);
        }
        catch (WebApplicationException e) {
            UI.addErrorMessage(getErrorMessageFrom(e));
        }
        catch (NumberFormatException e) {
            UI.addErrorMessage(String.format("Cannot register tag without a valid runner number."));
        }
    }

}
