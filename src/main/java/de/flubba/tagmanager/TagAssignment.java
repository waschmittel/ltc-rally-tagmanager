package de.flubba.tagmanager;

import io.avaje.jsonb.Json;

@Json
public record TagAssignment(
        Long id,
        String tagId,
        Long runnerId
) {}
