package com.company.scopery.modules.clientportal.shared.error;
import com.company.scopery.common.exception.AppException; import java.util.Map; import java.util.UUID;
public final class ClientPortalExceptions {
    private ClientPortalExceptions(){}
    public static AppException reviewNotFound(UUID id){return new AppException(ClientPortalErrorCatalog.REVIEW_NOT_FOUND,"Review not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException grantNotFound(UUID id){return new AppException(ClientPortalErrorCatalog.GRANT_NOT_FOUND,"Grant not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException accountNotFound(UUID id){return new AppException(ClientPortalErrorCatalog.ACCOUNT_NOT_FOUND,"Account not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException inviteNotFound(UUID id){return new AppException(ClientPortalErrorCatalog.INVITE_NOT_FOUND,"Invite not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException inviteInvalid(){return new AppException(ClientPortalErrorCatalog.INVITE_INVALID);}
    public static AppException invalidCredentials(){return new AppException(ClientPortalErrorCatalog.INVALID_CREDENTIALS);}
    public static AppException accessDenied(){return new AppException(ClientPortalErrorCatalog.ACCESS_DENIED);}
    public static AppException projectArchived(UUID id){return new AppException(ClientPortalErrorCatalog.PROJECT_ARCHIVED,"Project archived: "+id,Map.of("projectId",id));}
    public static AppException invalidStatus(String d){return new AppException(ClientPortalErrorCatalog.INVALID_STATUS,d,Map.of());}
    public static AppException titleRequired(){return new AppException(ClientPortalErrorCatalog.TITLE_REQUIRED);}
    public static AppException accountNotSuspendable(){return new AppException(ClientPortalErrorCatalog.ACCOUNT_NOT_SUSPENDABLE);}
    public static AppException accountAlreadyDeactivated(){return new AppException(ClientPortalErrorCatalog.ACCOUNT_ALREADY_DEACTIVATED);}
    public static AppException permissionPolicyNotFound(UUID id){return new AppException(ClientPortalErrorCatalog.PERMISSION_POLICY_NOT_FOUND,"Permission policy not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException permissionPolicyCodeExists(String code){return new AppException(ClientPortalErrorCatalog.PERMISSION_POLICY_CODE_EXISTS,"Permission policy code already exists: "+code,Map.of("code",code==null?"":code));}
}
