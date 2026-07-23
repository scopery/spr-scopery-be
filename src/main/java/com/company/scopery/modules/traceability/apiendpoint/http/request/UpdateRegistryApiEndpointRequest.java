package com.company.scopery.modules.traceability.apiendpoint.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateRegistryApiEndpointRequest(@NotBlank String method, @NotBlank String pathPattern, @NotBlank String name) {}
