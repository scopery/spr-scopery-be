package com.company.scopery.modules.traceability.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class TraceabilityApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    private static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}";
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
    public static final String REPORTS = BASE + "/reports";
    private TraceabilityApiPaths() {}
}
