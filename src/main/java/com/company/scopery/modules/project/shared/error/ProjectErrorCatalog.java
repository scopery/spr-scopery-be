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

    // ── Phase Definition ──────────────────────────────────────────────────────
    PHASE_DEFINITION_NOT_FOUND(
            "PHASE_DEFINITION_NOT_FOUND", "Phase definition not found", HttpStatus.NOT_FOUND),
    PHASE_DEFINITION_CODE_ALREADY_EXISTS(
            "PHASE_DEFINITION_CODE_ALREADY_EXISTS", "Phase definition code already exists", HttpStatus.CONFLICT),
    PHASE_DEFINITION_ALREADY_ARCHIVED(
            "PHASE_DEFINITION_ALREADY_ARCHIVED", "Phase definition is already archived", HttpStatus.UNPROCESSABLE_ENTITY),
    PHASE_DEFINITION_IN_USE(
            "PHASE_DEFINITION_IN_USE", "Phase definition is in use by one or more projects and cannot be archived", HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Project Phase ─────────────────────────────────────────────────────────
    PROJECT_PHASE_NOT_FOUND(
            "PROJECT_PHASE_NOT_FOUND", "Project phase not found", HttpStatus.NOT_FOUND),
    PROJECT_PHASE_PROJECT_MISMATCH(
            "PROJECT_PHASE_PROJECT_MISMATCH", "Project phase does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
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

    // ── Task ──────────────────────────────────────────────────────────────────
    TASK_NOT_FOUND(
            "TASK_NOT_FOUND", "Task not found", HttpStatus.NOT_FOUND),
    TASK_CODE_ALREADY_EXISTS(
            "TASK_CODE_ALREADY_EXISTS", "Task code already exists in this project", HttpStatus.CONFLICT),
    TASK_INVALID_DATE_RANGE(
            "TASK_INVALID_DATE_RANGE", "Task due date must not be before planned start date", HttpStatus.BAD_REQUEST),
    TASK_INVALID_ESTIMATE(
            "TASK_INVALID_ESTIMATE", "Task estimate hours must be greater than zero", HttpStatus.BAD_REQUEST),
    TASK_PROJECT_MISMATCH(
            "TASK_PROJECT_MISMATCH", "Task does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY),
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
            "TASK_DEPENDENCY_PROJECT_MISMATCH", "Task dependency does not belong to the specified project", HttpStatus.UNPROCESSABLE_ENTITY);

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
