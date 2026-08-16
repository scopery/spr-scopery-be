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
    public static AppException screenFieldKeyExists(String key){return new AppException(TraceabilityErrorCatalog.SCREEN_FIELD_KEY_EXISTS,"Screen field key already exists: "+key,Map.of("fieldKey",key));}
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
    public static AppException invalidFunctionScreenRole(String role){return new AppException(TraceabilityErrorCatalog.INVALID_FUNCTION_SCREEN_ROLE,"Invalid function-screen role: "+role,Map.of("role",role==null?"":role));}
    public static AppException functionScreenNotFound(UUID functionId, UUID screenId){return new AppException(TraceabilityErrorCatalog.FUNCTION_SCREEN_NOT_FOUND,"Function-screen link not found",Map.of("functionId",functionId==null?"":functionId,"screenId",screenId==null?"":screenId));}
    public static AppException functionApiDuplicate(){return new AppException(TraceabilityErrorCatalog.FUNCTION_API_DUPLICATE);}
    public static AppException commSpecNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.COMM_SPEC_NOT_FOUND,"Communication specification not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException commSpecCodeExists(String code){return new AppException(TraceabilityErrorCatalog.COMM_SPEC_CODE_EXISTS,"Communication specification code already exists: "+code,Map.of("code",code));}
    public static AppException commSpecNotReady(String reason){return new AppException(TraceabilityErrorCatalog.COMM_SPEC_NOT_READY,reason==null?"Cannot mark READY":reason);}
    public static AppException functionCommDuplicate(){return new AppException(TraceabilityErrorCatalog.FUNCTION_COMM_DUPLICATE);}
    public static AppException functionCommNotFound(UUID functionId, UUID communicationId){return new AppException(TraceabilityErrorCatalog.FUNCTION_COMM_NOT_FOUND,"Function-communication link not found",Map.of("functionId",functionId==null?"":functionId,"communicationId",communicationId==null?"":communicationId));}
    public static AppException functionApiNotFound(UUID functionId, UUID apiEndpointId){return new AppException(TraceabilityErrorCatalog.FUNCTION_API_NOT_FOUND,"Function-api link not found",Map.of("functionId",functionId==null?"":functionId,"apiEndpointId",apiEndpointId==null?"":apiEndpointId));}
    public static AppException screenComponentDuplicate(UUID componentId){return new AppException(TraceabilityErrorCatalog.SCREEN_COMPONENT_DUPLICATE,"Component already linked to this screen: "+componentId,Map.of("componentId",componentId==null?"":componentId));}
    public static AppException screenComponentNotFound(UUID screenId, UUID componentId){return new AppException(TraceabilityErrorCatalog.SCREEN_COMPONENT_NOT_FOUND,"Screen-component link not found",Map.of("screenId",screenId==null?"":screenId,"componentId",componentId==null?"":componentId));}
    public static AppException moduleNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.MODULE_NOT_FOUND,"Module not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException nfrScopeTargetDuplicate(){return new AppException(TraceabilityErrorCatalog.NFR_SCOPE_TARGET_DUPLICATE);}
    public static AppException nfrScopeTargetNotFound(UUID nfrId, UUID targetId){return new AppException(TraceabilityErrorCatalog.NFR_SCOPE_TARGET_NOT_FOUND,"NFR scope target not found",Map.of("nfrId",nfrId==null?"":nfrId,"targetId",targetId==null?"":targetId));}
    public static AppException importInvalidFunctionalItem(String reason){return new AppException(TraceabilityErrorCatalog.FUNCTIONAL_IMPORT_INVALID_ITEM,reason,Map.of());}
    public static AppException importFunctionalItemNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.FUNCTIONAL_IMPORT_ITEM_NOT_FOUND,"Functional item not found in project: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.USE_CASE_NOT_FOUND,"Use case not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseKeyExists(String key){return new AppException(TraceabilityErrorCatalog.USE_CASE_KEY_EXISTS,"Use case key already exists: "+key,Map.of("key",key));}
    public static AppException useCaseFlowNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.USE_CASE_FLOW_NOT_FOUND,"Use case flow not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseFlowStepNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.USE_CASE_FLOW_STEP_NOT_FOUND,"Flow step not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseMainFlowExists(UUID useCaseId){return new AppException(TraceabilityErrorCatalog.USE_CASE_MAIN_FLOW_EXISTS,"Main flow already exists for use case: "+useCaseId,Map.of("useCaseId",useCaseId==null?"":useCaseId));}
    public static AppException useCaseConditionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.USE_CASE_CONDITION_NOT_FOUND,"Use case condition not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseBusinessRuleNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.USE_CASE_BUSINESS_RULE_NOT_FOUND,"Use case business rule not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseCriterionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.USE_CASE_CRITERION_NOT_FOUND,"Acceptance criterion not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException useCaseSupportingFnDuplicate(){return new AppException(TraceabilityErrorCatalog.USE_CASE_SUPPORTING_FN_DUPLICATE);}
    public static AppException useCaseSupportingFnNotFound(UUID useCaseId, UUID functionId){return new AppException(TraceabilityErrorCatalog.USE_CASE_SUPPORTING_FN_NOT_FOUND,"Supporting function link not found",Map.of("useCaseId",useCaseId==null?"":useCaseId,"functionId",functionId==null?"":functionId));}
    public static AppException useCaseScreenNotLinked(UUID screenId){return new AppException(TraceabilityErrorCatalog.USE_CASE_SCREEN_NOT_LINKED,"Screen not linked to primary function: "+screenId,Map.of("screenId",screenId==null?"":screenId));}
    public static AppException useCaseFunctionRequired(UUID useCaseId){return new AppException(TraceabilityErrorCatalog.USE_CASE_FUNCTION_REQUIRED,"Use case must have a parent Function",Map.of("useCaseId",useCaseId==null?"":useCaseId));}
    public static AppException requirementFunctionDuplicate(){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_FUNCTION_DUPLICATE);}
    public static AppException requirementFunctionNotFound(UUID reqId, UUID fnId){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_FUNCTION_NOT_FOUND,"Requirement-function link not found",Map.of("requirementId",reqId==null?"":reqId,"functionId",fnId==null?"":fnId));}
    public static AppException requirementUseCaseDuplicate(){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_USE_CASE_DUPLICATE);}
    public static AppException requirementUseCaseNotFound(UUID reqId, UUID ucId){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_USE_CASE_NOT_FOUND,"Requirement-use case link not found",Map.of("requirementId",reqId==null?"":reqId,"useCaseId",ucId==null?"":ucId));}
    public static AppException requirementHasLinks(UUID id){return new AppException(TraceabilityErrorCatalog.REQUIREMENT_HAS_LINKS,"Requirement still has active links: "+id,Map.of("id",id==null?"":id));}
    // Screen Design Spec
    public static AppException screenModeNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_MODE_NOT_FOUND,"Screen mode not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenModeCodeExists(String code){return new AppException(TraceabilityErrorCatalog.SCREEN_MODE_CODE_EXISTS,"Screen mode code already exists: "+code,Map.of("code",code));}
    public static AppException screenModeWrongScreen(UUID modeId){return new AppException(TraceabilityErrorCatalog.SCREEN_MODE_WRONG_SCREEN,"Mode does not belong to the specified screen: "+modeId,Map.of("modeId",modeId==null?"":modeId));}
    public static AppException screenModeInactive(UUID modeId){return new AppException(TraceabilityErrorCatalog.SCREEN_MODE_INACTIVE,"Screen mode is inactive: "+modeId,Map.of("modeId",modeId==null?"":modeId));}
    public static AppException dataEntityFieldNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_FIELD_NOT_FOUND,"Data entity field not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException dataEntityFieldColumnExists(String col){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_FIELD_COLUMN_EXISTS,"Column already exists: "+col,Map.of("columnName",col));}
    public static AppException dataEntityFieldColumnNotFound(String col){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_FIELD_COLUMN_NOT_FOUND,"Column not found: "+col,Map.of("columnName",col));}
    public static AppException dataEntityNotActive(UUID id){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_NOT_ACTIVE,"Data entity is not active: "+id,Map.of("id",id==null?"":id));}
    public static AppException filterFieldNotInEntity(String field){return new AppException(TraceabilityErrorCatalog.FILTER_FIELD_NOT_IN_ENTITY,"Filter field not in entity: "+field,Map.of("field",field));}
    public static AppException validationRuleTypeNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.VALIDATION_RULE_TYPE_NOT_FOUND,"Validation rule type not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException validationRuleTypeCodeExists(String code){return new AppException(TraceabilityErrorCatalog.VALIDATION_RULE_TYPE_CODE_EXISTS,"Validation rule type code already exists: "+code,Map.of("code",code==null?"":code));}
    public static AppException fieldValidationNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.FIELD_VALIDATION_NOT_FOUND,"Field validation not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException fieldValidationRuleParamInvalid(String reason){return new AppException(TraceabilityErrorCatalog.FIELD_VALIDATION_RULE_PARAM_INVALID,reason,Map.of());}
    public static AppException componentOptionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.COMPONENT_OPTION_NOT_FOUND,"Component option not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException componentOptionValueExists(String val){return new AppException(TraceabilityErrorCatalog.COMPONENT_OPTION_VALUE_EXISTS,"Option value already exists: "+val,Map.of("optionValue",val));}
    public static AppException componentSourceTypeNotStatic(UUID id){return new AppException(TraceabilityErrorCatalog.COMPONENT_SOURCE_TYPE_NOT_STATIC,"Component option_source_type must be STATIC: "+id,Map.of("componentId",id==null?"":id));}
    public static AppException componentDifferentApplication(UUID id){return new AppException(TraceabilityErrorCatalog.COMPONENT_DIFFERENT_APPLICATION,"Component belongs to a different application: "+id,Map.of("componentId",id==null?"":id));}
    public static AppException dataEntityFieldDifferentApplication(UUID id){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_FIELD_DIFFERENT_APPLICATION,"Data entity field belongs to a different application: "+id,Map.of("dataEntityFieldId",id==null?"":id));}
    public static AppException fieldModeConfigNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.FIELD_MODE_CONFIG_NOT_FOUND,"Field mode config not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException modeConfigPayloadEmpty(){return new AppException(TraceabilityErrorCatalog.MODE_CONFIG_PAYLOAD_EMPTY);}
    public static AppException screenSpecDocNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_SPEC_DOC_NOT_FOUND,"Screen spec document not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenSpecDocCodeExists(String code){return new AppException(TraceabilityErrorCatalog.SCREEN_SPEC_DOC_CODE_EXISTS,"Document code already exists: "+code,Map.of("code",code));}
    public static AppException specDocScreenDuplicate(){return new AppException(TraceabilityErrorCatalog.SPEC_DOC_SCREEN_DUPLICATE);}
    public static AppException specDocScreenNotFound(UUID documentId, UUID screenId){return new AppException(TraceabilityErrorCatalog.SPEC_DOC_SCREEN_NOT_FOUND,"Screen not found in document",Map.of("documentId",documentId==null?"":documentId,"screenId",screenId==null?"":screenId));}
    public static AppException specDocRevisionNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SPEC_DOC_REVISION_NOT_FOUND,"Revision not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenProcessItemNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_PROCESS_ITEM_NOT_FOUND,"Process item not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException screenEventItemNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.SCREEN_EVENT_ITEM_NOT_FOUND,"Event item not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException componentFieldNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.COMPONENT_FIELD_NOT_FOUND,"Component field not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException componentFieldKeyExists(String key){return new AppException(TraceabilityErrorCatalog.COMPONENT_FIELD_KEY_EXISTS,"Field key already exists: "+key,Map.of("fieldKey",key));}
    public static AppException componentApiNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.COMPONENT_API_NOT_FOUND,"Component API link not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException componentApiDuplicate(UUID componentId,UUID apiId,String role){return new AppException(TraceabilityErrorCatalog.COMPONENT_API_DUPLICATE,"API already linked with role "+role,Map.of("componentId",componentId,"apiId",apiId,"role",role));}
    public static AppException apiEndpointNotInWorkspace(UUID apiId){return new AppException(TraceabilityErrorCatalog.API_ENDPOINT_NOT_IN_WORKSPACE,"API endpoint not found: "+apiId,Map.of("apiId",apiId==null?"":apiId));}
    public static AppException dataEntityRelationNotFound(UUID id){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_RELATION_NOT_FOUND,"Data entity relation not found: "+id,Map.of("id",id==null?"":id));}
    public static AppException dataEntityRelationDuplicate(UUID sourceEntityId, UUID targetEntityId, String relationType){return new AppException(TraceabilityErrorCatalog.DATA_ENTITY_RELATION_DUPLICATE,"Relation already exists",Map.of("sourceEntityId",sourceEntityId,"targetEntityId",targetEntityId,"relationType",relationType));}
    public static AppException uploadFileTooLarge(long actualBytes, long maxBytes){return new AppException(TraceabilityErrorCatalog.UPLOAD_FILE_TOO_LARGE,"File size "+actualBytes+" bytes exceeds maximum "+maxBytes+" bytes",Map.of("actualSizeBytes",String.valueOf(actualBytes),"maxSizeBytes",String.valueOf(maxBytes)));}
    public static AppException uploadObjectNotFound(String key){return new AppException(TraceabilityErrorCatalog.UPLOAD_OBJECT_NOT_FOUND,"Object not found in storage: "+key,Map.of("objectKey",key==null?"":key));}
    public static AppException uploadInvalidContentType(String ct){return new AppException(TraceabilityErrorCatalog.UPLOAD_INVALID_CONTENT_TYPE,"Content type not allowed: "+ct,Map.of("contentType",ct==null?"":ct));}
}
