package com.company.scopery.modules.traceability.dataentity.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record UpdateRegistryDataEntityRequest(@NotBlank String name, String description, String tableName, UUID moduleId) {}
