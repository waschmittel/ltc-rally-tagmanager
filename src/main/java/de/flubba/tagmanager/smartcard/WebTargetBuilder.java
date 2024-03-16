package de.flubba.tagmanager.smartcard;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class WebTargetBuilder {
    private static WebTarget clientConfig = null;

    public static void setHostAndPort(String hostname, Integer port) {
        if (hostname == null || port == null) {
            clientConfig = null;
        }
        clientConfig = ClientBuilder.newClient(new ClientConfig()).target("http://%s:%s".formatted(hostname, port));
    }

    public static WebTarget getClient() {
        if (clientConfig == null) {
            throw new IllegalArgumentException("no valid host/port set");
        }
        return clientConfig;
    }

    private WebTargetBuilder() {
    }

    public static String getErrorMessageFrom(WebApplicationException e) {
        Object entity = e.getResponse().getEntity();
        if (entity instanceof InputStream inputStream) {
            try {
                String errorContent = IOUtils.toString(inputStream, UTF_8);
                if (StringUtils.isBlank(errorContent)) {
                    errorContent = "<no message>";
                }
                return ("Error: Result: %s: Content: %s".formatted(e.getMessage(), errorContent));
            } catch (IOException ioException) {
                return "Error: " + ioException.getMessage();
            }
        }
        return "Error: Could not get error message.";
    }

}
