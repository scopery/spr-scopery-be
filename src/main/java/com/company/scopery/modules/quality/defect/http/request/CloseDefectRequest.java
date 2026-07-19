package com.company.scopery.modules.quality.defect.http.request;
import jakarta.validation.constraints.NotBlank;
public record CloseDefectRequest(@NotBlank String resolutionType, @NotBlank String resolutionNote) {}
