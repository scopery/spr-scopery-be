package com.company.scopery.modules.traceability.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class TraceabilityApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    private static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}";
    public static final String FUNCTIONAL_ITEMS = BASE + "/functional-items";
    public static final String FUNCTIONAL_ITEM_BUSINESS_RULES = FUNCTIONAL_ITEMS + "/{functionalItemId}/business-rules";
    public static final String FUNCTIONAL_ITEM_CUSTOM_PROPS = FUNCTIONAL_ITEMS + "/{functionalItemId}/custom-properties";
    public static final String FUNCTIONAL_ITEM_ANCHORS = FUNCTIONAL_ITEMS + "/{functionalItemId}/anchors";
    public static final String NON_FUNCTIONAL_ITEMS = BASE + "/non-functional-items";
    public static final String REQUIREMENTS = BASE + "/requirements";
    public static final String TRACE_LINKS = BASE + "/trace-links";
    public static final String APPLICATIONS = WS + "/applications";
    public static final String APP_MODULES = APPLICATIONS + "/{applicationId}/modules";
    public static final String APP_MODULE_ITEM = WS + "/application-modules";
    public static final String APP_COMPONENTS = APPLICATIONS + "/{applicationId}/components";
    public static final String APP_COMPONENT_ITEM = WS + "/application-components";
    public static final String SCREENS = APPLICATIONS + "/{applicationId}/screens";
    public static final String SCREEN_ITEM = WS + "/screens";
    public static final String SCREEN_SECTIONS = SCREEN_ITEM + "/{screenId}/sections";
    public static final String SCREEN_FIELDS = SCREEN_ITEM + "/{screenId}/fields";
    public static final String SCREEN_ACTIONS = SCREEN_ITEM + "/{screenId}/actions";
    public static final String API_ENDPOINTS = APPLICATIONS + "/{applicationId}/api-endpoints";
    public static final String API_ENDPOINT_ITEM = WS + "/api-endpoints";
    public static final String DATA_ENTITIES = APPLICATIONS + "/{applicationId}/data-entities";
    public static final String DATA_ENTITY_ITEM = WS + "/data-entities";
    public static final String REQUIREMENT_VERSIONS = REQUIREMENTS + "/{requirementId}/versions";
    public static final String REQUIREMENT_SOURCES = REQUIREMENTS + "/{requirementId}/sources";
    public static final String REQUIREMENT_CRITERIA = REQUIREMENTS + "/{requirementId}/acceptance-criteria";
    public static final String PROJECT_FUNCTIONAL_ITEM_ANCHORS = BASE + "/functional-item-anchors";
    public static final String REPORTS = BASE + "/reports";
    public static final String REQUIREMENT_TC_LINKS = REQUIREMENTS + "/{requirementId}/test-case-links";
    public static final String REQUIREMENT_LINKABLE_TCS = REQUIREMENTS + "/{requirementId}/linkable-test-cases";
    public static final String REQUIREMENT_LINKABLE_FUNCTIONS = REQUIREMENTS + "/{requirementId}/linkable-functions";
    public static final String REQUIREMENT_LINKABLE_USE_CASES = REQUIREMENTS + "/{requirementId}/linkable-use-cases";
    public static final String STRUCTURE_RELATIONS = APPLICATIONS + "/{applicationId}/structure-relations";
    public static final String FUNCTION_SCREENS = FUNCTIONAL_ITEMS + "/{functionalItemId}/screens";
    public static final String FUNCTION_APIS = FUNCTIONAL_ITEMS + "/{functionalItemId}/api-endpoints";
    public static final String FUNCTION_COMMUNICATIONS = FUNCTIONAL_ITEMS + "/{functionalItemId}/communications";
    public static final String SCREEN_COMPONENTS = SCREEN_ITEM + "/{screenId}/components";
    public static final String COMMUNICATION_SPECS = APPLICATIONS + "/{applicationId}/communication-specifications";
    public static final String COMMUNICATION_SPEC_ITEM = WS + "/communication-specifications";
    public static final String OVERALL_STRUCTURE = APPLICATIONS + "/{applicationId}/overall-structure";
    public static final String OVERALL_STRUCTURE_CANDIDATES = OVERALL_STRUCTURE + "/candidates";
    public static final String NFR_SCOPE_TARGETS = NON_FUNCTIONAL_ITEMS + "/{nfrId}/scope-targets";
    public static final String FUNCTION_SCREEN_COMPONENTS = FUNCTIONAL_ITEMS + "/{functionalItemId}/screens/{screenId}/components";
    // Use Case CRUD
    public static final String USE_CASES                    = BASE + "/use-cases";
    public static final String USE_CASE                     = USE_CASES + "/{useCaseId}";
    public static final String USE_CASE_NESTED_IMPORT       = USE_CASE + "/nested-import";
    // Use Case sub-resources
    public static final String USE_CASE_CONDITIONS          = USE_CASE + "/conditions";
    public static final String USE_CASE_CONDITION           = USE_CASE_CONDITIONS + "/{conditionId}";
    public static final String USE_CASE_FLOWS               = USE_CASE + "/flows";
    public static final String USE_CASE_FLOW                = USE_CASE + "/flows/{flowId}";
    public static final String USE_CASE_FLOW_STEPS          = USE_CASE_FLOW + "/steps";
    public static final String USE_CASE_FLOW_STEPS_ORDER    = USE_CASE_FLOW_STEPS + "/order";
    public static final String USE_CASE_FLOW_STEP           = USE_CASE_FLOW_STEPS + "/{stepId}";
    public static final String USE_CASE_BUSINESS_RULES      = USE_CASE + "/business-rules";
    public static final String USE_CASE_BUSINESS_RULE       = USE_CASE_BUSINESS_RULES + "/{ruleId}";
    public static final String USE_CASE_CRITERIA            = USE_CASE + "/acceptance-criteria";
    public static final String USE_CASE_CRITERION           = USE_CASE_CRITERIA + "/{criterionId}";
    public static final String USE_CASE_SUPPORTING_FNS      = USE_CASE + "/supporting-functions";
    public static final String USE_CASE_SUPPORTING_FN       = USE_CASE_SUPPORTING_FNS + "/{functionId}";
    public static final String USE_CASE_FLOW_SCOPE          = USE_CASE + "/flow-scope";
    public static final String USE_CASE_MENTION_OPTIONS     = USE_CASE + "/mention-options";
    public static final String USE_CASE_PRIMARY_FN_IMPACT   = USE_CASE + "/primary-function-change-impact";
    // Use Cases scoped under a Function
    public static final String FUNCTION_USE_CASES           = FUNCTIONAL_ITEMS + "/{functionalItemId}/use-cases";
    // Requirement <-> Function links
    public static final String FUNCTION_REQUIREMENTS        = FUNCTIONAL_ITEMS + "/{functionalItemId}/requirements";
    public static final String FUNCTION_REQUIREMENT         = FUNCTION_REQUIREMENTS + "/{requirementId}";
    public static final String FUNCTION_REQUIREMENTS_BULK_LINK = FUNCTION_REQUIREMENTS + "/bulk-link";
    // Requirement <-> Use Case links
    public static final String USE_CASE_REQUIREMENTS        = USE_CASE + "/requirements";
    public static final String USE_CASE_REQUIREMENT         = USE_CASE_REQUIREMENTS + "/{requirementId}";
    // Traceability Coverage
    public static final String TRACEABILITY_COVERAGE_SUMMARY = BASE + "/traceability/coverage-summary";
    public static final String TRACEABILITY_MATRIX           = BASE + "/traceability/matrix";
    public static final String TRACEABILITY_REQ_DETAIL       = BASE + "/traceability/requirements/{requirementId}";
    public static final String TRACEABILITY_REQ_HISTORY      = BASE + "/traceability/requirements/{requirementId}/history";
    public static final String TRACEABILITY_GAPS             = BASE + "/traceability/gaps";
    public static final String TRACEABILITY_OVERVIEW         = BASE + "/traceability/overview";
    public static final String TRACEABILITY_FUNCTIONS        = BASE + "/traceability/functions";
    public static final String TRACEABILITY_USE_CASES        = BASE + "/traceability/use-cases";
    public static final String TRACEABILITY_IMPLEMENTATION   = BASE + "/traceability/implementation";
    public static final String TRACEABILITY_NFR              = BASE + "/traceability/nfr-verification";
    public static final String TRACEABILITY_EXPLORER         = BASE + "/traceability/explorer";
    public static final String REQUIREMENT_REQUIRES_USE_CASE = REQUIREMENTS + "/{requirementId}/requires-use-case";
    // Screen Design Spec
    public static final String SCREEN_MODES              = SCREEN_ITEM + "/{screenId}/modes";
    public static final String DATA_ENTITY_FIELDS        = DATA_ENTITY_ITEM + "/{entityId}/fields";
    public static final String VALIDATION_RULE_TYPES     = WS + "/validation-rule-types";
    public static final String COMPONENT_OPTIONS         = APP_COMPONENT_ITEM + "/{componentId}/options";
    public static final String COMPONENT_FIELDS          = APP_COMPONENT_ITEM + "/{componentId}/fields";
    public static final String COMPONENT_API             = APP_COMPONENT_ITEM + "/{componentId}/apis";
    public static final String SECTION_BIND_COMPONENT    = SCREEN_ITEM + "/{screenId}/sections/{sectionId}/bind-component";
    public static final String SCREEN_FIELD_MODE_CONFIGS = SCREEN_ITEM + "/{screenId}/fields/{fieldId}/mode-configs";
    public static final String SCREEN_FIELD_VALIDATIONS  = SCREEN_ITEM + "/{screenId}/fields/{fieldId}/validations";
    public static final String SCREEN_FULL_SPEC          = SCREEN_ITEM + "/{screenId}/full-spec";
    public static final String SCREEN_SPEC_DOCS          = WS + "/screen-spec-docs";
    public static final String SPEC_DOC_REVISIONS        = WS + "/screen-spec-docs/{documentId}/revisions";
    public static final String SPEC_DOC_FULL_SPEC        = WS + "/screen-spec-docs/{documentId}/full-spec";
    public static final String SCREEN_PROCESS_ITEMS      = SCREEN_ITEM + "/{screenId}/process-items";
    public static final String SCREEN_EVENT_ITEMS        = SCREEN_ITEM + "/{screenId}/event-items";
    public static final String DATA_ENTITY_RELATIONS     = DATA_ENTITY_ITEM + "/{entityId}/relations";
    private TraceabilityApiPaths() {}
}
