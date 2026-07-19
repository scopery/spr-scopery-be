package com.company.scopery.modules.resourcecapacity.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum CapacityErrorCatalog implements ErrorCatalog {

    CAPACITY_CALENDAR_NOT_FOUND(
            "CAPACITY_CALENDAR_NOT_FOUND", "Working calendar not found", HttpStatus.NOT_FOUND),
    CAPACITY_CALENDAR_CODE_ALREADY_EXISTS(
            "CAPACITY_CALENDAR_CODE_ALREADY_EXISTS", "Calendar code already exists in this workspace", HttpStatus.CONFLICT),
    CAPACITY_CALENDAR_INVALID_TIMEZONE(
            "CAPACITY_CALENDAR_INVALID_TIMEZONE", "Calendar timezone is invalid", HttpStatus.BAD_REQUEST),
    CAPACITY_CALENDAR_NO_WORKING_DAY(
            "CAPACITY_CALENDAR_NO_WORKING_DAY", "Calendar must have at least one working day", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_CALENDAR_DEFAULT_CONFLICT(
            "CAPACITY_CALENDAR_DEFAULT_CONFLICT", "Only one default active calendar is allowed per workspace", HttpStatus.CONFLICT),
    CAPACITY_CALENDAR_ARCHIVED(
            "CAPACITY_CALENDAR_ARCHIVED", "Archived calendar cannot be assigned or used", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_CALENDAR_IN_USE(
            "CAPACITY_CALENDAR_IN_USE", "Calendar is assigned to capacity profiles and cannot be archived", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_CALENDAR_NOT_ACTIVE(
            "CAPACITY_CALENDAR_NOT_ACTIVE", "Calendar must be ACTIVE", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_CALENDAR_WORKSPACE_NOT_ACTIVE(
            "CAPACITY_CALENDAR_WORKSPACE_NOT_ACTIVE", "Workspace must be ACTIVE", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_CALENDAR_WORKSPACE_NOT_FOUND(
            "CAPACITY_CALENDAR_WORKSPACE_NOT_FOUND", "Workspace not found", HttpStatus.NOT_FOUND),

    CAPACITY_DAY_RULE_INVALID_DAY(
            "CAPACITY_DAY_RULE_INVALID_DAY", "Invalid day of week", HttpStatus.BAD_REQUEST),
    CAPACITY_DAY_RULE_DUPLICATE(
            "CAPACITY_DAY_RULE_DUPLICATE", "Duplicate day of week in day rules", HttpStatus.BAD_REQUEST),
    CAPACITY_DAY_RULE_INVALID_HOURS(
            "CAPACITY_DAY_RULE_INVALID_HOURS", "Day rule working hours are invalid", HttpStatus.BAD_REQUEST),
    CAPACITY_DAY_RULE_ATOMIC_UPDATE_FAILED(
            "CAPACITY_DAY_RULE_ATOMIC_UPDATE_FAILED", "Day rules update failed atomically", HttpStatus.UNPROCESSABLE_ENTITY),

    CAPACITY_EXCEPTION_NOT_FOUND(
            "CAPACITY_EXCEPTION_NOT_FOUND", "Calendar exception not found", HttpStatus.NOT_FOUND),
    CAPACITY_EXCEPTION_DUPLICATE_DATE(
            "CAPACITY_EXCEPTION_DUPLICATE_DATE", "An exception already exists for this calendar date", HttpStatus.CONFLICT),
    CAPACITY_EXCEPTION_INVALID_TYPE(
            "CAPACITY_EXCEPTION_INVALID_TYPE", "Invalid calendar exception type", HttpStatus.BAD_REQUEST),
    CAPACITY_EXCEPTION_INVALID_HOURS(
            "CAPACITY_EXCEPTION_INVALID_HOURS", "Exception working hours are invalid", HttpStatus.BAD_REQUEST),

    CAPACITY_PROFILE_NOT_FOUND(
            "CAPACITY_PROFILE_NOT_FOUND", "User capacity profile not found", HttpStatus.NOT_FOUND),
    CAPACITY_PROFILE_MEMBER_NOT_FOUND(
            "CAPACITY_PROFILE_MEMBER_NOT_FOUND", "Workspace member not found for capacity profile", HttpStatus.NOT_FOUND),
    CAPACITY_PROFILE_MEMBER_INACTIVE(
            "CAPACITY_PROFILE_MEMBER_INACTIVE", "Inactive workspace member cannot have an active capacity profile", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_PROFILE_CALENDAR_WORKSPACE_MISMATCH(
            "CAPACITY_PROFILE_CALENDAR_WORKSPACE_MISMATCH", "Calendar must belong to the same workspace", HttpStatus.UNPROCESSABLE_ENTITY),
    CAPACITY_PROFILE_INVALID_DAILY_HOURS(
            "CAPACITY_PROFILE_INVALID_DAILY_HOURS", "Default daily hours must be greater than 0 and at most 24", HttpStatus.BAD_REQUEST),
    CAPACITY_PROFILE_INVALID_FOCUS_FACTOR(
            "CAPACITY_PROFILE_INVALID_FOCUS_FACTOR", "Focus factor must be greater than 0 and at most 1", HttpStatus.BAD_REQUEST),
    CAPACITY_PROFILE_DATE_RANGE_INVALID(
            "CAPACITY_PROFILE_DATE_RANGE_INVALID", "Capacity profile effective date range is invalid", HttpStatus.BAD_REQUEST),
    CAPACITY_PROFILE_OVERLAP(
            "CAPACITY_PROFILE_OVERLAP", "Active capacity profiles overlap for this workspace member", HttpStatus.CONFLICT),

    PROJECT_ALLOCATION_NOT_FOUND(
            "PROJECT_ALLOCATION_NOT_FOUND", "Project resource allocation not found", HttpStatus.NOT_FOUND),
    PROJECT_ALLOCATION_PROJECT_NOT_FOUND(
            "PROJECT_ALLOCATION_PROJECT_NOT_FOUND", "Project not found for allocation", HttpStatus.NOT_FOUND),
    PROJECT_ALLOCATION_PROJECT_WORKSPACE_MISMATCH(
            "PROJECT_ALLOCATION_PROJECT_WORKSPACE_MISMATCH", "Project does not belong to the allocation workspace", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_ALLOCATION_MEMBER_NOT_FOUND(
            "PROJECT_ALLOCATION_MEMBER_NOT_FOUND", "Workspace member not found for allocation", HttpStatus.NOT_FOUND),
    PROJECT_ALLOCATION_MEMBER_INACTIVE(
            "PROJECT_ALLOCATION_MEMBER_INACTIVE", "Inactive workspace member cannot be allocated", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_ALLOCATION_INVALID_PERCENT(
            "PROJECT_ALLOCATION_INVALID_PERCENT", "Allocation percent must be greater than 0 and at most 100", HttpStatus.BAD_REQUEST),
    PROJECT_ALLOCATION_DATE_RANGE_INVALID(
            "PROJECT_ALLOCATION_DATE_RANGE_INVALID", "Allocation date range is invalid", HttpStatus.BAD_REQUEST),
    PROJECT_ALLOCATION_OVER_ALLOCATED(
            "PROJECT_ALLOCATION_OVER_ALLOCATED", "Total overlapping allocation exceeds 100%", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_ALLOCATION_ACCESS_DENIED(
            "PROJECT_ALLOCATION_ACCESS_DENIED", "Access to project allocation is denied", HttpStatus.FORBIDDEN),

    CAPACITY_CALCULATION_INVALID_RANGE(
            "CAPACITY_CALCULATION_INVALID_RANGE", "Capacity calculation date range is invalid", HttpStatus.BAD_REQUEST),
    CAPACITY_ACCESS_DENIED(
            "CAPACITY_ACCESS_DENIED", "Capacity access is denied", HttpStatus.FORBIDDEN),
    // Phase 37
    RESOURCE_PROFILE_NOT_FOUND("RESOURCE_PROFILE_NOT_FOUND", "Resource profile not found", HttpStatus.NOT_FOUND),
    RESOURCE_PROFILE_DUPLICATE_LINKED_USER("RESOURCE_PROFILE_DUPLICATE_LINKED_USER", "Resource already linked to this user", HttpStatus.CONFLICT),
    RESOURCE_ROLE_NOT_FOUND("RESOURCE_ROLE_NOT_FOUND", "Resource role not found", HttpStatus.NOT_FOUND),
    RESOURCE_ROLE_DUPLICATE_CODE("RESOURCE_ROLE_DUPLICATE_CODE", "Resource role code already exists", HttpStatus.CONFLICT),
    RESOURCE_SKILL_NOT_FOUND("RESOURCE_SKILL_NOT_FOUND", "Resource skill not found", HttpStatus.NOT_FOUND),
    RESOURCE_SKILL_DUPLICATE_CODE("RESOURCE_SKILL_DUPLICATE_CODE", "Resource skill code already exists", HttpStatus.CONFLICT),
    TASK_RESOURCE_ASSIGNMENT_NOT_FOUND("TASK_RESOURCE_ASSIGNMENT_NOT_FOUND", "Task resource assignment not found", HttpStatus.NOT_FOUND),
    TASK_RESOURCE_ASSIGNMENT_DUPLICATE("TASK_RESOURCE_ASSIGNMENT_DUPLICATE", "Active task resource assignment already exists", HttpStatus.CONFLICT),
    EFFORT_ESTIMATE_NOT_FOUND("EFFORT_ESTIMATE_NOT_FOUND", "Effort estimate not found", HttpStatus.NOT_FOUND),
    EFFORT_ESTIMATE_INVALID_HOURS("EFFORT_ESTIMATE_INVALID_HOURS", "Effort hours must be non-negative", HttpStatus.BAD_REQUEST),
    ACTUAL_EFFORT_RECORD_NOT_FOUND("ACTUAL_EFFORT_RECORD_NOT_FOUND", "Actual effort record not found", HttpStatus.NOT_FOUND),
    ACTUAL_EFFORT_INVALID_HOURS("ACTUAL_EFFORT_INVALID_HOURS", "Actual effort hours must be non-negative", HttpStatus.BAD_REQUEST),
    RESOURCE_RISK_FLAG_NOT_FOUND("RESOURCE_RISK_FLAG_NOT_FOUND", "Resource risk flag not found", HttpStatus.NOT_FOUND),
    RESOURCE_RISK_FLAG_INVALID_STATUS("RESOURCE_RISK_FLAG_INVALID_STATUS", "Invalid resource risk flag status transition", HttpStatus.UNPROCESSABLE_ENTITY),
    ASSIGNMENT_CONFLICT_NOT_FOUND("ASSIGNMENT_CONFLICT_NOT_FOUND", "Assignment conflict not found", HttpStatus.NOT_FOUND),
    RESOURCE_COST_SENSITIVE_ACCESS_DENIED("RESOURCE_COST_SENSITIVE_ACCESS_DENIED", "Sensitive cost access denied", HttpStatus.FORBIDDEN),
    UTILIZATION_THRESHOLD_POLICY_INVALID("UTILIZATION_THRESHOLD_POLICY_INVALID", "Utilization threshold policy invalid", HttpStatus.BAD_REQUEST),
    RESOURCE_PROFILE_ACCESS_DENIED("RESOURCE_PROFILE_ACCESS_DENIED", "Access to resource profile is denied", HttpStatus.FORBIDDEN),
    CAPACITY_EXCEPTION_INVALID_DATE_RANGE("CAPACITY_EXCEPTION_INVALID_DATE_RANGE", "Capacity exception date range is invalid", HttpStatus.BAD_REQUEST),
    PROJECT_RESOURCE_ALLOCATION_INVALID_STATUS("PROJECT_RESOURCE_ALLOCATION_INVALID_STATUS", "Allocation status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_RESOURCE_ALLOCATION_INVALID_AMOUNT("PROJECT_RESOURCE_ALLOCATION_INVALID_AMOUNT", "Allocation amount or hours value is invalid", HttpStatus.BAD_REQUEST),
    TASK_RESOURCE_ASSIGNMENT_TARGET_MISMATCH("TASK_RESOURCE_ASSIGNMENT_TARGET_MISMATCH", "Task does not belong to this project", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_RESOURCE_ASSIGNMENT_ACCESS_DENIED("TASK_RESOURCE_ASSIGNMENT_ACCESS_DENIED", "Access to task resource assignment is denied", HttpStatus.FORBIDDEN),
    EFFORT_ESTIMATE_INVALID_TARGET("EFFORT_ESTIMATE_INVALID_TARGET", "Effort estimate target type or target id is invalid", HttpStatus.BAD_REQUEST),
    EFFORT_FORECAST_REBUILD_FAILED("EFFORT_FORECAST_REBUILD_FAILED", "Effort forecast rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ACTUAL_EFFORT_CANCEL_NOT_ALLOWED("ACTUAL_EFFORT_CANCEL_NOT_ALLOWED", "Actual effort record cannot be cancelled in its current state", HttpStatus.UNPROCESSABLE_ENTITY),
    RESOURCE_UTILIZATION_REBUILD_FAILED("RESOURCE_UTILIZATION_REBUILD_FAILED", "Resource utilization summary rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_CAPACITY_REBUILD_FAILED("PROJECT_CAPACITY_REBUILD_FAILED", "Project capacity summary rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),
    WORKLOAD_SNAPSHOT_CREATE_FAILED("WORKLOAD_SNAPSHOT_CREATE_FAILED", "Workload snapshot creation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ASSIGNMENT_CONFLICT_INVALID_STATUS("ASSIGNMENT_CONFLICT_INVALID_STATUS", "Conflict status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    RESOURCE_COST_INPUT_REBUILD_FAILED("RESOURCE_COST_INPUT_REBUILD_FAILED", "Resource cost input rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),
    RESOURCE_REPORT_ACCESS_DENIED("RESOURCE_REPORT_ACCESS_DENIED", "Access to resource report is denied", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    CapacityErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
