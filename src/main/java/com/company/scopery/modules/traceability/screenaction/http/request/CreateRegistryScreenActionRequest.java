package com.company.scopery.modules.traceability.screenaction.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateRegistryScreenActionRequest(@NotBlank String actionCode, @NotBlank String name,
                                                String actionType, String description, int displayOrder) {}
