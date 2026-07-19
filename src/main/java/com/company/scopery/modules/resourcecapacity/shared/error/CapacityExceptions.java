package com.company.scopery.modules.resourcecapacity.shared.error;

import com.company.scopery.common.exception.AppException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class CapacityExceptions {

    private CapacityExceptions() {}

    public static AppException calendarNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_NOT_FOUND,
                "Working calendar not found: " + id, Map.of("id", id));
    }

    public static AppException calendarCodeAlreadyExists(String code, UUID workspaceId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_CODE_ALREADY_EXISTS,
                "Calendar code already exists: " + code,
                Map.of("code", code, "workspaceId", workspaceId));
    }

    public static AppException calendarInvalidTimezone(String timezone) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_INVALID_TIMEZONE,
                "Invalid timezone: " + timezone, Map.of("timezone", timezone));
    }

    public static AppException calendarNoWorkingDay(UUID calendarId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_NO_WORKING_DAY,
                "Calendar must have at least one working day: " + calendarId,
                Map.of("calendarId", calendarId));
    }

    public static AppException calendarDefaultConflict(UUID workspaceId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_DEFAULT_CONFLICT,
                "Only one default active calendar allowed in workspace: " + workspaceId,
                Map.of("workspaceId", workspaceId));
    }

    public static AppException calendarArchived(UUID id) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_ARCHIVED,
                "Calendar is archived: " + id, Map.of("id", id));
    }

    public static AppException calendarInUse(UUID id) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_IN_USE,
                "Calendar is in use by capacity profiles: " + id, Map.of("id", id));
    }

    public static AppException calendarNotActive(UUID id) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_NOT_ACTIVE,
                "Calendar is not active: " + id, Map.of("id", id));
    }

    public static AppException calendarWorkspaceNotFound(UUID workspaceId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_WORKSPACE_NOT_FOUND,
                "Workspace not found: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException calendarWorkspaceNotActive(UUID workspaceId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALENDAR_WORKSPACE_NOT_ACTIVE,
                "Workspace is not active: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException dayRuleInvalidDay(String dayOfWeek) {
        return new AppException(CapacityErrorCatalog.CAPACITY_DAY_RULE_INVALID_DAY,
                "Invalid day of week: " + dayOfWeek, Map.of("dayOfWeek", dayOfWeek));
    }

    public static AppException dayRuleDuplicate(String dayOfWeek) {
        return new AppException(CapacityErrorCatalog.CAPACITY_DAY_RULE_DUPLICATE,
                "Duplicate day of week: " + dayOfWeek, Map.of("dayOfWeek", dayOfWeek));
    }

    public static AppException dayRuleInvalidHours() {
        return new AppException(CapacityErrorCatalog.CAPACITY_DAY_RULE_INVALID_HOURS);
    }

    public static AppException dayRuleAtomicUpdateFailed(UUID calendarId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_DAY_RULE_ATOMIC_UPDATE_FAILED,
                "Day rules atomic update failed for calendar: " + calendarId,
                Map.of("calendarId", calendarId));
    }

    public static AppException exceptionNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.CAPACITY_EXCEPTION_NOT_FOUND,
                "Calendar exception not found: " + id, Map.of("id", id));
    }

    public static AppException exceptionDuplicateDate(UUID calendarId, String date) {
        return new AppException(CapacityErrorCatalog.CAPACITY_EXCEPTION_DUPLICATE_DATE,
                "Exception already exists for date: " + date,
                Map.of("calendarId", calendarId, "exceptionDate", date));
    }

    public static AppException exceptionInvalidType(String type) {
        return new AppException(CapacityErrorCatalog.CAPACITY_EXCEPTION_INVALID_TYPE,
                "Invalid exception type: " + type, Map.of("exceptionType", type));
    }

    public static AppException exceptionInvalidHours() {
        return new AppException(CapacityErrorCatalog.CAPACITY_EXCEPTION_INVALID_HOURS);
    }

    public static AppException profileNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_NOT_FOUND,
                "User capacity profile not found: " + id, Map.of("id", id));
    }

    public static AppException profileMemberNotFound(UUID memberId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_MEMBER_NOT_FOUND,
                "Workspace member not found: " + memberId, Map.of("workspaceMemberId", memberId));
    }

    public static AppException profileMemberInactive(UUID memberId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_MEMBER_INACTIVE,
                "Workspace member is inactive: " + memberId, Map.of("workspaceMemberId", memberId));
    }

    public static AppException profileCalendarWorkspaceMismatch(UUID calendarId, UUID workspaceId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_CALENDAR_WORKSPACE_MISMATCH,
                "Calendar does not belong to workspace",
                Map.of("calendarId", calendarId, "workspaceId", workspaceId));
    }

    public static AppException profileInvalidDailyHours(BigDecimal hours) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_INVALID_DAILY_HOURS,
                "Invalid default daily hours: " + hours, Map.of("defaultDailyHours", hours));
    }

    public static AppException profileInvalidFocusFactor(BigDecimal focusFactor) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_INVALID_FOCUS_FACTOR,
                "Invalid focus factor: " + focusFactor, Map.of("focusFactor", focusFactor));
    }

    public static AppException profileDateRangeInvalid() {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_DATE_RANGE_INVALID);
    }

    public static AppException profileOverlap(UUID workspaceMemberId) {
        return new AppException(CapacityErrorCatalog.CAPACITY_PROFILE_OVERLAP,
                "Overlapping active capacity profile for member: " + workspaceMemberId,
                Map.of("workspaceMemberId", workspaceMemberId));
    }

    public static AppException allocationNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_NOT_FOUND,
                "Project resource allocation not found: " + id, Map.of("id", id));
    }

    public static AppException allocationProjectNotFound(UUID projectId) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_PROJECT_NOT_FOUND,
                "Project not found: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException allocationProjectWorkspaceMismatch(UUID projectId, UUID workspaceId) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_PROJECT_WORKSPACE_MISMATCH,
                "Project does not belong to workspace",
                Map.of("projectId", projectId, "workspaceId", workspaceId));
    }

    public static AppException allocationMemberNotFound(UUID memberId) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_MEMBER_NOT_FOUND,
                "Workspace member not found: " + memberId, Map.of("workspaceMemberId", memberId));
    }

    public static AppException allocationMemberInactive(UUID memberId) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_MEMBER_INACTIVE,
                "Workspace member is inactive: " + memberId, Map.of("workspaceMemberId", memberId));
    }

    public static AppException allocationInvalidPercent(BigDecimal percent) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_INVALID_PERCENT,
                "Invalid allocation percent: " + percent, Map.of("allocationPercent", percent));
    }

    public static AppException allocationDateRangeInvalid() {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_DATE_RANGE_INVALID);
    }

    public static AppException allocationOverAllocated(UUID userId, BigDecimal totalPercent) {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_OVER_ALLOCATED,
                "Total overlapping allocation exceeds 100%: " + totalPercent,
                Map.of("userId", userId, "totalAllocationPercent", totalPercent));
    }

    public static AppException allocationAccessDenied() {
        return new AppException(CapacityErrorCatalog.PROJECT_ALLOCATION_ACCESS_DENIED);
    }

    public static AppException calculationInvalidRange() {
        return new AppException(CapacityErrorCatalog.CAPACITY_CALCULATION_INVALID_RANGE);
    }

    public static AppException accessDenied() {
        return new AppException(CapacityErrorCatalog.CAPACITY_ACCESS_DENIED);
    }

    public static AppException resourceProfileNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.RESOURCE_PROFILE_NOT_FOUND, "Resource profile not found: " + id, Map.of("id", id));
    }
    public static AppException resourceProfileDuplicateUser(UUID workspaceId, UUID userId) {
        return new AppException(CapacityErrorCatalog.RESOURCE_PROFILE_DUPLICATE_LINKED_USER,
                "Resource already linked to user", Map.of("workspaceId", workspaceId, "linkedUserId", userId));
    }
    public static AppException resourceRoleNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.RESOURCE_ROLE_NOT_FOUND, "Resource role not found: " + id, Map.of("id", id));
    }
    public static AppException resourceRoleDuplicate(String code) {
        return new AppException(CapacityErrorCatalog.RESOURCE_ROLE_DUPLICATE_CODE, "Duplicate role code: " + code, Map.of("roleCode", code));
    }
    public static AppException resourceSkillNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.RESOURCE_SKILL_NOT_FOUND, "Resource skill not found: " + id, Map.of("id", id));
    }
    public static AppException resourceSkillDuplicate(String code) {
        return new AppException(CapacityErrorCatalog.RESOURCE_SKILL_DUPLICATE_CODE, "Duplicate skill code: " + code, Map.of("skillCode", code));
    }
    public static AppException taskAssignmentNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.TASK_RESOURCE_ASSIGNMENT_NOT_FOUND, "Task assignment not found: " + id, Map.of("id", id));
    }
    public static AppException taskAssignmentDuplicate() {
        return new AppException(CapacityErrorCatalog.TASK_RESOURCE_ASSIGNMENT_DUPLICATE);
    }
    public static AppException effortEstimateNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.EFFORT_ESTIMATE_NOT_FOUND, "Effort estimate not found: " + id, Map.of("id", id));
    }
    public static AppException effortInvalidHours() {
        return new AppException(CapacityErrorCatalog.EFFORT_ESTIMATE_INVALID_HOURS);
    }
    public static AppException actualEffortNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.ACTUAL_EFFORT_RECORD_NOT_FOUND, "Actual effort not found: " + id, Map.of("id", id));
    }
    public static AppException actualEffortInvalidHours() {
        return new AppException(CapacityErrorCatalog.ACTUAL_EFFORT_INVALID_HOURS);
    }
    public static AppException riskFlagNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.RESOURCE_RISK_FLAG_NOT_FOUND, "Risk flag not found: " + id, Map.of("id", id));
    }
    public static AppException riskFlagInvalidStatus() {
        return new AppException(CapacityErrorCatalog.RESOURCE_RISK_FLAG_INVALID_STATUS);
    }
    public static AppException conflictNotFound(UUID id) {
        return new AppException(CapacityErrorCatalog.ASSIGNMENT_CONFLICT_NOT_FOUND, "Conflict not found: " + id, Map.of("id", id));
    }
    public static AppException costSensitiveAccessDenied() {
        return new AppException(CapacityErrorCatalog.RESOURCE_COST_SENSITIVE_ACCESS_DENIED);
    }
    public static AppException thresholdInvalid() {
        return new AppException(CapacityErrorCatalog.UTILIZATION_THRESHOLD_POLICY_INVALID);
    }

    public static AppException resourceProfileAccessDenied() {
        return new AppException(CapacityErrorCatalog.RESOURCE_PROFILE_ACCESS_DENIED);
    }

    public static AppException capacityExceptionInvalidDateRange() {
        return new AppException(CapacityErrorCatalog.CAPACITY_EXCEPTION_INVALID_DATE_RANGE);
    }

    public static AppException allocationInvalidStatus(UUID id, String currentStatus) {
        return new AppException(CapacityErrorCatalog.PROJECT_RESOURCE_ALLOCATION_INVALID_STATUS,
                "Allocation " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("allocationId", id, "currentStatus", currentStatus));
    }

    public static AppException allocationInvalidAmount(String reason) {
        return new AppException(CapacityErrorCatalog.PROJECT_RESOURCE_ALLOCATION_INVALID_AMOUNT,
                "Invalid allocation amount: " + reason, Map.of("reason", reason));
    }

    public static AppException taskAssignmentTargetMismatch(UUID taskId, UUID projectId) {
        return new AppException(CapacityErrorCatalog.TASK_RESOURCE_ASSIGNMENT_TARGET_MISMATCH,
                "Task " + taskId + " does not belong to project " + projectId,
                Map.of("taskId", taskId, "projectId", projectId));
    }

    public static AppException taskAssignmentAccessDenied() {
        return new AppException(CapacityErrorCatalog.TASK_RESOURCE_ASSIGNMENT_ACCESS_DENIED);
    }

    public static AppException effortEstimateInvalidTarget(String targetType, UUID targetId) {
        return new AppException(CapacityErrorCatalog.EFFORT_ESTIMATE_INVALID_TARGET,
                "Invalid effort estimate target: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException effortForecastRebuildFailed(String reason) {
        return new AppException(CapacityErrorCatalog.EFFORT_FORECAST_REBUILD_FAILED,
                "Effort forecast rebuild failed: " + reason, Map.of("reason", reason));
    }

    public static AppException actualEffortCancelNotAllowed(UUID id, String currentStatus) {
        return new AppException(CapacityErrorCatalog.ACTUAL_EFFORT_CANCEL_NOT_ALLOWED,
                "Actual effort record " + id + " cannot be cancelled in status: " + currentStatus,
                Map.of("actualEffortId", id, "currentStatus", currentStatus));
    }

    public static AppException utilizationRebuildFailed(String reason) {
        return new AppException(CapacityErrorCatalog.RESOURCE_UTILIZATION_REBUILD_FAILED,
                "Utilization rebuild failed: " + reason, Map.of("reason", reason));
    }

    public static AppException projectCapacityRebuildFailed(String reason) {
        return new AppException(CapacityErrorCatalog.PROJECT_CAPACITY_REBUILD_FAILED,
                "Project capacity rebuild failed: " + reason, Map.of("reason", reason));
    }

    public static AppException workloadSnapshotCreateFailed(String reason) {
        return new AppException(CapacityErrorCatalog.WORKLOAD_SNAPSHOT_CREATE_FAILED,
                "Workload snapshot creation failed: " + reason, Map.of("reason", reason));
    }

    public static AppException conflictInvalidStatus(UUID id, String currentStatus) {
        return new AppException(CapacityErrorCatalog.ASSIGNMENT_CONFLICT_INVALID_STATUS,
                "Conflict " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("conflictId", id, "currentStatus", currentStatus));
    }

    public static AppException costInputRebuildFailed(String reason) {
        return new AppException(CapacityErrorCatalog.RESOURCE_COST_INPUT_REBUILD_FAILED,
                "Cost input rebuild failed: " + reason, Map.of("reason", reason));
    }

    public static AppException resourceReportAccessDenied() {
        return new AppException(CapacityErrorCatalog.RESOURCE_REPORT_ACCESS_DENIED);
    }
}

