package com.company.scopery.modules.traceability.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class TraceabilityExceptions {
    private TraceabilityExceptions(){}
    public static AppException requirementNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_NOT_FOUND,"Requirement not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException requirementImmutable(){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_IMMUTABLE);}
    public static AppException requirementInvalidStatus(String s){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_INVALID_STATUS,"Invalid transition from: "+s,Map.of("status",s));}
    public static AppException requirementVersionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_VERSION_NOT_FOUND,"Requirement version not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException requirementSourceNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_SOURCE_NOT_FOUND,"Requirement source not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException requirementCriteriaNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_CRITERIA_NOT_FOUND,"Criteria not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException traceLinkNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.TRACE_LINK_NOT_FOUND,"Trace link not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException traceLinkDuplicate(){return new AppException(TraceabilityErrorCatalog.TRACE_LINK_DUPLICATE);}
    public static AppException applicationNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.APPLICATION_NOT_FOUND,"Application not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException appModuleNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.APP_MODULE_NOT_FOUND,"Module not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException appComponentNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.APP_COMPONENT_NOT_FOUND,"Component not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_NOT_FOUND,"Screen not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenSectionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_SECTION_NOT_FOUND,"Section not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenFieldNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_FIELD_NOT_FOUND,"Field not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenActionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_ACTION_NOT_FOUND,"Action not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException apiEndpointNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.API_ENDPOINT_NOT_FOUND,"API endpoint not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException dataEntityNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_NOT_FOUND,"Data entity not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException accessDenied(){return new AppException(TraceabilityErrorCatalog.ACCESS_DENIED);}
    public static AppException projectArchived(UUID id){return new AppException(TraceabilityErrorCatalog.PROJECT_ARCHIVED,"Project archived: "+id,Map.of("projectId",id));}
    public static AppException titleRequired(){return new AppException(TraceabilityErrorCatalog.TITLE_REQUIRED);}
}
