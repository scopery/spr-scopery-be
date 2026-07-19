package com.company.scopery.modules.trust.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum TrustErrorCatalog implements ErrorCatalog {
    TRUST_ACCESS_DENIED("TRUST_ACCESS_DENIED","Trust access denied",HttpStatus.FORBIDDEN),
    PRIVACY_REQUEST_NOT_FOUND("PRIVACY_REQUEST_NOT_FOUND","Privacy request not found",HttpStatus.NOT_FOUND),
    PRIVACY_REQUEST_INVALID_STATUS("PRIVACY_REQUEST_INVALID_STATUS","Invalid privacy request status",HttpStatus.UNPROCESSABLE_ENTITY),
    PRIVACY_REQUEST_REJECTION_REASON_REQUIRED("PRIVACY_REQUEST_REJECTION_REASON_REQUIRED","Rejection reason required",HttpStatus.BAD_REQUEST),
    LEGAL_HOLD_NOT_FOUND("LEGAL_HOLD_NOT_FOUND","Legal hold not found",HttpStatus.NOT_FOUND),
    LEGAL_HOLD_REASON_REQUIRED("LEGAL_HOLD_REASON_REQUIRED","Legal hold reason required",HttpStatus.BAD_REQUEST),
    LEGAL_HOLD_RELEASE_REASON_REQUIRED("LEGAL_HOLD_RELEASE_REASON_REQUIRED","Release reason required",HttpStatus.BAD_REQUEST),
    ANONYMIZATION_LEGAL_HOLD_BLOCKED("ANONYMIZATION_LEGAL_HOLD_BLOCKED","Anonymization blocked by legal hold",HttpStatus.UNPROCESSABLE_ENTITY),
    ANONYMIZATION_PLAN_DRY_RUN_REQUIRED("ANONYMIZATION_PLAN_DRY_RUN_REQUIRED","Dry-run required before execute",HttpStatus.UNPROCESSABLE_ENTITY),
    RETENTION_POLICY_NOT_FOUND("RETENTION_POLICY_NOT_FOUND","Retention policy not found",HttpStatus.NOT_FOUND),
    ACCESS_REVIEW_CAMPAIGN_NOT_FOUND("ACCESS_REVIEW_CAMPAIGN_NOT_FOUND","Access review campaign not found",HttpStatus.NOT_FOUND),
    ACCESS_REVIEW_INVALID_STATUS("ACCESS_REVIEW_INVALID_STATUS","Invalid access review campaign status",HttpStatus.UNPROCESSABLE_ENTITY),
    COMPLIANCE_EVIDENCE_NOT_FOUND("COMPLIANCE_EVIDENCE_NOT_FOUND","Evidence not found",HttpStatus.NOT_FOUND),
    COMPLIANCE_EVIDENCE_FINALIZED_IMMUTABLE("COMPLIANCE_EVIDENCE_FINALIZED_IMMUTABLE","Finalized evidence is immutable",HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_CLASSIFICATION_POLICY_NOT_FOUND("DATA_CLASSIFICATION_POLICY_NOT_FOUND","Classification policy not found",HttpStatus.NOT_FOUND),
    SENSITIVE_OBJECT_NOT_FOUND("SENSITIVE_OBJECT_NOT_FOUND","Sensitive object registry entry not found",HttpStatus.NOT_FOUND),
    SENSITIVE_OBJECT_ALREADY_EXISTS("SENSITIVE_OBJECT_ALREADY_EXISTS","Sensitive object type already registered",HttpStatus.CONFLICT),
    SENSITIVE_FIELD_NOT_FOUND("SENSITIVE_FIELD_NOT_FOUND","Sensitive field registry entry not found",HttpStatus.NOT_FOUND),
    SENSITIVE_FIELD_ALREADY_EXISTS("SENSITIVE_FIELD_ALREADY_EXISTS","Sensitive field already registered",HttpStatus.CONFLICT);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    TrustErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
