package com.company.scopery.modules.integrationhub.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class IntegrationExceptions {
    private IntegrationExceptions(){}
    public static AppException accessDenied(){ return new AppException(IntegrationErrorCatalog.INTEGRATION_ACCESS_DENIED); }
    public static AppException providerNotFound(String code){ return new AppException(IntegrationErrorCatalog.INTEGRATION_PROVIDER_NOT_FOUND,"Provider not found: "+code, Map.of("providerCode",code)); }
    public static AppException connectionNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.INTEGRATION_CONNECTION_NOT_FOUND,"Connection not found: "+id, Map.of("id",id)); }
    public static AppException connectionInvalidStatus(String currentStatus, String reason){ return new AppException(IntegrationErrorCatalog.INTEGRATION_CONNECTION_INVALID_STATUS, reason, Map.of("currentStatus",currentStatus)); }
    public static AppException secretNotReturnable(){ return new AppException(IntegrationErrorCatalog.INTEGRATION_SECRET_VALUE_NOT_RETURNABLE); }
    public static AppException credentialNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.INTEGRATION_CREDENTIAL_NOT_FOUND,"Credential not found: "+id, Map.of("id",id)); }
    public static AppException credentialRevoked(UUID id){ return new AppException(IntegrationErrorCatalog.INTEGRATION_CREDENTIAL_REVOKED,"Credential is revoked: "+id, Map.of("id",id)); }
    public static AppException credentialExpired(UUID id){ return new AppException(IntegrationErrorCatalog.INTEGRATION_CREDENTIAL_EXPIRED,"Credential is expired: "+id, Map.of("id",id)); }
    public static AppException mappingProfileNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.DATA_MAPPING_PROFILE_NOT_FOUND,"Mapping profile not found: "+id, Map.of("id",id)); }
    public static AppException mappingProfileCodeExists(String code){ return new AppException(IntegrationErrorCatalog.DATA_MAPPING_PROFILE_CODE_ALREADY_EXISTS,"Mapping profile code already exists: "+code, Map.of("code",code)); }
    public static AppException externalIdMappingNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.EXTERNAL_ID_MAPPING_NOT_FOUND,"External id mapping not found: "+id, Map.of("id",id)); }
    public static AppException importTemplateNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.IMPORT_TEMPLATE_NOT_FOUND,"Import template not found: "+id, Map.of("id",id)); }
    public static AppException importJobNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.IMPORT_JOB_NOT_FOUND,"Import job not found: "+id, Map.of("id",id)); }
    public static AppException importJobInvalidStatus(String status){ return new AppException(IntegrationErrorCatalog.IMPORT_JOB_INVALID_STATUS,"Invalid import job status: "+status, Map.of("status",status)); }
    public static AppException importJobExecutionFailed(String msg){ return new AppException(IntegrationErrorCatalog.IMPORT_JOB_EXECUTION_FAILED, msg); }
    public static AppException dryRunRequired(){ return new AppException(IntegrationErrorCatalog.IMPORT_JOB_DRY_RUN_REQUIRED); }
    public static AppException exportProfileNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.EXPORT_PROFILE_NOT_FOUND,"Export profile not found: "+id, Map.of("id",id)); }
    public static AppException exportProfileCodeExists(String code){ return new AppException(IntegrationErrorCatalog.EXPORT_PROFILE_CODE_ALREADY_EXISTS,"Export profile code already exists: "+code, Map.of("code",code)); }
    public static AppException exportJobNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.EXPORT_JOB_NOT_FOUND,"Export job not found: "+id, Map.of("id",id)); }
    public static AppException exportJobInvalidStatus(String status){ return new AppException(IntegrationErrorCatalog.EXPORT_JOB_INVALID_STATUS,"Invalid export job status: "+status, Map.of("status",status)); }
    public static AppException exportFileExpired(UUID id){ return new AppException(IntegrationErrorCatalog.EXPORT_FILE_EXPIRED,"Export file expired: "+id, Map.of("id",id)); }
    public static AppException webhookSubscriptionNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.WEBHOOK_SUBSCRIPTION_NOT_FOUND,"Webhook subscription not found: "+id, Map.of("id",id)); }
    public static AppException webhookDeliveryNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.WEBHOOK_DELIVERY_NOT_FOUND,"Webhook delivery not found: "+id, Map.of("id",id)); }
    public static AppException webhookDeliveryRetryNotAllowed(String status){ return new AppException(IntegrationErrorCatalog.WEBHOOK_DELIVERY_RETRY_NOT_ALLOWED,"Webhook delivery cannot be retried from status: "+status, Map.of("status",status)); }
    public static AppException inboundEndpointNotFound(String code){ return new AppException(IntegrationErrorCatalog.INBOUND_WEBHOOK_ENDPOINT_NOT_FOUND,"Inbound endpoint not found: "+code, Map.of("endpointCode",code)); }
    public static AppException inboundDuplicateEvent(String externalId){ return new AppException(IntegrationErrorCatalog.INBOUND_WEBHOOK_DUPLICATE_EVENT,"Duplicate inbound event: "+externalId, Map.of("externalEventId",externalId)); }
    public static AppException invalidSignature(){ return new AppException(IntegrationErrorCatalog.INBOUND_WEBHOOK_SIGNATURE_INVALID); }
    public static AppException syncJobNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.SYNC_JOB_NOT_FOUND,"Sync job not found: "+id, Map.of("id",id)); }
    public static AppException syncJobInvalidStatus(String status){ return new AppException(IntegrationErrorCatalog.SYNC_JOB_INVALID_STATUS,"Invalid sync job status: "+status, Map.of("status",status)); }
    public static AppException syncRunNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.SYNC_RUN_NOT_FOUND,"Sync run not found: "+id, Map.of("id",id)); }
    public static AppException syncConflictNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.SYNC_CONFLICT_NOT_FOUND,"Sync conflict not found: "+id, Map.of("id",id)); }
    public static AppException deadLetterEventNotFound(UUID id){ return new AppException(IntegrationErrorCatalog.DEAD_LETTER_EVENT_NOT_FOUND,"Dead letter event not found: "+id, Map.of("id",id)); }
    public static AppException providerRateLimited(String providerCode){ return new AppException(IntegrationErrorCatalog.PROVIDER_RATE_LIMITED,"Provider rate limited: "+providerCode, Map.of("providerCode",providerCode)); }
}
