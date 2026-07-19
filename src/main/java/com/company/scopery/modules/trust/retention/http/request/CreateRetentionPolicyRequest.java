package com.company.scopery.modules.trust.retention.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
public record CreateRetentionPolicyRequest(@NotBlank String policyCode, @NotBlank String name,
        @NotBlank String objectTypeCode, String classification, @NotNull Integer retentionPeriodDays,
        @NotBlank String retentionAction) {}
