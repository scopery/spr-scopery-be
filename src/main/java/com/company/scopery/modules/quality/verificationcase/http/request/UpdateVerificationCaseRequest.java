package com.company.scopery.modules.quality.verificationcase.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
public record UpdateVerificationCaseRequest(
        String title, String description,
        @Schema(allowableValues = {"LOAD_TEST","PERFORMANCE_TEST","SECURITY_SCAN","PENETRATION_TEST",
                "AVAILABILITY_CHECK","ACCESSIBILITY_AUDIT","COMPLIANCE_REVIEW","MANUAL_REVIEW","MONITORING_CHECK"})
        String verificationMethod,
        String procedure, String expectedResultJson, String environment,
        @Schema(allowableValues = {"DRAFT","READY","DEPRECATED","ARCHIVED"}) String lifecycleStatus,
        @Schema(allowableValues = {"MANUAL","PLANNED","AUTOMATED"}) String automationStatus,
        UUID ownerId, UUID assigneeId, Long version) {}
