package com.company.scopery.modules.integrationhub.shared.constant;
public final class IntegrationTableNames {
    public static final String PROVIDER = "integration_provider";
    public static final String CAPABILITY = "integration_connector_capability";
    public static final String CONNECTION = "integration_connection";
    public static final String HEALTH_CHECK = "integration_connection_health_check";
    public static final String CREDENTIAL = "integration_credential_reference";
    public static final String MAPPING_PROFILE = "integration_data_mapping_profile";
    public static final String EXTERNAL_ID_MAPPING = "integration_external_id_mapping";
    public static final String IMPORT_TEMPLATE = "integration_import_template";
    public static final String IMPORT_JOB = "integration_import_job";
    public static final String IMPORT_ROW = "integration_import_row_result";
    public static final String EXPORT_PROFILE = "integration_export_profile";
    public static final String EXPORT_JOB = "integration_export_job";
    public static final String WEBHOOK_SUB = "integration_webhook_subscription";
    public static final String WEBHOOK_DELIVERY = "integration_webhook_delivery_attempt";
    public static final String INBOUND_ENDPOINT = "integration_inbound_webhook_endpoint";
    public static final String INBOUND_EVENT = "integration_inbound_webhook_event";
    public static final String SYNC_JOB = "integration_sync_job";
    public static final String SYNC_RUN = "integration_sync_run";
    public static final String SYNC_CURSOR = "integration_sync_cursor";
    public static final String SYNC_CONFLICT = "integration_sync_conflict";
    public static final String RATE_LIMIT = "integration_provider_rate_limit_state";
    public static final String DEAD_LETTER = "integration_dead_letter_event";
    private IntegrationTableNames(){}
}
