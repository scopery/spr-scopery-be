package com.company.scopery.modules.trust.evidence.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateComplianceEvidenceRequest(@NotBlank String evidenceType, @NotBlank String title, String description) {}
