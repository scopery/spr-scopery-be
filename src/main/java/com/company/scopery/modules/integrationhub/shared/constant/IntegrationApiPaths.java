package com.company.scopery.modules.integrationhub.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class IntegrationApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH;
    private static final String WS = BASE + "/workspaces/{workspaceId}/integrations";

    // Providers (platform-wide, no workspace)
    public static final String PROVIDERS = BASE + "/integrations/providers";
    public static final String PROVIDER_BY_CODE = BASE + "/integrations/providers/{providerCode}";
    public static final String PROVIDER_CAPABILITIES = BASE + "/integrations/providers/{providerCode}/capabilities";
    public static final String ADMIN_PROVIDERS_SEED = BASE + "/admin/integrations/providers/seed";

    // Connections
    public static final String CONNECTIONS = WS + "/connections";
    public static final String CONNECTION_BY_ID = WS + "/connections/{connectionId}";
    public static final String CONNECTION_ENABLE = WS + "/connections/{connectionId}/enable";
    public static final String CONNECTION_DISABLE = WS + "/connections/{connectionId}/disable";
    public static final String CONNECTION_ARCHIVE = WS + "/connections/{connectionId}/archive";
    public static final String CONNECTION_HEALTH = WS + "/connections/{connectionId}/health-check";
    public static final String CONNECTION_HEALTH_LIST = WS + "/connections/{connectionId}/health-checks";
    public static final String CONNECTION_TEST = WS + "/connections/{connectionId}/test-connection";
    public static final String CONNECTION_SYNC = WS + "/connections/{connectionId}/sync-pull";

    // Credentials
    public static final String CREDENTIALS = WS + "/credential-references";
    public static final String CREDENTIAL_BY_ID = WS + "/credential-references/{credentialId}";
    public static final String CREDENTIAL_ROTATE = WS + "/credential-references/{credentialId}/rotate";
    public static final String CREDENTIAL_REVOKE = WS + "/credential-references/{credentialId}/revoke";

    // Mapping profiles + external id mappings
    public static final String MAPPING_PROFILES = WS + "/mapping-profiles";
    public static final String MAPPING_PROFILE_BY_ID = WS + "/mapping-profiles/{mappingProfileId}";
    public static final String MAPPING_PROFILE_ARCHIVE = WS + "/mapping-profiles/{mappingProfileId}/archive";
    public static final String EXTERNAL_ID_MAPPINGS = WS + "/external-id-mappings";

    // Import templates
    public static final String IMPORT_TEMPLATES = WS + "/import-templates";
    public static final String IMPORT_TEMPLATE_BY_ID = WS + "/import-templates/{templateId}";

    // Import jobs
    public static final String IMPORT_JOBS = WS + "/import-jobs";
    public static final String IMPORT_JOB_BY_ID = WS + "/import-jobs/{importJobId}";
    public static final String IMPORT_JOB_VALIDATE = WS + "/import-jobs/{importJobId}/validate";
    public static final String IMPORT_JOB_DRY_RUN = WS + "/import-jobs/{importJobId}/dry-run";
    public static final String IMPORT_JOB_EXECUTE = WS + "/import-jobs/{importJobId}/execute";
    public static final String IMPORT_JOB_CANCEL = WS + "/import-jobs/{importJobId}/cancel";
    public static final String IMPORT_JOB_ROWS = WS + "/import-jobs/{importJobId}/rows";

    // Export profiles
    public static final String EXPORT_PROFILES = WS + "/export-profiles";
    public static final String EXPORT_PROFILE_BY_ID = WS + "/export-profiles/{exportProfileId}";
    public static final String EXPORT_PROFILE_ARCHIVE = WS + "/export-profiles/{exportProfileId}/archive";

    // Export jobs
    public static final String EXPORT_JOBS = WS + "/export-jobs";
    public static final String EXPORT_JOB_BY_ID = WS + "/export-jobs/{exportJobId}";
    public static final String EXPORT_JOB_CANCEL = WS + "/export-jobs/{exportJobId}/cancel";
    public static final String EXPORT_JOB_DOWNLOAD = WS + "/export-jobs/{exportJobId}/download";

    // Webhooks
    public static final String WEBHOOKS = WS + "/webhooks/subscriptions";
    public static final String WEBHOOK_BY_ID = WS + "/webhooks/subscriptions/{subscriptionId}";
    public static final String WEBHOOK_ENABLE = WS + "/webhooks/subscriptions/{subscriptionId}/enable";
    public static final String WEBHOOK_DISABLE = WS + "/webhooks/subscriptions/{subscriptionId}/disable";
    public static final String WEBHOOK_ARCHIVE = WS + "/webhooks/subscriptions/{subscriptionId}/archive";
    public static final String WEBHOOK_DELIVERIES = WS + "/webhooks/delivery-attempts";
    public static final String WEBHOOK_DELIVERY_RETRY = WS + "/webhooks/delivery-attempts/{attemptId}/retry";

    // Inbound
    public static final String INBOUND = BASE + "/integrations/inbound/{endpointCode}";
    public static final String INBOUND_ENDPOINTS = WS + "/inbound-endpoints";
    public static final String INBOUND_EVENTS = WS + "/inbound-events";

    // Sync
    public static final String SYNC_JOBS = WS + "/sync-jobs";
    public static final String SYNC_JOB_BY_ID = WS + "/sync-jobs/{syncJobId}";
    public static final String SYNC_JOB_ENABLE = WS + "/sync-jobs/{syncJobId}/enable";
    public static final String SYNC_JOB_DISABLE = WS + "/sync-jobs/{syncJobId}/disable";
    public static final String SYNC_JOB_ARCHIVE = WS + "/sync-jobs/{syncJobId}/archive";
    public static final String SYNC_RUN_NOW = WS + "/sync-jobs/{syncJobId}/run-now";
    public static final String SYNC_RUNS = WS + "/sync-runs";
    public static final String SYNC_RUN_BY_ID = WS + "/sync-runs/{syncRunId}";
    public static final String SYNC_CONFLICTS = WS + "/sync-conflicts";
    public static final String SYNC_CONFLICT_RESOLVE = WS + "/sync-conflicts/{conflictId}/resolve";
    public static final String SYNC_CONFLICT_DISMISS = WS + "/sync-conflicts/{conflictId}/dismiss";

    // Rate limits
    public static final String RATE_LIMITS = WS + "/rate-limits";

    // Dead letters
    public static final String DEAD_LETTERS = WS + "/dead-letter-events";
    public static final String DEAD_LETTER_RETRY = WS + "/dead-letter-events/{deadLetterId}/retry";
    public static final String DEAD_LETTER_RESOLVE = WS + "/dead-letter-events/{deadLetterId}/resolve";

    // Dashboard
    public static final String DASHBOARD = WS + "/dashboard";

    private IntegrationApiPaths(){}
}
