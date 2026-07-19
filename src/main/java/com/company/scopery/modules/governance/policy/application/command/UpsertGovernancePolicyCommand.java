package com.company.scopery.modules.governance.policy.application.command;
import java.util.UUID;
public record UpsertGovernancePolicyCommand(UUID workspaceId, String objectTypeCode, String versioningMode,
        Boolean versionOnUpdate, Boolean lockOnFinalize, Boolean allowRestore, Boolean allowOwnerGrant,
        String baselineGuardMode, String auditLevel) {}
