package com.company.scopery.modules.configuration.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ConfigurationApiPaths {
    public static final String OBJECT_TYPES = ApiPaths.BASE_PATH + "/config/object-types";
    public static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/config";
    public static final String CUSTOM_FIELDS = WS + "/custom-fields";
    public static final String FIELD_VALUES = WS + "/custom-field-values";
    public static final String FIELD_VALIDATION_RULES = WS + "/custom-field-validation-rules";
    public static final String FORMS = WS + "/forms";
    public static final String FORM_SECTIONS = WS + "/form-sections";
    public static final String FORM_FIELDS = WS + "/form-fields";
    public static final String FORM_SUBMISSIONS = WS + "/form-submissions";
    public static final String TAGS = WS + "/tags";
    public static final String TAG_ASSIGNMENTS = WS + "/tag-assignments";
    public static final String TAXONOMIES = WS + "/taxonomies";
    public static final String STATUS_SETS = WS + "/status-sets";
    public static final String LAYOUTS = WS + "/layouts";
    public static final String FIELD_VISIBILITY_POLICIES = CUSTOM_FIELDS + "/{fieldId}/visibility-policies";
    public static final String PROJECT_CUSTOM_FIELDS = ApiPaths.BASE_PATH + "/projects/{projectId}/custom-fields";
    private ConfigurationApiPaths(){}
}
