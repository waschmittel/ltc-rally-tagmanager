package de.flubba.tagmanager.smartcard;

import de.flubba.tagmanager.RunnerDto;
import de.flubba.tagmanager.TagAssignment;
import io.avaje.http.client.HttpClient;
import io.avaje.http.client.HttpException;
import io.avaje.jsonb.Jsonb;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

@Slf4j
public final class ServerCommunication {
    private static final Jsonb JSONB = Jsonb.builder().build();
    private static HttpClient client = null;
    private static String baseUrl = null;

    public static void setHostAndPort(String hostname, Integer port) {
        if (hostname == null || port == null) {
            client = null;
            baseUrl = null;
            return;
        }
        //noinspection HttpUrlsUsage -- this is designed for auto-discovery in an airgapped network, so http is fine
        baseUrl = "http://%s:%s".formatted(hostname, port);
        client = HttpClient.builder()
                .baseUrl(baseUrl)
                .connectionTimeout(Duration.ofMillis(2000))
                .requestTimeout(Duration.ofMillis(2000))
                .bodyAdapter(new io.avaje.http.client.JsonbBodyAdapter(JSONB))
                .build();
    }

    private static HttpClient getClient() {
        if (client == null) {
            throw new IllegalArgumentException("no valid host/port set");
        }
        return client;
    }

    public static Optional<RunnerDto> countLap(String tagId) {
        try {
            var response = getClient()
                    .request()
                    .path("countLap")
                    .queryParam("tagId", tagId)
                    .POST()
                    .bean(RunnerDto.class);
            return Optional.of(response);
        } catch (HttpException e) {
            if (e.statusCode() == HTTP_CONFLICT) {
                log.warn("Lap too short. Full response: {}", e.getMessage());
                return Optional.empty();
            }
            throw e;
        }
    }

    public static Optional<String> assignTag(String tagId, Long runnerNumber, boolean overwrite) {
        try {
            var response = getClient()
                    .request()
                    .path("setTagAssignment")
                    .queryParam("tagId", tagId)
                    .queryParam("runnerId", runnerNumber)
                    .queryParam("overwrite", overwrite)
                    .POST()
                    .asString()
                    .body();
            return Optional.of(response);
        } catch (HttpException e) {
            if (e.statusCode() == HTTP_CONFLICT) {
                log.error("Tag already assigned. Full response: {}", e.getMessage());
                return Optional.empty();
            }
            throw e;
        }
    }

    public static Optional<TagAssignment> getTagAssignment(String tagId) {
        try {
            var response = getClient()
                    .request()
                    .path("getTagAssignment")
                    .queryParam("tagId", tagId)
                    .GET()
                    .bean(TagAssignment.class);
            return Optional.of(response);
        } catch (HttpException e) {
            if (e.statusCode() == HTTP_NOT_FOUND) {
                return Optional.empty();
            }
            throw e;
        }
    }

    public static boolean ping() {
        try {
            log.debug("Pinging server {}", baseUrl);
            getClient()
                    .request()
                    .path("ping")
                    .GET()
                    .asVoid();
            return true;
        } catch (RuntimeException e) {
            log.debug("Ping failed: {}", e.getMessage(), e);
            return false;
        }
    }

    public static void logHttpException(HttpException e) {
        log.error(getErrorMessageFrom(e), e);
    }

    private static String getErrorMessageFrom(HttpException e) {
        String errorContent = e.bodyAsString();
        if (errorContent == null || errorContent.isBlank()) {
            errorContent = "<no message>";
        }
        return "Error: Status: %d, Content: %s".formatted(e.statusCode(), errorContent);
    }

    // this is just a helper class with static methods
    private ServerCommunication() {
    }
}
