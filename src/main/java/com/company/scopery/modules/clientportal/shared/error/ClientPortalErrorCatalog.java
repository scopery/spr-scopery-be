package com.company.scopery.modules.clientportal.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum ClientPortalErrorCatalog implements ErrorCatalog {
    REVIEW_NOT_FOUND("CLIENT_REVIEW_NOT_FOUND","Client review request not found",HttpStatus.NOT_FOUND),
    GRANT_NOT_FOUND("PORTAL_GRANT_NOT_FOUND","Portal access grant not found",HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_FOUND("PORTAL_ACCOUNT_NOT_FOUND","Portal account not found",HttpStatus.NOT_FOUND),
    INVITE_NOT_FOUND("PORTAL_INVITE_NOT_FOUND","Portal invite not found",HttpStatus.NOT_FOUND),
    INVITE_INVALID("PORTAL_INVITE_INVALID","Portal invite is invalid or expired",HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_CREDENTIALS("PORTAL_INVALID_CREDENTIALS","Invalid portal credentials",HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("CLIENT_PORTAL_ACCESS_DENIED","Client portal access denied",HttpStatus.FORBIDDEN),
    PROJECT_ARCHIVED("CLIENT_PORTAL_PROJECT_ARCHIVED","Project archived",HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_STATUS("CLIENT_PORTAL_INVALID_STATUS","Invalid status for this action",HttpStatus.UNPROCESSABLE_ENTITY),
    TITLE_REQUIRED("CLIENT_PORTAL_TITLE_REQUIRED","Title is required",HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_SUSPENDABLE("CLIENT_PORTAL_ACCOUNT_NOT_SUSPENDABLE","Only active accounts can be suspended",HttpStatus.UNPROCESSABLE_ENTITY),
    ACCOUNT_ALREADY_DEACTIVATED("CLIENT_PORTAL_ACCOUNT_ALREADY_DEACTIVATED","Account is already deactivated",HttpStatus.UNPROCESSABLE_ENTITY),
    PERMISSION_POLICY_NOT_FOUND("PORTAL_PERMISSION_POLICY_NOT_FOUND","Permission policy not found",HttpStatus.NOT_FOUND),
    PERMISSION_POLICY_CODE_EXISTS("PORTAL_PERMISSION_POLICY_CODE_EXISTS","Permission policy code already exists",HttpStatus.CONFLICT);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    ClientPortalErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
