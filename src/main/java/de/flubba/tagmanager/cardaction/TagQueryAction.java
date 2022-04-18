package de.flubba.tagmanager.cardaction;

import de.flubba.tagmanager.TagAssignment;
import de.flubba.tagmanager.UI;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.WebTarget;

public class TagQueryAction extends CardAction {

    @Override
    public void doWithTagId(String tagId) {
        try {
            UI.addMessage("Getting information for tag " + tagId);
            WebTarget target = clientConfig.path("getTagAssignment");
            target = target.queryParam("tagId", tagId);
            TagAssignment assignment = target.request().get(TagAssignment.class);
            UI.addMessage(String.format("Tag %s is assigned to runner %s", assignment.tagId, assignment.runnerId));
        } catch (WebApplicationException e) {
            UI.addErrorMessage(getErrorMessageFrom(e));
        }
    }

}
