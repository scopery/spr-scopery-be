package com.company.scopery.modules.externalparty.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum ExternalPartyErrorCatalog implements ErrorCatalog {
    // Organization
    ORGANIZATION_NOT_FOUND("EXTERNAL_ORG_NOT_FOUND","External organization not found",HttpStatus.NOT_FOUND),
    ORGANIZATION_CODE_ALREADY_EXISTS("EXTERNAL_ORG_CODE_ALREADY_EXISTS","Organization code already exists in workspace",HttpStatus.CONFLICT),
    ORGANIZATION_ARCHIVED("EXTERNAL_ORG_ARCHIVED","Organization is archived",HttpStatus.UNPROCESSABLE_ENTITY),
    ORGANIZATION_BLACKLISTED("EXTERNAL_ORG_BLACKLISTED","Organization is blacklisted; override permission required",HttpStatus.UNPROCESSABLE_ENTITY),
    // Contact
    CONTACT_NOT_FOUND("EXTERNAL_CONTACT_NOT_FOUND","External contact not found",HttpStatus.NOT_FOUND),
    CONTACT_ORGANIZATION_MISMATCH("EXTERNAL_CONTACT_ORG_MISMATCH","Contact does not belong to the specified organization",HttpStatus.UNPROCESSABLE_ENTITY),
    CONTACT_ARCHIVED("EXTERNAL_CONTACT_ARCHIVED","Contact is archived",HttpStatus.UNPROCESSABLE_ENTITY),
    CONTACT_DO_NOT_CONTACT("EXTERNAL_CONTACT_DO_NOT_CONTACT","Contact is marked do-not-contact",HttpStatus.UNPROCESSABLE_ENTITY),
    CONTACT_SENSITIVE_ACCESS_DENIED("EXTERNAL_CONTACT_SENSITIVE_ACCESS_DENIED","Sensitive contact fields require elevated permission",HttpStatus.FORBIDDEN),
    // Address
    ADDRESS_NOT_FOUND("EXTERNAL_ADDRESS_NOT_FOUND","External party address not found",HttpStatus.NOT_FOUND),
    ADDRESS_OWNER_REQUIRED("EXTERNAL_ADDRESS_OWNER_REQUIRED","Address must belong to an organization or contact",HttpStatus.UNPROCESSABLE_ENTITY),
    ADDRESS_OWNER_MISMATCH("EXTERNAL_ADDRESS_OWNER_MISMATCH","Address does not belong to the specified owner",HttpStatus.UNPROCESSABLE_ENTITY),
    // Channel
    CHANNEL_NOT_FOUND("EXTERNAL_CHANNEL_NOT_FOUND","Contact channel not found",HttpStatus.NOT_FOUND),
    CHANNEL_DUPLICATE_PRIMARY("EXTERNAL_CHANNEL_DUPLICATE_PRIMARY","A primary channel of this type already exists for the contact",HttpStatus.CONFLICT),
    CHANNEL_INVALID_TYPE("EXTERNAL_CHANNEL_INVALID_TYPE","Invalid channel type",HttpStatus.BAD_REQUEST),
    // Communication preference
    COMMUNICATION_PREFERENCE_NOT_FOUND("EXTERNAL_COMM_PREF_NOT_FOUND","Communication preference not found",HttpStatus.NOT_FOUND),
    // Relationship
    RELATIONSHIP_NOT_FOUND("EXTERNAL_RELATIONSHIP_NOT_FOUND","Project external party relationship not found",HttpStatus.NOT_FOUND),
    RELATIONSHIP_WORKSPACE_MISMATCH("EXTERNAL_RELATIONSHIP_WORKSPACE_MISMATCH","External party does not belong to the project workspace",HttpStatus.UNPROCESSABLE_ENTITY),
    PRIMARY_CLIENT_CONFLICT("EXTERNAL_PRIMARY_CLIENT_CONFLICT","Project already has a primary client relationship",HttpStatus.CONFLICT),
    // Stakeholder
    STAKEHOLDER_NOT_FOUND("STAKEHOLDER_NOT_FOUND","Stakeholder not found",HttpStatus.NOT_FOUND),
    // Approval authority
    APPROVAL_AUTHORITY_NOT_FOUND("APPROVAL_AUTHORITY_NOT_FOUND","Approval authority not found",HttpStatus.NOT_FOUND),
    APPROVAL_AUTHORITY_TARGET_MISMATCH("APPROVAL_AUTHORITY_TARGET_MISMATCH","Authority target does not belong to project workspace",HttpStatus.UNPROCESSABLE_ENTITY),
    // Document link
    DOCUMENT_LINK_NOT_FOUND("EXTERNAL_DOC_LINK_NOT_FOUND","External party document link not found",HttpStatus.NOT_FOUND),
    DOCUMENT_LINK_TARGET_MISMATCH("EXTERNAL_DOC_LINK_TARGET_MISMATCH","Document does not belong to the same workspace",HttpStatus.UNPROCESSABLE_ENTITY),
    // General
    ACCESS_DENIED("EXTERNAL_PARTY_ACCESS_DENIED","External party access denied",HttpStatus.FORBIDDEN),
    PROJECT_ARCHIVED("EXTERNAL_PARTY_PROJECT_ARCHIVED","Project archived",HttpStatus.UNPROCESSABLE_ENTITY),
    NAME_REQUIRED("EXTERNAL_PARTY_NAME_REQUIRED","Name is required",HttpStatus.BAD_REQUEST);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    ExternalPartyErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
