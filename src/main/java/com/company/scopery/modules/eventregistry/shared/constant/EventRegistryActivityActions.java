package com.company.scopery.modules.eventregistry.shared.constant;

public final class EventRegistryActivityActions {

    public static final String CREATE_EVENT_DEFINITION     = "EVENT_DEFINITION_CREATED";
    public static final String UPDATE_EVENT_DEFINITION     = "EVENT_DEFINITION_UPDATED";
    public static final String ACTIVATE_EVENT_DEFINITION   = "EVENT_DEFINITION_ACTIVATED";
    public static final String DEACTIVATE_EVENT_DEFINITION = "EVENT_DEFINITION_DEACTIVATED";
    public static final String DEPRECATE_EVENT_DEFINITION  = "EVENT_DEFINITION_DEPRECATED";
    public static final String UPSERT_EVENT_VARIABLES      = "EVENT_VARIABLES_UPSERTED";
    public static final String EVENT_SEEDER_RAN            = "EVENT_SEEDER_RAN";
    public static final String EVENT_SEEDER_DRIFT_DETECTED = "EVENT_SEEDER_DRIFT_DETECTED";

    private EventRegistryActivityActions() {}
}
