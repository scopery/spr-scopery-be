package com.company.scopery.modules.traceability.screenfield.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateRegistryScreenFieldRequest(@NotBlank String fieldKey, @NotBlank String label,
                                               @NotBlank String fieldType, String description,
                                               boolean required, int displayOrder, UUID sectionId) {}
