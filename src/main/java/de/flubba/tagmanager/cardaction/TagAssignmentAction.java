package de.flubba.tagmanager.cardaction;

import de.flubba.tagmanager.AssignmentInformation;
import de.flubba.tagmanager.UI;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

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
            UI.addErrorMessage("Cannot register tag without a valid runner number.");
        }
    }

}
