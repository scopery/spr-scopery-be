package com.company.scopery.modules.project.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ProjectErrorCatalog implements ErrorCatalog {

    // ── Project ───────────────────────────────────────────────────────────────
    PROJECT_NOT_FOUND(
            "PROJECT_NOT_FOUND", "Project not found", HttpStatus.NOT_FOUND),
    PROJECT_CODE_ALREADY_EXISTS(
            "PROJECT_CODE_ALREADY_EXISTS", "Project code already exists in this workspace", HttpStatus.CONFLICT),
    PROJECT_INVALID_DATE_RANGE(
            "PROJECT_INVALID_DATE_RANGE", "Project end date must not be before start date", HttpStatus.BAD_REQUEST),
    PROJECT_ALREADY_ARCHIVED(
            "PROJECT_ALREADY_ARCHIVED", "Project is already archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOT_ACTIVE(
            "PROJECT_NOT_ACTIVE", "Project is not active", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOT_ACTIVE_OR_DRAFT(
            "PROJECT_NOT_ACTIVE_OR_DRAFT", "Project must be in DRAFT or ACTIVE status", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_CANNOT_HOLD(
            "PROJECT_CANNOT_HOLD", "Only an ACTIVE project can be put on hold", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_CANNOT_COMPLETE(
            "PROJECT_CANNOT_COMPLETE", "Only an ACTIVE project can be completed", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_WORKSPACE_NOT_FOUND(
            "PROJECT_WORKSPACE_NOT_FOUND", "Workspace not found for project", HttpStatus.NOT_FOUND),
    PROJECT_WORKSPACE_NOT_ACTIVE(
            "PROJECT_WORKSPACE_NOT_ACTIVE", "Workspace must be ACTIVE to create a project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_OWNER_NOT_WORKSPACE_MEMBER(
            "PROJECT_OWNER_NOT_WORKSPACE_MEMBER", "Project owner must be an active workspace member", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_CANNOT_ACTIVATE(
            "PROJECT_CANNOT_ACTIVATE", "Only a DRAFT project can be activated", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_ACCESS_DENIED(
            "PROJECT_ACCESS_DENIED", "Access to the project is denied", HttpStatus.FORBIDDEN),
    PROJECT_WORKSPACE_ACCESS_DENIED(
            "PROJECT_WORKSPACE_ACCESS_DENIED", "Workspace access is required for this project operation", HttpStatus.FORBIDDEN),
    PROJECT_PERMISSION_DENIED(
            "PROJECT_PERMISSION_DENIED", "Required project permission is missing", HttpStatus.FORBIDDEN),
    PROJECT_CROSS_WORKSPACE_ACCESS_DENIED(
            "PROJECT_CROSS_WORKSPACE_ACCESS_DENIED", "Cross-workspace project access is denied", HttpStatus.FORBIDDEN),
    PROJECT_RESOURCE_NOT_ACCESSIBLE(
            "PROJECT_RESOURCE_NOT_ACCESSIBLE", "Project resource is not accessible", HttpStatus.NOT_FOUND),
    PROJECT_WORKSPACE_ARCHIVED(
            "PROJECT_WORKSPACE_ARCHIVED", "Archived workspace blocks project mutations", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_WORKSPACE_MEMBER_INACTIVE(
            "PROJECT_WORKSPACE_MEMBER_INACTIVE", "Inactive workspace members cannot access projects", HttpStatus.FORBIDDEN),
    PROJECT_PATH_MISMATCH(
            "PROJECT_PATH_MISMATCH", "Child resource does not belong to the specified project path", HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Phase Definition ──────────────────────────────────────────────────────
    PHASE_DEFINITION_NOT_FOUND(
            "PHASE_DEFINITION_NOT_FOUND", "Phase definition not found", HttpStatus.NOT_FOUND),
    PHASE_DEFINITION_CODE_ALREADY_EXISTS(
            "PHASE_DEFINITION_CODE_ALREADY_EXISTS", "Phase definition code already exists", HttpStatus.CONFLICT),
    PHASE_DEFINITION_ALREADY_ARCHIVED(
            "PHASE_DEFINITION_ALREADY_ARCHIVED", "Phase definition is already archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_IN_USE(
            "PHASE_DEFINITION_IN_USE", "Phase definition is in use by one or more projects and cannot be archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_INVALID_SCOPE(
            "PHASE_DEFINITION_INVALID_SCOPE", "Phase definition scope is invalid for the provided identifiers", HttpStatus.BAD_REQUEST),
    PHASE_DEFINITION_ARCHIVED(
            "PHASE_DEFINITION_ARCHIVED", "Phase definition is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_INACTIVE(
            "PHASE_DEFINITION_INACTIVE", "Phase definition is inactive and cannot be used", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_BUILT_IN_CANNOT_DELETE(
            "PHASE_DEFINITION_BUILT_IN_CANNOT_DELETE", "Built-in phase definitions cannot be hard-deleted", HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Project Phase ─────────────────────────────────────────────────────────
    PROJECT_PHASE_NOT_FOUND(
            "PROJECT_PHASE_NOT_FOUND", "Project phase not found", HttpStatus.NOT_FOUND),
    PROJECT_PHASE_PROJECT_MISMATCH(
            "PROJECT_PHASE_PROJECT_MISMATCH", "Project phase does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_PHASE_PATH_MISMATCH(
            "PROJECT_PHASE_PATH_MISMATCH", "Project phase path does not match the parent project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_PHASE_ACCESS_DENIED(
            "PROJECT_PHASE_ACCESS_DENIED", "Access to the project phase is denied", HttpStatus.FORBIDDEN),
    PROJECT_PHASE_CODE_ALREADY_EXISTS(
            "PROJECT_PHASE_CODE_ALREADY_EXISTS", "Phase code already exists in this project", HttpStatus.CONFLICT),
    PROJECT_PHASE_DISPLAY_ORDER_CONFLICT(
            "PROJECT_PHASE_DISPLAY_ORDER_CONFLICT", "A phase with this display order already exists in the project", HttpStatus.CONFLICT),
    PROJECT_PHASE_INVALID_DATE_RANGE(
            "PROJECT_PHASE_INVALID_DATE_RANGE", "Phase end date must not be before start date", HttpStatus.BAD_REQUEST),
    PROJECT_PHASE_ALREADY_ARCHIVED(
            "PROJECT_PHASE_ALREADY_ARCHIVED", "Project phase is already archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_PHASE_CANNOT_ARCHIVE(
            "PROJECT_PHASE_CANNOT_ARCHIVE", "Project phase cannot be archived because it has active WBS nodes or tasks", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_PHASE_NOT_ACTIVE(
            "PROJECT_PHASE_NOT_ACTIVE", "Project phase must be in PLANNED or ACTIVE status", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_NOT_ACTIVE(
            "PHASE_DEFINITION_NOT_ACTIVE", "Phase definition is not active and cannot be used", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_WORKSPACE_MISMATCH(
            "PHASE_DEFINITION_WORKSPACE_MISMATCH", "Phase definition does not belong to the project's workspace", HttpStatus.UNPROCESSABLE_ENTITY),

    // ── WBS Node ──────────────────────────────────────────────────────────────
    WBS_NODE_NOT_FOUND(
            "WBS_NODE_NOT_FOUND", "WBS node not found", HttpStatus.NOT_FOUND),
    WBS_NODE_CODE_ALREADY_EXISTS(
            "WBS_NODE_CODE_ALREADY_EXISTS", "WBS node code already exists in this project", HttpStatus.CONFLICT),
    WBS_NODE_SORT_ORDER_CONFLICT(
            "WBS_NODE_SORT_ORDER_CONFLICT", "A WBS node with this sort order already exists under the same parent", HttpStatus.CONFLICT),
    WBS_NODE_ALREADY_ARCHIVED(
            "WBS_NODE_ALREADY_ARCHIVED", "WBS node is already archived", HttpStatus.UNPROCESSABLE_ENTITY),
    WBS_NODE_CANNOT_ARCHIVE(
            "WBS_NODE_CANNOT_ARCHIVE", "WBS node cannot be archived because it has active children or linked tasks", HttpStatus.UNPROCESSABLE_ENTITY),
    WBS_NODE_PHASE_MISMATCH(
            "WBS_NODE_PHASE_MISMATCH", "WBS node does not belong to the specified project phase", HttpStatus.UNPROCESSABLE_ENTITY),
    WBS_NODE_CIRCULAR_PARENT(
            "WBS_NODE_CIRCULAR_PARENT", "Setting this parent would create a circular relationship", HttpStatus.UNPROCESSABLE_ENTITY),
    WBS_NODE_PROJECT_MISMATCH(
            "WBS_NODE_PROJECT_MISMATCH", "WBS node does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_WBS_PATH_MISMATCH(
            "PROJECT_WBS_PATH_MISMATCH", "WBS node path does not match the parent project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_WBS_ACCESS_DENIED(
            "PROJECT_WBS_ACCESS_DENIED", "Access to the WBS node is denied", HttpStatus.FORBIDDEN),

    // ── Task ──────────────────────────────────────────────────────────────────
    TASK_NOT_FOUND(
            "TASK_NOT_FOUND", "Task not found", HttpStatus.NOT_FOUND),
    TASK_CODE_ALREADY_EXISTS(
            "TASK_CODE_ALREADY_EXISTS", "Task code already exists in this project", HttpStatus.CONFLICT),
    TASK_INVALID_DATE_RANGE(
            "TASK_INVALID_DATE_RANGE", "Task due date must not be before planned start date", HttpStatus.BAD_REQUEST),
    TASK_INVALID_ESTIMATE(
            "TASK_INVALID_ESTIMATE", "Task estimate hours must be greater than zero", HttpStatus.BAD_REQUEST),
    TASK_ESTIMATE_REQUIRED(
            "TASK_ESTIMATE_REQUIRED", "Task estimate hours is required", HttpStatus.BAD_REQUEST),
    TASK_PROJECT_MISMATCH(
            "TASK_PROJECT_MISMATCH", "Task does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TASK_PATH_MISMATCH(
            "PROJECT_TASK_PATH_MISMATCH", "Task path does not match the parent project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TASK_ACCESS_DENIED(
            "PROJECT_TASK_ACCESS_DENIED", "Access to the task is denied", HttpStatus.FORBIDDEN),
    TASK_CANNOT_TRANSITION(
            "TASK_CANNOT_TRANSITION", "Task cannot transition to the requested status from its current status", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER(
            "TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER", "The assigned user is not an active member of the workspace", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_ENTITY_MISMATCH(
            "TASK_ENTITY_MISMATCH", "Phase or WBS node does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Task Dependency ───────────────────────────────────────────────────────
    TASK_DEPENDENCY_NOT_FOUND(
            "TASK_DEPENDENCY_NOT_FOUND", "Task dependency not found", HttpStatus.NOT_FOUND),
    TASK_DEPENDENCY_ALREADY_EXISTS(
            "TASK_DEPENDENCY_ALREADY_EXISTS", "A dependency with the same predecessor, successor and type already exists", HttpStatus.CONFLICT),
    TASK_DEPENDENCY_CIRCULAR(
            "TASK_DEPENDENCY_CIRCULAR", "Adding this dependency would create a circular dependency chain", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_DEPENDENCY_SELF_REFERENCE(
            "TASK_DEPENDENCY_SELF_REFERENCE", "A task cannot depend on itself", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_DEPENDENCY_DIFFERENT_PROJECTS(
            "TASK_DEPENDENCY_DIFFERENT_PROJECTS", "Predecessor and successor tasks must belong to the same project", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_DEPENDENCY_PROJECT_MISMATCH(
            "TASK_DEPENDENCY_PROJECT_MISMATCH", "Task dependency does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_DEPENDENCY_PATH_MISMATCH(
            "PROJECT_DEPENDENCY_PATH_MISMATCH", "Task dependency path does not match the parent project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TASK_DEPENDENCY_ACCESS_DENIED(
            "PROJECT_TASK_DEPENDENCY_ACCESS_DENIED", "Access to the task dependency is denied", HttpStatus.FORBIDDEN),
    TASK_DEPENDENCY_TASK_NOT_ELIGIBLE(
            "TASK_DEPENDENCY_TASK_NOT_ELIGIBLE",
            "Dependencies cannot be created for ARCHIVED, CANCELLED, or DONE tasks",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Project Template (Phase 11) ───────────────────────────────────────────
    PROJECT_TEMPLATE_NOT_FOUND(
            "PROJECT_TEMPLATE_NOT_FOUND", "Project template not found", HttpStatus.NOT_FOUND),
    PROJECT_TEMPLATE_CODE_ALREADY_EXISTS(
            "PROJECT_TEMPLATE_CODE_ALREADY_EXISTS", "Project template code already exists in this scope", HttpStatus.CONFLICT),
    PROJECT_TEMPLATE_INVALID_SCOPE(
            "PROJECT_TEMPLATE_INVALID_SCOPE", "Project template scope is invalid for the provided identifiers", HttpStatus.BAD_REQUEST),
    PROJECT_TEMPLATE_ARCHIVED(
            "PROJECT_TEMPLATE_ARCHIVED", "Project template is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_INACTIVE(
            "PROJECT_TEMPLATE_INACTIVE", "Project template is inactive", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_NO_PUBLISHED_VERSION(
            "PROJECT_TEMPLATE_NO_PUBLISHED_VERSION", "Active template requires at least one published version", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_ACCESS_DENIED(
            "PROJECT_TEMPLATE_ACCESS_DENIED", "Access to the project template is denied", HttpStatus.FORBIDDEN),

    PROJECT_TEMPLATE_VERSION_NOT_FOUND(
            "PROJECT_TEMPLATE_VERSION_NOT_FOUND", "Project template version not found", HttpStatus.NOT_FOUND),
    PROJECT_TEMPLATE_VERSION_NOT_DRAFT(
            "PROJECT_TEMPLATE_VERSION_NOT_DRAFT", "Only DRAFT template versions can be edited", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_VERSION_NOT_PUBLISHED(
            "PROJECT_TEMPLATE_VERSION_NOT_PUBLISHED", "Template version must be PUBLISHED", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_VERSION_ALREADY_PUBLISHED(
            "PROJECT_TEMPLATE_VERSION_ALREADY_PUBLISHED", "Template version is already published", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_VERSION_STRUCTURE_INVALID(
            "PROJECT_TEMPLATE_VERSION_STRUCTURE_INVALID", "Template version structure is invalid for publish", HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_TEMPLATE_PHASE_NOT_FOUND(
            "PROJECT_TEMPLATE_PHASE_NOT_FOUND", "Template phase not found", HttpStatus.NOT_FOUND),
    PROJECT_TEMPLATE_PHASE_PATH_MISMATCH(
            "PROJECT_TEMPLATE_PHASE_PATH_MISMATCH", "Template phase does not belong to the specified template version", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_PHASE_HAS_TASKS(
            "PROJECT_TEMPLATE_PHASE_HAS_TASKS", "Template phase has tasks; cascade delete required", HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_TEMPLATE_WBS_NODE_NOT_FOUND(
            "PROJECT_TEMPLATE_WBS_NODE_NOT_FOUND", "Template WBS node not found", HttpStatus.NOT_FOUND),
    PROJECT_TEMPLATE_WBS_NODE_PATH_MISMATCH(
            "PROJECT_TEMPLATE_WBS_NODE_PATH_MISMATCH", "Template WBS node does not belong to the specified template version", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_WBS_NODE_PARENT_INVALID(
            "PROJECT_TEMPLATE_WBS_NODE_PARENT_INVALID", "Template WBS parent is invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_WBS_NODE_CYCLE_DETECTED(
            "PROJECT_TEMPLATE_WBS_NODE_CYCLE_DETECTED", "Template WBS move would create a cycle", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_WBS_NODE_HAS_CHILDREN(
            "PROJECT_TEMPLATE_WBS_NODE_HAS_CHILDREN", "Template WBS node has children; cascade delete required", HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_TEMPLATE_TASK_NOT_FOUND(
            "PROJECT_TEMPLATE_TASK_NOT_FOUND", "Template task not found", HttpStatus.NOT_FOUND),
    PROJECT_TEMPLATE_TASK_PATH_MISMATCH(
            "PROJECT_TEMPLATE_TASK_PATH_MISMATCH", "Template task does not belong to the specified template version", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_TASK_INVALID_ESTIMATE(
            "PROJECT_TEMPLATE_TASK_INVALID_ESTIMATE", "Template task estimate hours must be null or greater than zero", HttpStatus.BAD_REQUEST),
    PROJECT_TEMPLATE_TASK_PHASE_MISMATCH(
            "PROJECT_TEMPLATE_TASK_PHASE_MISMATCH", "Template task phase must belong to the same template version", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_TASK_WBS_MISMATCH(
            "PROJECT_TEMPLATE_TASK_WBS_MISMATCH", "Template task WBS node must belong to the same template version", HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_TEMPLATE_DEPENDENCY_NOT_FOUND(
            "PROJECT_TEMPLATE_DEPENDENCY_NOT_FOUND", "Template task dependency not found", HttpStatus.NOT_FOUND),
    PROJECT_TEMPLATE_DEPENDENCY_DUPLICATE(
            "PROJECT_TEMPLATE_DEPENDENCY_DUPLICATE", "Template task dependency already exists", HttpStatus.CONFLICT),
    PROJECT_TEMPLATE_DEPENDENCY_SELF_NOT_ALLOWED(
            "PROJECT_TEMPLATE_DEPENDENCY_SELF_NOT_ALLOWED", "A template task cannot depend on itself", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_DEPENDENCY_CYCLE_DETECTED(
            "PROJECT_TEMPLATE_DEPENDENCY_CYCLE_DETECTED", "Template task dependency would create a cycle", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_DEPENDENCY_TASK_MISMATCH(
            "PROJECT_TEMPLATE_DEPENDENCY_TASK_MISMATCH", "Template dependency tasks must belong to the same template version", HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_TEMPLATE_APPLY_FAILED(
            "PROJECT_TEMPLATE_APPLY_FAILED", "Failed to apply project template", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_APPLY_WORKSPACE_INACTIVE(
            "PROJECT_TEMPLATE_APPLY_WORKSPACE_INACTIVE", "Target workspace must be ACTIVE to apply a template", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_TEMPLATE_APPLY_PERMISSION_DENIED(
            "PROJECT_TEMPLATE_APPLY_PERMISSION_DENIED", "Missing permission to apply project template", HttpStatus.FORBIDDEN),
    PROJECT_TEMPLATE_APPLY_DUPLICATE_PROJECT_CODE(
            "PROJECT_TEMPLATE_APPLY_DUPLICATE_PROJECT_CODE", "Project code already exists in the target workspace", HttpStatus.CONFLICT),

    SCHEDULE_RUN_NOT_FOUND("SCHEDULE_RUN_NOT_FOUND","Schedule run not found",HttpStatus.NOT_FOUND),
    SCHEDULE_RUN_PROJECT_ARCHIVED("SCHEDULE_RUN_PROJECT_ARCHIVED","Archived projects cannot be scheduled",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_RUN_INVALID_DATE_RANGE("SCHEDULE_RUN_INVALID_DATE_RANGE","Schedule planning range is invalid",HttpStatus.BAD_REQUEST),
    SCHEDULE_RUN_RANGE_TOO_LARGE("SCHEDULE_RUN_RANGE_TOO_LARGE","Schedule planning range exceeds 365 days",HttpStatus.BAD_REQUEST),
    SCHEDULE_RUN_ALREADY_RUNNING("SCHEDULE_RUN_ALREADY_RUNNING","A schedule run is already running",HttpStatus.CONFLICT),
    SCHEDULE_RUN_NOT_CANCELLABLE("SCHEDULE_RUN_NOT_CANCELLABLE","Schedule run is not cancellable",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_RUN_FAILED("SCHEDULE_RUN_FAILED","Schedule run failed",HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_SCHEDULE_NOT_FOUND("TASK_SCHEDULE_NOT_FOUND","Task schedule not found",HttpStatus.NOT_FOUND),
    TASK_SCHEDULE_TASK_NOT_FOUND("TASK_SCHEDULE_TASK_NOT_FOUND","Task for schedule not found",HttpStatus.NOT_FOUND),
    TASK_SCHEDULE_TASK_PROJECT_MISMATCH("TASK_SCHEDULE_TASK_PROJECT_MISMATCH","Scheduled task does not belong to project",HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_SCHEDULE_TASK_NO_ASSIGNEE("TASK_SCHEDULE_TASK_NO_ASSIGNEE","Task has no in-charge user",HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_SCHEDULE_ASSIGNEE_INACTIVE("TASK_SCHEDULE_ASSIGNEE_INACTIVE","Task in-charge user is inactive",HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_SCHEDULE_NO_CAPACITY("TASK_SCHEDULE_NO_CAPACITY","Task has no scheduling capacity",HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_SCHEDULE_UNSUPPORTED_DEPENDENCY_TYPE("TASK_SCHEDULE_UNSUPPORTED_DEPENDENCY_TYPE","Dependency type is not supported",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_DEPENDENCY_CYCLE_DETECTED("SCHEDULE_DEPENDENCY_CYCLE_DETECTED","Dependency cycle detected",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_DEPENDENCY_PREDECESSOR_UNSCHEDULED("SCHEDULE_DEPENDENCY_PREDECESSOR_UNSCHEDULED","Dependency predecessor is unscheduled",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_CAPACITY_PROFILE_MISSING("SCHEDULE_CAPACITY_PROFILE_MISSING","Capacity profile is missing",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_PROJECT_ALLOCATION_MISSING("SCHEDULE_PROJECT_ALLOCATION_MISSING","Project allocation is missing",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_CALENDAR_MISSING("SCHEDULE_CALENDAR_MISSING","Working calendar is missing",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_NON_WORKING_DAY("SCHEDULE_NON_WORKING_DAY","Work cannot be scheduled on a non-working day",HttpStatus.UNPROCESSABLE_ENTITY),
    SCHEDULE_ACCESS_DENIED("SCHEDULE_ACCESS_DENIED","Schedule access denied",HttpStatus.FORBIDDEN),

    // Phase 14 — Gantt (spec §22)
    GANTT_PROJECT_NOT_FOUND("GANTT_PROJECT_NOT_FOUND", "Gantt project not found", HttpStatus.NOT_FOUND),
    GANTT_PROJECT_ARCHIVED("GANTT_PROJECT_ARCHIVED", "Archived projects cannot use Gantt mutations", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_ACCESS_DENIED("GANTT_ACCESS_DENIED", "Gantt access denied", HttpStatus.FORBIDDEN),
    GANTT_SCHEDULE_RUN_NOT_FOUND("GANTT_SCHEDULE_RUN_NOT_FOUND", "Gantt schedule run not found", HttpStatus.NOT_FOUND),
    GANTT_SCHEDULE_RUN_PROJECT_MISMATCH("GANTT_SCHEDULE_RUN_PROJECT_MISMATCH", "Schedule run does not belong to this project", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_NO_COMPLETED_SCHEDULE_RUN("GANTT_NO_COMPLETED_SCHEDULE_RUN", "No completed schedule run available for Gantt", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_PROJECTION_FAILED("GANTT_PROJECTION_FAILED", "Failed to build Gantt projection", HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_MILESTONE_NOT_FOUND("PROJECT_MILESTONE_NOT_FOUND", "Project milestone not found", HttpStatus.NOT_FOUND),
    PROJECT_MILESTONE_PATH_MISMATCH("PROJECT_MILESTONE_PATH_MISMATCH", "Milestone does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_MILESTONE_INVALID_DATE("PROJECT_MILESTONE_INVALID_DATE", "Milestone date is invalid", HttpStatus.BAD_REQUEST),
    PROJECT_MILESTONE_PHASE_MISMATCH("PROJECT_MILESTONE_PHASE_MISMATCH", "Milestone phase does not belong to the project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_MILESTONE_WBS_MISMATCH("PROJECT_MILESTONE_WBS_MISMATCH", "Milestone WBS node does not belong to the project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_MILESTONE_ARCHIVED("PROJECT_MILESTONE_ARCHIVED", "Milestone is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_MILESTONE_CODE_ALREADY_EXISTS("PROJECT_MILESTONE_CODE_ALREADY_EXISTS", "Milestone code already exists in this project", HttpStatus.CONFLICT),

    GANTT_TASK_NOT_FOUND("GANTT_TASK_NOT_FOUND", "Gantt task not found", HttpStatus.NOT_FOUND),
    GANTT_TASK_PATH_MISMATCH("GANTT_TASK_PATH_MISMATCH", "Task does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_TASK_MOVE_NOT_SUPPORTED("GANTT_TASK_MOVE_NOT_SUPPORTED", "Gantt task move is not supported", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_TASK_RESIZE_NOT_SUPPORTED("GANTT_TASK_RESIZE_NOT_SUPPORTED", "Gantt task resize is not supported", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_TASK_OVERRIDE_NOT_FOUND("GANTT_TASK_OVERRIDE_NOT_FOUND", "Active schedule override not found", HttpStatus.NOT_FOUND),
    GANTT_TASK_OVERRIDE_INVALID_DATE_RANGE("GANTT_TASK_OVERRIDE_INVALID_DATE_RANGE", "Schedule override date range is invalid", HttpStatus.BAD_REQUEST),
    GANTT_TASK_OVERRIDE_REASON_REQUIRED("GANTT_TASK_OVERRIDE_REASON_REQUIRED", "Schedule override reason is required", HttpStatus.BAD_REQUEST),
    GANTT_TASK_OVERRIDE_CONFLICT("GANTT_TASK_OVERRIDE_CONFLICT", "Conflicting active schedule override exists", HttpStatus.CONFLICT),

    GANTT_DEPENDENCY_DUPLICATE("GANTT_DEPENDENCY_DUPLICATE", "Gantt dependency already exists", HttpStatus.CONFLICT),
    GANTT_DEPENDENCY_SELF_NOT_ALLOWED("GANTT_DEPENDENCY_SELF_NOT_ALLOWED", "Self dependency is not allowed", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_DEPENDENCY_CYCLE_DETECTED("GANTT_DEPENDENCY_CYCLE_DETECTED", "Gantt dependency would create a cycle", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_DEPENDENCY_PROJECT_MISMATCH("GANTT_DEPENDENCY_PROJECT_MISMATCH", "Dependency does not belong to the project", HttpStatus.UNPROCESSABLE_ENTITY),

    GANTT_RECALCULATION_FAILED("GANTT_RECALCULATION_FAILED", "Gantt recalculation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    GANTT_INVALID_EXPORT_FORMAT("GANTT_INVALID_EXPORT_FORMAT", "Gantt export format must be CSV or JSON", HttpStatus.BAD_REQUEST),

    POST_BASELINE_EDIT_BLOCKED(
            "POST_BASELINE_EDIT_BLOCKED",
            "Direct edit blocked after current baseline; create a ChangeRequest",
            HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProjectErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
