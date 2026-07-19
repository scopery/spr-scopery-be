package com.company.scopery.modules.trust.accessreview.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreatePermissionReviewFindingRequest(@NotBlank String findingType, @NotBlank String severity,
        String description, String recommendation) {}
