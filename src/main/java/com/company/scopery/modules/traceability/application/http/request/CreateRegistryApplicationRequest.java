package com.company.scopery.modules.traceability.application.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateRegistryApplicationRequest(@NotBlank String code, @NotBlank String name, String description, UUID ownerUserId) {}
