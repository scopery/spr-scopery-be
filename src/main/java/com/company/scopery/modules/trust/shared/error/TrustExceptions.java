package com.company.scopery.modules.trust.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class TrustExceptions {
    private TrustExceptions(){}
    public static AppException accessDenied(){ return new AppException(TrustErrorCatalog.TRUST_ACCESS_DENIED); }
    public static AppException privacyNotFound(UUID id){ return new AppException(TrustErrorCatalog.PRIVACY_REQUEST_NOT_FOUND,"Privacy request not found: "+id, Map.of("id",id)); }
    public static AppException privacyInvalidStatus(){ return new AppException(TrustErrorCatalog.PRIVACY_REQUEST_INVALID_STATUS); }
    public static AppException rejectionReasonRequired(){ return new AppException(TrustErrorCatalog.PRIVACY_REQUEST_REJECTION_REASON_REQUIRED); }
    public static AppException legalHoldNotFound(UUID id){ return new AppException(TrustErrorCatalog.LEGAL_HOLD_NOT_FOUND,"Legal hold not found: "+id, Map.of("id",id)); }
    public static AppException legalHoldReasonRequired(){ return new AppException(TrustErrorCatalog.LEGAL_HOLD_REASON_REQUIRED); }
    public static AppException releaseReasonRequired(){ return new AppException(TrustErrorCatalog.LEGAL_HOLD_RELEASE_REASON_REQUIRED); }
    public static AppException anonymizationBlocked(){ return new AppException(TrustErrorCatalog.ANONYMIZATION_LEGAL_HOLD_BLOCKED); }
    public static AppException dryRunRequired(){ return new AppException(TrustErrorCatalog.ANONYMIZATION_PLAN_DRY_RUN_REQUIRED); }
    public static AppException evidenceNotFound(UUID id){ return new AppException(TrustErrorCatalog.COMPLIANCE_EVIDENCE_NOT_FOUND,"Evidence not found: "+id, Map.of("id",id)); }
    public static AppException evidenceImmutable(){ return new AppException(TrustErrorCatalog.COMPLIANCE_EVIDENCE_FINALIZED_IMMUTABLE); }
    public static AppException retentionPolicyNotFound(){ return new AppException(TrustErrorCatalog.RETENTION_POLICY_NOT_FOUND); }
    public static AppException accessReviewNotFound(){ return new AppException(TrustErrorCatalog.ACCESS_REVIEW_CAMPAIGN_NOT_FOUND); }
    public static AppException accessReviewInvalidStatus(){ return new AppException(TrustErrorCatalog.ACCESS_REVIEW_INVALID_STATUS); }
    public static AppException sensitiveObjectNotFound(UUID id){ return new AppException(TrustErrorCatalog.SENSITIVE_OBJECT_NOT_FOUND,"Sensitive object not found: "+id, Map.of("id",id)); }
    public static AppException sensitiveObjectAlreadyExists(String code){ return new AppException(TrustErrorCatalog.SENSITIVE_OBJECT_ALREADY_EXISTS,"Object type already registered: "+code, Map.of("objectTypeCode",code)); }
    public static AppException sensitiveFieldNotFound(UUID id){ return new AppException(TrustErrorCatalog.SENSITIVE_FIELD_NOT_FOUND,"Sensitive field not found: "+id, Map.of("id",id)); }
    public static AppException sensitiveFieldAlreadyExists(String path){ return new AppException(TrustErrorCatalog.SENSITIVE_FIELD_ALREADY_EXISTS,"Field already registered: "+path, Map.of("fieldPath",path)); }
}
