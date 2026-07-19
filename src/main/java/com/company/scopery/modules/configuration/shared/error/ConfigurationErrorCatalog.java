package com.company.scopery.modules.configuration.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum ConfigurationErrorCatalog implements ErrorCatalog {
    FIELD_NOT_FOUND("CUSTOM_FIELD_NOT_FOUND","Custom field not found",HttpStatus.NOT_FOUND),
    FIELD_KEY_EXISTS("CUSTOM_FIELD_KEY_EXISTS","Custom field key already exists",HttpStatus.CONFLICT),
    OPTION_NOT_FOUND("CUSTOM_FIELD_OPTION_NOT_FOUND","Custom field option not found",HttpStatus.NOT_FOUND),
    FORM_NOT_FOUND("CUSTOM_FORM_NOT_FOUND","Custom form not found",HttpStatus.NOT_FOUND),
    FORM_VERSION_NOT_FOUND("CUSTOM_FORM_VERSION_NOT_FOUND","Form version not found",HttpStatus.NOT_FOUND),
    FORM_NOT_PUBLISHED("CUSTOM_FORM_NOT_PUBLISHED","Form version is not published",HttpStatus.UNPROCESSABLE_ENTITY),
    FORM_VERSION_IMMUTABLE("CUSTOM_FORM_VERSION_IMMUTABLE","Published form version is immutable",HttpStatus.UNPROCESSABLE_ENTITY),
    TAG_NOT_FOUND("TAG_NOT_FOUND","Tag not found",HttpStatus.NOT_FOUND),
    TAXONOMY_NOT_FOUND("TAXONOMY_NOT_FOUND","Taxonomy not found",HttpStatus.NOT_FOUND),
    STATUS_SET_NOT_FOUND("STATUS_SET_NOT_FOUND","Status set not found",HttpStatus.NOT_FOUND),
    LAYOUT_NOT_FOUND("LAYOUT_NOT_FOUND","Layout not found",HttpStatus.NOT_FOUND),
    SUBMISSION_NOT_FOUND("FORM_SUBMISSION_NOT_FOUND","Form submission not found",HttpStatus.NOT_FOUND),
    CONFIG_ACCESS_DENIED("CONFIGURATION_ACCESS_DENIED","Configuration access denied",HttpStatus.FORBIDDEN),
    CONFIG_NAME_REQUIRED("CONFIGURATION_NAME_REQUIRED","Label/name is required",HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("CUSTOM_FIELD_VALIDATION_FAILED","Custom field validation failed",HttpStatus.BAD_REQUEST);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    ConfigurationErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}
