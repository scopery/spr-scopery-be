package com.company.scopery.modules.traceability.apiendpoint.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateRegistryApiEndpointRequest(UUID projectId, @NotBlank String method, @NotBlank String pathPattern, String name) {}
