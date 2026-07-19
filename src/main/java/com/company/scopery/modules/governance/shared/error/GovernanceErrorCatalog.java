package com.company.scopery.modules.governance.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum GovernanceErrorCatalog implements ErrorCatalog {
    OBJECT_TYPE_NOT_FOUND("GOVERNANCE_OBJECT_TYPE_NOT_FOUND","Governed object type not found",HttpStatus.NOT_FOUND),
    POLICY_NOT_FOUND("GOVERNANCE_POLICY_NOT_FOUND","Governance policy not found",HttpStatus.NOT_FOUND),
    OWNERSHIP_NOT_FOUND("OBJECT_OWNERSHIP_NOT_FOUND","Ownership not found",HttpStatus.NOT_FOUND),
    OWNERSHIP_TRANSFER_NOT_ALLOWED("OBJECT_OWNER_TRANSFER_NOT_ALLOWED","Ownership transfer not allowed: no active owner",HttpStatus.UNPROCESSABLE_ENTITY),
    GRANT_NOT_FOUND("OBJECT_ACCESS_GRANT_NOT_FOUND","Access grant not found",HttpStatus.NOT_FOUND),
    GRANT_DUPLICATE("OBJECT_ACCESS_GRANT_DUPLICATE","Access grant already exists",HttpStatus.CONFLICT),
    VERSION_NOT_FOUND("OBJECT_VERSION_NOT_FOUND","Version record not found",HttpStatus.NOT_FOUND),
    RESTORE_NOT_ELIGIBLE("GOVERNANCE_VERSION_NOT_RESTORE_ELIGIBLE","Version is not eligible for restore",HttpStatus.UNPROCESSABLE_ENTITY),
    SNAPSHOT_NOT_FOUND("OBJECT_SNAPSHOT_NOT_FOUND","Snapshot not found",HttpStatus.NOT_FOUND),
    RESTORE_NOT_FOUND("OBJECT_RESTORE_NOT_FOUND","Restore request not found",HttpStatus.NOT_FOUND),
    LOCK_ACTIVE("OBJECT_LOCK_ACTIVE","Object is locked",HttpStatus.UNPROCESSABLE_ENTITY),
    LOCK_NOT_FOUND("OBJECT_LOCK_NOT_FOUND","Object lock not found",HttpStatus.NOT_FOUND),
    FINALIZE_NOT_ALLOWED("OBJECT_FINALIZE_NOT_ALLOWED","Object cannot be finalized",HttpStatus.UNPROCESSABLE_ENTITY),
    UNFINALIZE_NOT_ALLOWED("OBJECT_UNFINALIZE_NOT_ALLOWED","Object cannot be unfinalized",HttpStatus.UNPROCESSABLE_ENTITY),
    BASELINE_GUARD_BLOCKED("BASELINE_GUARD_BLOCKED","Change blocked by baseline guard",HttpStatus.UNPROCESSABLE_ENTITY),
    SENSITIVE_ACCESS_DENIED("SENSITIVE_ACCESS_DENIED","Access to sensitive data denied",HttpStatus.FORBIDDEN),
    GOVERNANCE_ACCESS_DENIED("GOVERNANCE_ACCESS_DENIED","Governance access denied",HttpStatus.FORBIDDEN);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    GovernanceErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
