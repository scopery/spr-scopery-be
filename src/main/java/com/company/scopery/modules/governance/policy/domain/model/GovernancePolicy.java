package com.company.scopery.modules.governance.policy.domain.model;
import java.time.Instant; import java.util.UUID;
public record GovernancePolicy(UUID id, UUID workspaceId, String objectTypeCode, String versioningMode, boolean versionOnUpdate,
        boolean lockOnFinalize, boolean allowRestore, boolean allowOwnerGrant, String baselineGuardMode, String auditLevel,
        String status, int version, Instant createdAt, Instant updatedAt) {
    public static GovernancePolicy create(UUID workspaceId, String objectType) {
        Instant now = Instant.now();
        return new GovernancePolicy(UUID.randomUUID(), workspaceId, objectType, "ON_UPDATE", true, true, true, true,
                "WARN", "STANDARD", "ACTIVE", 0, now, now);
    }
    public GovernancePolicy update(String versioningMode, Boolean versionOnUpdate, Boolean lockOnFinalize, Boolean allowRestore,
                                   Boolean allowOwnerGrant, String baselineGuardMode, String auditLevel) {
        return new GovernancePolicy(id, workspaceId, objectTypeCode,
                versioningMode != null ? versioningMode : this.versioningMode,
                versionOnUpdate != null ? versionOnUpdate : this.versionOnUpdate,
                lockOnFinalize != null ? lockOnFinalize : this.lockOnFinalize,
                allowRestore != null ? allowRestore : this.allowRestore,
                allowOwnerGrant != null ? allowOwnerGrant : this.allowOwnerGrant,
                baselineGuardMode != null ? baselineGuardMode : this.baselineGuardMode,
                auditLevel != null ? auditLevel : this.auditLevel,
                status, version, createdAt, Instant.now());
    }
}
