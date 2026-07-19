package com.company.scopery.modules.traceability.screen.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateRegistryScreenRequest(UUID projectId, @NotBlank String code, @NotBlank String name, String routePath) {}
