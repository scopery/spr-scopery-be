package com.company.scopery.modules.integrationhub.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum IntegrationErrorCatalog implements ErrorCatalog {
    INTEGRATION_ACCESS_DENIED("INTEGRATION_ACCESS_DENIED","Integration access denied",HttpStatus.FORBIDDEN),
    INTEGRATION_PROVIDER_NOT_FOUND("INTEGRATION_PROVIDER_NOT_FOUND","Provider not found",HttpStatus.NOT_FOUND),
    INTEGRATION_CONNECTION_NOT_FOUND("INTEGRATION_CONNECTION_NOT_FOUND","Connection not found",HttpStatus.NOT_FOUND),
    INTEGRATION_CONNECTION_INVALID_STATUS("INTEGRATION_CONNECTION_INVALID_STATUS","Invalid connection status",HttpStatus.UNPROCESSABLE_ENTITY),
    INTEGRATION_SECRET_VALUE_NOT_RETURNABLE("INTEGRATION_SECRET_VALUE_NOT_RETURNABLE","Raw secret cannot be returned",HttpStatus.BAD_REQUEST),
    INTEGRATION_CREDENTIAL_NOT_FOUND("INTEGRATION_CREDENTIAL_NOT_FOUND","Credential not found",HttpStatus.NOT_FOUND),
    INTEGRATION_CREDENTIAL_REVOKED("INTEGRATION_CREDENTIAL_REVOKED","Credential is revoked",HttpStatus.UNPROCESSABLE_ENTITY),
    INTEGRATION_CREDENTIAL_EXPIRED("INTEGRATION_CREDENTIAL_EXPIRED","Credential is expired",HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_MAPPING_PROFILE_NOT_FOUND("DATA_MAPPING_PROFILE_NOT_FOUND","Mapping profile not found",HttpStatus.NOT_FOUND),
    DATA_MAPPING_PROFILE_CODE_ALREADY_EXISTS("DATA_MAPPING_PROFILE_CODE_ALREADY_EXISTS","Mapping profile code already exists",HttpStatus.CONFLICT),
    EXTERNAL_ID_MAPPING_NOT_FOUND("EXTERNAL_ID_MAPPING_NOT_FOUND","External id mapping not found",HttpStatus.NOT_FOUND),
    IMPORT_TEMPLATE_NOT_FOUND("IMPORT_TEMPLATE_NOT_FOUND","Import template not found",HttpStatus.NOT_FOUND),
    IMPORT_JOB_NOT_FOUND("IMPORT_JOB_NOT_FOUND","Import job not found",HttpStatus.NOT_FOUND),
    IMPORT_JOB_DRY_RUN_REQUIRED("IMPORT_JOB_DRY_RUN_REQUIRED","Dry-run required before execute",HttpStatus.UNPROCESSABLE_ENTITY),
    IMPORT_JOB_INVALID_STATUS("IMPORT_JOB_INVALID_STATUS","Invalid import job status",HttpStatus.UNPROCESSABLE_ENTITY),
    IMPORT_JOB_EXECUTION_FAILED("IMPORT_JOB_EXECUTION_FAILED","Import job execution failed",HttpStatus.UNPROCESSABLE_ENTITY),
    EXPORT_PROFILE_NOT_FOUND("EXPORT_PROFILE_NOT_FOUND","Export profile not found",HttpStatus.NOT_FOUND),
    EXPORT_PROFILE_CODE_ALREADY_EXISTS("EXPORT_PROFILE_CODE_ALREADY_EXISTS","Export profile code already exists",HttpStatus.CONFLICT),
    EXPORT_JOB_NOT_FOUND("EXPORT_JOB_NOT_FOUND","Export job not found",HttpStatus.NOT_FOUND),
    EXPORT_JOB_INVALID_STATUS("EXPORT_JOB_INVALID_STATUS","Invalid export job status",HttpStatus.UNPROCESSABLE_ENTITY),
    EXPORT_FILE_EXPIRED("EXPORT_FILE_EXPIRED","Export file expired",HttpStatus.UNPROCESSABLE_ENTITY),
    WEBHOOK_SUBSCRIPTION_NOT_FOUND("WEBHOOK_SUBSCRIPTION_NOT_FOUND","Webhook subscription not found",HttpStatus.NOT_FOUND),
    WEBHOOK_DELIVERY_NOT_FOUND("WEBHOOK_DELIVERY_NOT_FOUND","Webhook delivery not found",HttpStatus.NOT_FOUND),
    WEBHOOK_DELIVERY_RETRY_NOT_ALLOWED("WEBHOOK_DELIVERY_RETRY_NOT_ALLOWED","Webhook delivery retry not allowed",HttpStatus.UNPROCESSABLE_ENTITY),
    WEBHOOK_DELIVERY_DEAD_LETTERED("WEBHOOK_DELIVERY_DEAD_LETTERED","Webhook dead-lettered",HttpStatus.UNPROCESSABLE_ENTITY),
    INBOUND_WEBHOOK_ENDPOINT_NOT_FOUND("INBOUND_WEBHOOK_ENDPOINT_NOT_FOUND","Inbound webhook endpoint not found",HttpStatus.NOT_FOUND),
    INBOUND_WEBHOOK_DUPLICATE_EVENT("INBOUND_WEBHOOK_DUPLICATE_EVENT","Duplicate inbound webhook event",HttpStatus.CONFLICT),
    INBOUND_WEBHOOK_SIGNATURE_INVALID("INBOUND_WEBHOOK_SIGNATURE_INVALID","Invalid inbound signature",HttpStatus.UNAUTHORIZED),
    SYNC_JOB_NOT_FOUND("SYNC_JOB_NOT_FOUND","Sync job not found",HttpStatus.NOT_FOUND),
    SYNC_JOB_INVALID_STATUS("SYNC_JOB_INVALID_STATUS","Invalid sync job status",HttpStatus.UNPROCESSABLE_ENTITY),
    SYNC_RUN_NOT_FOUND("SYNC_RUN_NOT_FOUND","Sync run not found",HttpStatus.NOT_FOUND),
    SYNC_CONFLICT_NOT_FOUND("SYNC_CONFLICT_NOT_FOUND","Sync conflict not found",HttpStatus.NOT_FOUND),
    SYNC_CURSOR_UPDATE_FAILED("SYNC_CURSOR_UPDATE_FAILED","Cursor update failed",HttpStatus.UNPROCESSABLE_ENTITY),
    DEAD_LETTER_EVENT_NOT_FOUND("DEAD_LETTER_EVENT_NOT_FOUND","Dead letter event not found",HttpStatus.NOT_FOUND),
    PROVIDER_RATE_LIMITED("PROVIDER_RATE_LIMITED","Provider rate limited",HttpStatus.TOO_MANY_REQUESTS);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    IntegrationErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
