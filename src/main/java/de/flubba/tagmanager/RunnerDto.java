package de.flubba.tagmanager;

import io.avaje.jsonb.Json;

@Json
public record RunnerDto(Long id, String name) {}
