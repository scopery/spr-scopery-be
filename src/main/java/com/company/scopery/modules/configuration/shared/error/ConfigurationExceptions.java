package com.company.scopery.modules.configuration.shared.error;
import com.company.scopery.common.exception.AppException; import java.util.Map; import java.util.UUID;
public final class ConfigurationExceptions {
    private ConfigurationExceptions(){}
    public static AppException fieldNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.FIELD_NOT_FOUND,"Field not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException fieldKeyExists(String key){return new AppException(ConfigurationErrorCatalog.FIELD_KEY_EXISTS,"Key exists: "+key,Map.of("key",key==null?"":key));}
    public static AppException optionNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.OPTION_NOT_FOUND,"Option not found: "+id,Map.of("id",id));}
    public static AppException formNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.FORM_NOT_FOUND,"Form not found: "+id,Map.of("id",id));}
    public static AppException formVersionNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.FORM_VERSION_NOT_FOUND,"Form version not found: "+id,Map.of("id",id));}
    public static AppException formNotPublished(){return new AppException(ConfigurationErrorCatalog.FORM_NOT_PUBLISHED);}
    public static AppException formVersionImmutable(){return new AppException(ConfigurationErrorCatalog.FORM_VERSION_IMMUTABLE);}
    public static AppException tagNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.TAG_NOT_FOUND,"Tag not found: "+id,Map.of("id",id));}
    public static AppException taxonomyNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.TAXONOMY_NOT_FOUND,"Taxonomy not found: "+id,Map.of("id",id));}
    public static AppException statusSetNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.STATUS_SET_NOT_FOUND,"Status set not found: "+id,Map.of("id",id));}
    public static AppException layoutNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.LAYOUT_NOT_FOUND,"Layout not found: "+id,Map.of("id",id));}
    public static AppException submissionNotFound(UUID id){return new AppException(ConfigurationErrorCatalog.SUBMISSION_NOT_FOUND,"Submission not found: "+id,Map.of("id",id));}
    public static AppException accessDenied(){return new AppException(ConfigurationErrorCatalog.CONFIG_ACCESS_DENIED);}
    public static AppException nameRequired(){return new AppException(ConfigurationErrorCatalog.CONFIG_NAME_REQUIRED);}
    public static AppException validationFailed(String msg){return new AppException(ConfigurationErrorCatalog.VALIDATION_FAILED,msg);}
}
