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
    public static AppException functionalItemNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.FUNCTIONAL_ITEM_NOT_FOUND,"Functional item not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException functionalItemCodeExists(String code){return new AppException(TraceabilityErrorCatalog.FUNCTIONAL_ITEM_CODE_EXISTS,"Functional item code already exists: "+code,Map.of("code",code));}
    public static AppException nonFunctionalItemNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.NON_FUNCTIONAL_ITEM_NOT_FOUND,"Non-functional item not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException nonFunctionalItemCodeExists(String code){return new AppException(TraceabilityErrorCatalog.NON_FUNCTIONAL_ITEM_CODE_EXISTS,"Non-functional item code already exists: "+code,Map.of("code",code));}
    public static AppException businessRuleNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.BUSINESS_RULE_NOT_FOUND,"Business rule not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException businessRuleCodeExists(String code){return new AppException(TraceabilityErrorCatalog.BUSINESS_RULE_CODE_EXISTS,"Business rule code already exists: "+code,Map.of("code",code));}
    public static AppException funcItemCustomPropNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.FUNC_ITEM_CUSTOM_PROP_NOT_FOUND,"Custom property not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException funcItemCustomPropKeyExists(String key){return new AppException(TraceabilityErrorCatalog.FUNC_ITEM_CUSTOM_PROP_KEY_EXISTS,"Custom property key already exists: "+key,Map.of("key",key));}
    public static AppException funcItemAnchorNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.FUNC_ITEM_ANCHOR_NOT_FOUND,"Anchor not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException funcItemAnchorDuplicate(){return new AppException(TraceabilityErrorCatalog.FUNC_ITEM_ANCHOR_DUPLICATE);}
    public static AppException structureRelationNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.STRUCTURE_RELATION_NOT_FOUND,id.toString());}
    public static AppException structureRelationDuplicate(){return new AppException(TraceabilityErrorCatalog.STRUCTURE_RELATION_DUPLICATE);}
    public static AppException structureRelationSelfLoop(){return new AppException(TraceabilityErrorCatalog.STRUCTURE_RELATION_SELF_LOOP);}
    public static AppException functionScreenDuplicate(){return new AppException(TraceabilityErrorCatalog.FUNCTION_SCREEN_DUPLICATE);}
    public static AppException functionScreenNotFound(UUID functionId, UUID screenId){return new AppException(TraceabilityErrorCatalog.FUNCTION_SCREEN_NOT_FOUND,"Function-screen link not found",Map.of("functionId",functionId==null?"":functionId,"screenId",screenId==null?"":screenId));}
    public static AppException functionApiDuplicate(){return new AppException(TraceabilityErrorCatalog.FUNCTION_API_DUPLICATE);}
    public static AppException functionApiNotFound(UUID functionId, UUID apiEndpointId){return new AppException(TraceabilityErrorCatalog.FUNCTION_API_NOT_FOUND,"Function-api link not found",Map.of("functionId",functionId==null?"":functionId,"apiEndpointId",apiEndpointId==null?"":apiEndpointId));}
    public static AppException screenComponentDuplicate(){return new AppException(TraceabilityErrorCatalog.SCREEN_COMPONENT_DUPLICATE);}
    public static AppException screenComponentNotFound(UUID screenId, UUID componentId){return new AppException(TraceabilityErrorCatalog.SCREEN_COMPONENT_NOT_FOUND,"Screen-component link not found",Map.of("screenId",screenId==null?"":screenId,"componentId",componentId==null?"":componentId));}
    public static AppException moduleNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.MODULE_NOT_FOUND,"Module not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException nfrScopeTargetDuplicate(){return new AppException(TraceabilityErrorCatalog.NFR_SCOPE_TARGET_DUPLICATE);}
    public static AppException nfrScopeTargetNotFound(UUID nfrId, UUID targetId){return new AppException(TraceabilityErrorCatalog.NFR_SCOPE_TARGET_NOT_FOUND,"NFR scope target not found",Map.of("nfrId",nfrId==null?"":nfrId,"targetId",targetId==null?"":targetId));}
}
