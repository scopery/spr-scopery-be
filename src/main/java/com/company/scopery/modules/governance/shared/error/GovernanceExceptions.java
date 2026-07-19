package com.company.scopery.modules.governance.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class GovernanceExceptions {
    private GovernanceExceptions(){}
    public static AppException accessDenied(){ return new AppException(GovernanceErrorCatalog.GOVERNANCE_ACCESS_DENIED); }
    public static AppException objectTypeNotFound(String code){ return new AppException(GovernanceErrorCatalog.OBJECT_TYPE_NOT_FOUND,"Object type not found: "+code, Map.of("code", code)); }
    public static AppException lockActive(String objectType, UUID targetId){
        return new AppException(GovernanceErrorCatalog.LOCK_ACTIVE,"Object already locked: "+objectType+"/"+targetId, Map.of("objectType", objectType, "targetId", targetId));
    }
    public static AppException lockNotFound(UUID lockId){ return new AppException(GovernanceErrorCatalog.LOCK_NOT_FOUND,"Lock not found: "+lockId, Map.of("lockId", lockId)); }
    public static AppException finalizeNotAllowed(String reason){ return new AppException(GovernanceErrorCatalog.FINALIZE_NOT_ALLOWED, reason); }
    public static AppException unfinalizeNotAllowed(String reason){ return new AppException(GovernanceErrorCatalog.UNFINALIZE_NOT_ALLOWED, reason); }
    public static AppException ownershipNotFound(String objectType, UUID targetId){
        return new AppException(GovernanceErrorCatalog.OWNERSHIP_NOT_FOUND,"Ownership not found: "+objectType+"/"+targetId, Map.of("objectType", objectType, "targetId", targetId));
    }
    public static AppException ownershipTransferNotAllowed(){ return new AppException(GovernanceErrorCatalog.OWNERSHIP_TRANSFER_NOT_ALLOWED); }
    public static AppException policyNotFound(UUID workspaceId, String objectType){
        return new AppException(GovernanceErrorCatalog.POLICY_NOT_FOUND,"Governance policy not found for "+objectType, Map.of("workspaceId", workspaceId, "objectType", objectType));
    }
    public static AppException grantNotFound(UUID id){ return new AppException(GovernanceErrorCatalog.GRANT_NOT_FOUND,"Grant not found: "+id, Map.of("id", id)); }
    public static AppException grantDuplicate(){ return new AppException(GovernanceErrorCatalog.GRANT_DUPLICATE); }
    public static AppException versionNotFound(UUID id){ return new AppException(GovernanceErrorCatalog.VERSION_NOT_FOUND,"Version not found: "+id, Map.of("id", id)); }
    public static AppException restoreNotEligible(UUID id){ return new AppException(GovernanceErrorCatalog.RESTORE_NOT_ELIGIBLE,"Version not eligible for restore: "+id, Map.of("id", id)); }
    public static AppException snapshotNotFound(UUID id){ return new AppException(GovernanceErrorCatalog.SNAPSHOT_NOT_FOUND,"Snapshot not found: "+id, Map.of("id", id)); }
    public static AppException restoreNotFound(UUID id){ return new AppException(GovernanceErrorCatalog.RESTORE_NOT_FOUND,"Restore not found: "+id, Map.of("id", id)); }
    public static AppException baselineGuardBlocked(String reason){ return new AppException(GovernanceErrorCatalog.BASELINE_GUARD_BLOCKED, reason); }
    public static AppException sensitiveAccessDenied(){ return new AppException(GovernanceErrorCatalog.SENSITIVE_ACCESS_DENIED); }
}
