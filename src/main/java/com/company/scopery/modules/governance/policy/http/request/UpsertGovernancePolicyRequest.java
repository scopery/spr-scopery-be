package com.company.scopery.modules.governance.policy.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpsertGovernancePolicyRequest(@NotBlank String objectTypeCode, String versioningMode, Boolean versionOnUpdate,
        Boolean lockOnFinalize, Boolean allowRestore, Boolean allowOwnerGrant, String baselineGuardMode, String auditLevel) {}
