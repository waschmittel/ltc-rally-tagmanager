package de.flubba.tagmanager.smartcard;

import de.flubba.tagmanager.RunnerDto;
import de.flubba.tagmanager.TagAssignment;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public final class ServerCommunication {
    private static final Client CLIENT = ClientBuilder.newClient();
    private static WebTarget clientConfig = null;

    public static void setHostAndPort(String hostname, Integer port) {
        if (hostname == null || port == null) {
            clientConfig = null;
        }
        //noinspection HttpUrlsUsage -- this is designed for auto-discovery in an airgapped network, so http is fine
        clientConfig = CLIENT.target("http://%s:%s".formatted(hostname, port));
    }

    private static WebTarget buildWebTarget() {
        if (clientConfig == null) {
            throw new IllegalArgumentException("no valid host/port set");
        }
        return clientConfig;
    }

    public static Optional<RunnerDto> countLap(String tagId) {
        try {
            return Optional.of(
                    ServerCommunication.buildWebTarget()
                            .path("countLap")
                            .queryParam("tagId", tagId)
                            .request()
                            .post(Entity.entity(String.class, APPLICATION_JSON), RunnerDto.class)
            );
        } catch (ClientErrorException clientErrorException) {
            if (clientErrorException.getResponse().getStatus() == CONFLICT.getStatusCode()) {
                log.warn("Lap too short. Full response: {}", clientErrorException.getResponse().readEntity(String.class));
                return Optional.empty();
            }
            throw clientErrorException;
        }
    }

    public static Optional<String> assignTag(String tagId, Long runnerNumber, boolean overwrite) {
        try {
            return Optional.of(
                    ServerCommunication.buildWebTarget()
                            .path("setTagAssignment")
                            .queryParam("tagId", tagId)
                            .queryParam("runnerId", runnerNumber)
                            .queryParam("overwrite", overwrite)
                            .request()
                            .post(Entity.entity(String.class, APPLICATION_JSON), String.class)
            );
        } catch (ClientErrorException clientErrorException) {
            if (clientErrorException.getResponse().getStatus() == CONFLICT.getStatusCode()) {
                log.error("Tag already assigned. Full response: {}", clientErrorException.getResponse().readEntity(String.class));
                return Optional.empty();
            }
            throw clientErrorException;
        }
    }

    public static Optional<TagAssignment> getTagAssignment(String tagId) {
        try {
            return Optional.of(
                    ServerCommunication.buildWebTarget()
                            .path("getTagAssignment")
                            .queryParam("tagId", tagId)
                            .request()
                            .get(TagAssignment.class)
            );
        } catch (NotFoundException notFoundException) {
            return Optional.empty();
        }
    }

    public static void logWebApplicationException(WebApplicationException e) {
        log.error(getErrorMessageFrom(e), e);
    }

    private static String getErrorMessageFrom(WebApplicationException e) {
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

    // this is just a helper class with static methods
    private ServerCommunication() {
    }
}
