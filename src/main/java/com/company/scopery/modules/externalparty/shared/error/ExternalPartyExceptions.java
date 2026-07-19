package com.company.scopery.modules.externalparty.shared.error;
import com.company.scopery.common.exception.AppException; import java.util.Map; import java.util.UUID;
public final class ExternalPartyExceptions {
    private ExternalPartyExceptions(){}
    // Organization
    public static AppException organizationNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.ORGANIZATION_NOT_FOUND,"Organization not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException organizationCodeAlreadyExists(String code){return new AppException(ExternalPartyErrorCatalog.ORGANIZATION_CODE_ALREADY_EXISTS,"Code already exists: "+code,Map.of("code",code==null?"":code));}
    public static AppException organizationArchived(UUID id){return new AppException(ExternalPartyErrorCatalog.ORGANIZATION_ARCHIVED,"Organization archived: "+id,Map.of("id",id));}
    public static AppException organizationBlacklisted(UUID id){return new AppException(ExternalPartyErrorCatalog.ORGANIZATION_BLACKLISTED,"Organization blacklisted: "+id,Map.of("id",id));}
    // Contact
    public static AppException contactNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.CONTACT_NOT_FOUND,"Contact not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException contactOrganizationMismatch(UUID contactId,UUID orgId){return new AppException(ExternalPartyErrorCatalog.CONTACT_ORGANIZATION_MISMATCH,"Contact "+contactId+" does not belong to org "+orgId,Map.of("contactId",contactId,"orgId",orgId));}
    public static AppException contactArchived(UUID id){return new AppException(ExternalPartyErrorCatalog.CONTACT_ARCHIVED,"Contact archived: "+id,Map.of("id",id));}
    public static AppException contactDoNotContact(UUID id){return new AppException(ExternalPartyErrorCatalog.CONTACT_DO_NOT_CONTACT,"Contact marked do-not-contact: "+id,Map.of("id",id));}
    public static AppException contactSensitiveAccessDenied(){return new AppException(ExternalPartyErrorCatalog.CONTACT_SENSITIVE_ACCESS_DENIED);}
    // Address
    public static AppException addressNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.ADDRESS_NOT_FOUND,"Address not found: "+id,Map.of("id",id));}
    public static AppException addressOwnerRequired(){return new AppException(ExternalPartyErrorCatalog.ADDRESS_OWNER_REQUIRED);}
    public static AppException addressOwnerMismatch(UUID addressId){return new AppException(ExternalPartyErrorCatalog.ADDRESS_OWNER_MISMATCH,"Address owner mismatch: "+addressId,Map.of("id",addressId));}
    // Channel
    public static AppException channelNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.CHANNEL_NOT_FOUND,"Channel not found: "+id,Map.of("id",id));}
    public static AppException channelDuplicatePrimary(String type){return new AppException(ExternalPartyErrorCatalog.CHANNEL_DUPLICATE_PRIMARY,"Primary "+type+" channel already exists",Map.of("channelType",type==null?"":type));}
    public static AppException channelInvalidType(String type){return new AppException(ExternalPartyErrorCatalog.CHANNEL_INVALID_TYPE,"Invalid channel type: "+type,Map.of("channelType",type==null?"":type));}
    // Communication preference
    public static AppException communicationPreferenceNotFound(UUID ownerId){return new AppException(ExternalPartyErrorCatalog.COMMUNICATION_PREFERENCE_NOT_FOUND,"Preference not found for: "+ownerId,Map.of("ownerId",ownerId));}
    // Relationship
    public static AppException relationshipNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.RELATIONSHIP_NOT_FOUND,"Relationship not found: "+id,Map.of("id",id));}
    public static AppException relationshipWorkspaceMismatch(UUID id){return new AppException(ExternalPartyErrorCatalog.RELATIONSHIP_WORKSPACE_MISMATCH,"External party workspace mismatch: "+id,Map.of("id",id));}
    public static AppException primaryClientConflict(UUID projectId){return new AppException(ExternalPartyErrorCatalog.PRIMARY_CLIENT_CONFLICT,"Project "+projectId+" already has a primary client",Map.of("projectId",projectId));}
    // Stakeholder
    public static AppException stakeholderNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.STAKEHOLDER_NOT_FOUND,"Stakeholder not found: "+id,Map.of("id",id==null?"":id));}
    // Approval authority
    public static AppException approvalAuthorityNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.APPROVAL_AUTHORITY_NOT_FOUND,"Approval authority not found: "+id,Map.of("id",id));}
    public static AppException approvalAuthorityTargetMismatch(UUID id){return new AppException(ExternalPartyErrorCatalog.APPROVAL_AUTHORITY_TARGET_MISMATCH,"Authority target mismatch: "+id,Map.of("id",id));}
    // Document link
    public static AppException documentLinkNotFound(UUID id){return new AppException(ExternalPartyErrorCatalog.DOCUMENT_LINK_NOT_FOUND,"Document link not found: "+id,Map.of("id",id));}
    public static AppException documentLinkTargetMismatch(UUID documentId){return new AppException(ExternalPartyErrorCatalog.DOCUMENT_LINK_TARGET_MISMATCH,"Document workspace mismatch: "+documentId,Map.of("documentId",documentId));}
    // General
    public static AppException accessDenied(){return new AppException(ExternalPartyErrorCatalog.ACCESS_DENIED);}
    public static AppException projectArchived(UUID id){return new AppException(ExternalPartyErrorCatalog.PROJECT_ARCHIVED,"Project archived: "+id,Map.of("projectId",id));}
    public static AppException nameRequired(){return new AppException(ExternalPartyErrorCatalog.NAME_REQUIRED);}
}
