package com.company.scopery.modules.traceability.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum TraceabilityErrorCatalog implements ErrorCatalog {
    REQUIREMENT_NOT_FOUND("REQUIREMENT_NOT_FOUND", "Requirement not found", HttpStatus.NOT_FOUND),
    REQUIREMENT_CODE_EXISTS("REQUIREMENT_CODE_ALREADY_EXISTS", "Requirement code already exists", HttpStatus.CONFLICT),
    REQUIREMENT_IMMUTABLE("REQUIREMENT_APPROVED_IMMUTABLE", "Approved requirement is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    REQUIREMENT_INVALID_STATUS("REQUIREMENT_INVALID_STATUS", "Invalid requirement status transition", HttpStatus.UNPROCESSABLE_ENTITY),
    REQUIREMENT_VERSION_NOT_FOUND("REQUIREMENT_VERSION_NOT_FOUND", "Requirement version not found", HttpStatus.NOT_FOUND),
    REQUIREMENT_SOURCE_NOT_FOUND("REQUIREMENT_SOURCE_NOT_FOUND", "Requirement source not found", HttpStatus.NOT_FOUND),
    REQUIREMENT_CRITERIA_NOT_FOUND("REQUIREMENT_CRITERIA_NOT_FOUND", "Requirement criteria not found", HttpStatus.NOT_FOUND),
    TRACE_LINK_NOT_FOUND("TRACE_LINK_NOT_FOUND", "Trace link not found", HttpStatus.NOT_FOUND),
    TRACE_LINK_DUPLICATE("TRACE_LINK_DUPLICATE", "Duplicate active trace link", HttpStatus.CONFLICT),
    APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "Application not found", HttpStatus.NOT_FOUND),
    APPLICATION_CODE_EXISTS("APPLICATION_CODE_ALREADY_EXISTS", "Application code already exists", HttpStatus.CONFLICT),
    APP_MODULE_NOT_FOUND("APPLICATION_MODULE_NOT_FOUND", "Application module not found", HttpStatus.NOT_FOUND),
    APP_MODULE_CODE_EXISTS("APPLICATION_MODULE_CODE_ALREADY_EXISTS", "Module code already exists", HttpStatus.CONFLICT),
    APP_COMPONENT_NOT_FOUND("APPLICATION_COMPONENT_NOT_FOUND", "Application component not found", HttpStatus.NOT_FOUND),
    APP_COMPONENT_CODE_EXISTS("APPLICATION_COMPONENT_CODE_ALREADY_EXISTS", "Component code already exists", HttpStatus.CONFLICT),
    SCREEN_NOT_FOUND("SCREEN_NOT_FOUND", "Screen not found", HttpStatus.NOT_FOUND),
    SCREEN_CODE_EXISTS("SCREEN_CODE_ALREADY_EXISTS", "Screen code already exists", HttpStatus.CONFLICT),
    SCREEN_SECTION_NOT_FOUND("SCREEN_SECTION_NOT_FOUND", "Screen section not found", HttpStatus.NOT_FOUND),
    SCREEN_FIELD_NOT_FOUND("SCREEN_FIELD_NOT_FOUND", "Screen field not found", HttpStatus.NOT_FOUND),
    SCREEN_FIELD_KEY_EXISTS("SCREEN_FIELD_KEY_ALREADY_EXISTS", "Screen field key already exists", HttpStatus.CONFLICT),
    SCREEN_ACTION_NOT_FOUND("SCREEN_ACTION_NOT_FOUND", "Screen action not found", HttpStatus.NOT_FOUND),
    SCREEN_ACTION_CODE_EXISTS("SCREEN_ACTION_CODE_ALREADY_EXISTS", "Screen action code already exists", HttpStatus.CONFLICT),
    API_ENDPOINT_NOT_FOUND("API_ENDPOINT_NOT_FOUND", "API endpoint not found", HttpStatus.NOT_FOUND),
    API_ENDPOINT_DUPLICATE("API_ENDPOINT_DUPLICATE", "Duplicate API endpoint method+path", HttpStatus.CONFLICT),
    DATA_ENTITY_NOT_FOUND("DATA_ENTITY_NOT_FOUND", "Data entity not found", HttpStatus.NOT_FOUND),
    DATA_ENTITY_CODE_EXISTS("DATA_ENTITY_CODE_ALREADY_EXISTS", "Data entity code already exists", HttpStatus.CONFLICT),
    ACCESS_DENIED("TRACEABILITY_ACCESS_DENIED", "Traceability access denied", HttpStatus.FORBIDDEN),
    PROJECT_ARCHIVED("TRACEABILITY_PROJECT_ARCHIVED", "Project archived", HttpStatus.UNPROCESSABLE_ENTITY),
    TITLE_REQUIRED("REQUIREMENT_TITLE_REQUIRED", "Title required", HttpStatus.BAD_REQUEST),
    FUNCTIONAL_ITEM_NOT_FOUND("FUNCTIONAL_ITEM_NOT_FOUND", "Functional item not found", HttpStatus.NOT_FOUND),
    FUNCTIONAL_ITEM_CODE_EXISTS("FUNCTIONAL_ITEM_CODE_ALREADY_EXISTS", "Functional item code already exists", HttpStatus.CONFLICT),
    NON_FUNCTIONAL_ITEM_NOT_FOUND("NON_FUNCTIONAL_ITEM_NOT_FOUND", "Non-functional item not found", HttpStatus.NOT_FOUND),
    NON_FUNCTIONAL_ITEM_CODE_EXISTS("NON_FUNCTIONAL_ITEM_CODE_ALREADY_EXISTS", "Non-functional item code already exists", HttpStatus.CONFLICT),
    BUSINESS_RULE_NOT_FOUND("BUSINESS_RULE_NOT_FOUND", "Business rule not found", HttpStatus.NOT_FOUND),
    BUSINESS_RULE_CODE_EXISTS("BUSINESS_RULE_CODE_ALREADY_EXISTS", "Business rule code already exists", HttpStatus.CONFLICT),
    FUNC_ITEM_CUSTOM_PROP_NOT_FOUND("FUNC_ITEM_CUSTOM_PROP_NOT_FOUND", "Custom property not found", HttpStatus.NOT_FOUND),
    FUNC_ITEM_CUSTOM_PROP_KEY_EXISTS("FUNC_ITEM_CUSTOM_PROP_KEY_EXISTS", "Custom property key already exists", HttpStatus.CONFLICT),
    FUNC_ITEM_ANCHOR_NOT_FOUND("FUNC_ITEM_ANCHOR_NOT_FOUND", "Anchor not found", HttpStatus.NOT_FOUND),
    FUNC_ITEM_ANCHOR_DUPLICATE("FUNC_ITEM_ANCHOR_DUPLICATE", "Duplicate anchor for this node", HttpStatus.CONFLICT),
    STRUCTURE_RELATION_NOT_FOUND("STRUCTURE_RELATION_NOT_FOUND", "Structure relation not found", HttpStatus.NOT_FOUND),
    STRUCTURE_RELATION_DUPLICATE("STRUCTURE_RELATION_DUPLICATE", "Relation between these nodes already exists", HttpStatus.CONFLICT),
    FUNCTION_SCREEN_DUPLICATE("FUNCTION_SCREEN_DUPLICATE", "Screen already linked to this function", HttpStatus.CONFLICT),
    FUNCTION_SCREEN_NOT_FOUND("FUNCTION_SCREEN_NOT_FOUND", "Function-screen link not found", HttpStatus.NOT_FOUND),
    FUNCTION_API_DUPLICATE("FUNCTION_API_DUPLICATE", "API endpoint already linked to this function", HttpStatus.CONFLICT),
    FUNCTION_API_NOT_FOUND("FUNCTION_API_NOT_FOUND", "Function-api link not found", HttpStatus.NOT_FOUND),
    SCREEN_COMPONENT_DUPLICATE("SCREEN_COMPONENT_DUPLICATE", "Component already linked to this screen", HttpStatus.CONFLICT),
    SCREEN_COMPONENT_NOT_FOUND("SCREEN_COMPONENT_NOT_FOUND", "Screen-component link not found", HttpStatus.NOT_FOUND),
    MODULE_NOT_FOUND("APP_MODULE_NOT_FOUND_FOR_LINK", "Module not found", HttpStatus.NOT_FOUND),
    STRUCTURE_RELATION_SELF_LOOP("STRUCTURE_RELATION_SELF_LOOP", "A node cannot be related to itself", HttpStatus.UNPROCESSABLE_ENTITY),
    NFR_SCOPE_TARGET_DUPLICATE("NFR_SCOPE_TARGET_DUPLICATE", "This target is already linked to the NFR", HttpStatus.CONFLICT),
    NFR_SCOPE_TARGET_NOT_FOUND("NFR_SCOPE_TARGET_NOT_FOUND", "NFR scope target link not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    TraceabilityErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
