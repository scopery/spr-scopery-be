package com.company.scopery.modules.traceability.apiendpoint.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateRegistryApiEndpointRequest(
        UUID projectId,
        @NotBlank @Schema(allowableValues = {"GET", "POST", "PUT", "PATCH", "DELETE"}, example = "GET") String method,
        @NotBlank String pathPattern,
        String name
) {}
