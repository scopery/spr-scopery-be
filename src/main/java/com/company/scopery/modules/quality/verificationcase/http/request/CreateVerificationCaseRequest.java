package com.company.scopery.modules.quality.verificationcase.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateVerificationCaseRequest(
        @NotBlank String title,
        @NotNull @Schema(allowableValues = {"LOAD_TEST","PERFORMANCE_TEST","SECURITY_SCAN","PENETRATION_TEST",
                "AVAILABILITY_CHECK","ACCESSIBILITY_AUDIT","COMPLIANCE_REVIEW","MANUAL_REVIEW","MONITORING_CHECK"})
        String verificationMethod,
        @NotNull UUID requirementId,
        String code, String description, String procedure, String expectedResultJson,
        String environment,
        @Schema(allowableValues = {"MANUAL","PLANNED","AUTOMATED"}) String automationStatus,
        UUID ownerId, UUID assigneeId) {}
