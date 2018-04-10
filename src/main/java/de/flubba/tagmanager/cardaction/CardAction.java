package de.flubba.tagmanager.cardaction;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;

import de.flubba.tagmanager.UI;

public abstract class CardAction {
    protected WebTarget clientConfig = ClientBuilder.newClient(new ClientConfig()).target(UI.getBaseUrl());

    public abstract void doWithTagId(String uid);

    protected static String getErrorMessageFrom(WebApplicationException e) {
        Object entity = e.getResponse().getEntity();
        if (entity instanceof InputStream) {
            try {
                return ("Error: " + IOUtils.toString((InputStream) entity, "UTF-8"));
            }
            catch (IOException ioException) {
                // can be empty because the error message is returned anyway
            }
        }
        return "Error: Could not get error message.";
    }
}
