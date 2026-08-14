package com.company.scopery.modules.traceability.specdocrevision.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateRegistrySpecDocRevisionRequest(
        @NotBlank String revisionNo,
        String targetSheetName,
        @NotBlank String details,
        String personInCharge,
        String color,
        LocalDate changedAt,
        int displayOrder) {}
