package com.company.scopery.modules.raid.shared.error;
import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;
public enum RaidErrorCatalog implements ErrorCatalog {
    RAID_ITEM_NOT_FOUND("RAID_ITEM_NOT_FOUND", "RAID item not found", HttpStatus.NOT_FOUND),
    RAID_ACTION_NOT_FOUND("RAID_ACTION_NOT_FOUND", "RAID action not found", HttpStatus.NOT_FOUND),
    RAID_INVALID_STATUS("RAID_INVALID_STATUS", "Invalid RAID status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    RAID_ACCESS_DENIED("RAID_ACCESS_DENIED", "RAID access denied", HttpStatus.FORBIDDEN),
    RAID_PROJECT_ARCHIVED("RAID_PROJECT_ARCHIVED", "Cannot modify RAID for archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    RAID_TITLE_REQUIRED("RAID_TITLE_REQUIRED", "RAID title is required", HttpStatus.BAD_REQUEST),
    RAID_ESCALATION_REASON_REQUIRED("RAID_ESCALATION_REASON_REQUIRED", "Escalation reason is required", HttpStatus.BAD_REQUEST),
    RAID_RESOLUTION_NOTE_REQUIRED("RAID_RESOLUTION_NOTE_REQUIRED", "Resolution note is required", HttpStatus.BAD_REQUEST),
    DECISION_NOT_FOUND("DECISION_NOT_FOUND", "Decision record not found", HttpStatus.NOT_FOUND),
    DECISION_INVALID_STATUS("DECISION_INVALID_STATUS", "Invalid decision status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    DECISION_OUTCOME_REQUIRED("DECISION_OUTCOME_REQUIRED", "Decision outcome is required", HttpStatus.BAD_REQUEST),
    DECISION_RATIONALE_REQUIRED("DECISION_RATIONALE_REQUIRED", "Decision rationale is required", HttpStatus.BAD_REQUEST),
    RAID_LINK_NOT_FOUND("RAID_LINK_NOT_FOUND", "RAID link not found", HttpStatus.NOT_FOUND),
    RAID_LINK_TYPE_REQUIRED("RAID_LINK_TYPE_REQUIRED", "Link type is required", HttpStatus.BAD_REQUEST),
    RAID_TARGET_TYPE_REQUIRED("RAID_TARGET_TYPE_REQUIRED", "Target type is required", HttpStatus.BAD_REQUEST),
    RAID_ACTION_ALREADY_LINKED("RAID_ACTION_ALREADY_LINKED", "RAID action already has a linked task", HttpStatus.UNPROCESSABLE_ENTITY),
    RAID_ACTION_NOT_OPEN("RAID_ACTION_NOT_OPEN", "RAID action is not open for modification", HttpStatus.UNPROCESSABLE_ENTITY),
    RAID_NO_PROJECT_PHASE("RAID_NO_PROJECT_PHASE", "No active project phase available for task creation", HttpStatus.UNPROCESSABLE_ENTITY),
    DECISION_OPTION_NOT_FOUND("DECISION_OPTION_NOT_FOUND", "Decision option not found", HttpStatus.NOT_FOUND),
    DECISION_REPLACEMENT_REQUIRED("DECISION_REPLACEMENT_REQUIRED", "Replacement decision id or new decision details required", HttpStatus.BAD_REQUEST),
    DECISION_LINK_NOT_FOUND("DECISION_LINK_NOT_FOUND", "Decision link not found", HttpStatus.NOT_FOUND);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    RaidErrorCatalog(String c, String m, HttpStatus s){ code=c; defaultMessage=m; httpStatus=s; }
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;}
    @Override public HttpStatus httpStatus(){return httpStatus;}
}
