package com.company.scopery.modules.productivity.savedview.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateSavedViewRequest(@NotBlank String targetType, @NotBlank String name, UUID projectId, String viewConfigJson, String filtersJson) {}
