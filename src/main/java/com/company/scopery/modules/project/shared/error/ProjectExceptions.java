package com.company.scopery.modules.project.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ProjectExceptions {

    private ProjectExceptions() {}

    // ── Project ───────────────────────────────────────────────────────────────

    public static AppException projectNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_NOT_FOUND,
                "Project not found: " + id, Map.of("id", id));
    }

    public static AppException projectCodeAlreadyExists(String code) {
        return new AppException(ProjectErrorCatalog.PROJECT_CODE_ALREADY_EXISTS,
                "Project code already exists: " + code, Map.of("code", code));
    }

    public static AppException projectInvalidDateRange() {
        return new AppException(ProjectErrorCatalog.PROJECT_INVALID_DATE_RANGE);
    }

    public static AppException projectAlreadyArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_ALREADY_ARCHIVED,
                "Project is already archived: " + id, Map.of("id", id));
    }

    public static AppException projectNotActive(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_NOT_ACTIVE,
                "Project is not active: " + id, Map.of("id", id));
    }

    public static AppException projectNotActiveOrDraft(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_NOT_ACTIVE_OR_DRAFT,
                "Project must be DRAFT or ACTIVE: " + id, Map.of("id", id));
    }

    public static AppException projectCannotHold(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_CANNOT_HOLD,
                "Only an ACTIVE project can be put on hold: " + id, Map.of("id", id));
    }

    public static AppException projectCannotComplete(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_CANNOT_COMPLETE,
                "Only an ACTIVE project can be completed: " + id, Map.of("id", id));
    }

    // ── Phase Definition ──────────────────────────────────────────────────────

    public static AppException phaseDefinitionNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_NOT_FOUND,
                "Phase definition not found: " + id, Map.of("id", id));
    }

    public static AppException phaseDefinitionCodeAlreadyExists(String code) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_CODE_ALREADY_EXISTS,
                "Phase definition code already exists: " + code, Map.of("code", code));
    }

    public static AppException phaseDefinitionAlreadyArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_ALREADY_ARCHIVED,
                "Phase definition is already archived: " + id, Map.of("id", id));
    }

    public static AppException phaseDefinitionInUse(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_IN_USE,
                "Phase definition is in use by projects: " + id, Map.of("id", id));
    }

    public static AppException phaseDefinitionNotActive(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_NOT_ACTIVE,
                "Phase definition is not active: " + id, Map.of("id", id));
    }

    public static AppException phaseDefinitionWorkspaceMismatch(UUID definitionId, UUID workspaceId) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_WORKSPACE_MISMATCH,
                "Phase definition " + definitionId + " does not belong to workspace " + workspaceId,
                Map.of("definitionId", definitionId, "workspaceId", workspaceId));
    }

    // ── Project Phase ─────────────────────────────────────────────────────────

    public static AppException projectPhaseNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_NOT_FOUND,
                "Project phase not found: " + id, Map.of("id", id));
    }

    public static AppException projectPhaseProjectMismatch(UUID phaseId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_PROJECT_MISMATCH,
                "Project phase " + phaseId + " does not belong to project " + projectId,
                Map.of("phaseId", phaseId, "projectId", projectId));
    }

    public static AppException projectPhaseCodeAlreadyExists(String code, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_CODE_ALREADY_EXISTS,
                "Phase code already exists in project " + projectId + ": " + code,
                Map.of("code", code, "projectId", projectId));
    }

    public static AppException projectPhaseDisplayOrderConflict(int displayOrder, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_DISPLAY_ORDER_CONFLICT,
                "Display order " + displayOrder + " already exists in project " + projectId,
                Map.of("displayOrder", displayOrder, "projectId", projectId));
    }

    public static AppException projectPhaseInvalidDateRange() {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_INVALID_DATE_RANGE);
    }

    public static AppException projectPhaseAlreadyArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_ALREADY_ARCHIVED,
                "Project phase is already archived: " + id, Map.of("id", id));
    }

    public static AppException projectPhaseCannotArchive(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_CANNOT_ARCHIVE,
                "Project phase has active WBS nodes or tasks: " + id, Map.of("id", id));
    }

    public static AppException projectPhaseNotActive(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_PHASE_NOT_ACTIVE,
                "Project phase must be PLANNED or ACTIVE: " + id, Map.of("id", id));
    }

    // ── WBS Node ──────────────────────────────────────────────────────────────

    public static AppException wbsNodeNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_NOT_FOUND,
                "WBS node not found: " + id, Map.of("id", id));
    }

    public static AppException wbsNodeCodeAlreadyExists(String code, UUID projectId) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_CODE_ALREADY_EXISTS,
                "WBS node code already exists in project " + projectId + ": " + code,
                Map.of("code", code, "projectId", projectId));
    }

    public static AppException wbsNodeSortOrderConflict(int sortOrder) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_SORT_ORDER_CONFLICT,
                "Sort order " + sortOrder + " already exists under this parent",
                Map.of("sortOrder", sortOrder));
    }

    public static AppException wbsNodeAlreadyArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_ALREADY_ARCHIVED,
                "WBS node is already archived: " + id, Map.of("id", id));
    }

    public static AppException wbsNodeCannotArchive(UUID id) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_CANNOT_ARCHIVE,
                "WBS node has active children or linked tasks: " + id, Map.of("id", id));
    }

    public static AppException wbsNodePhaseMismatch(UUID nodeId, UUID phaseId) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_PHASE_MISMATCH,
                "WBS node " + nodeId + " does not belong to phase " + phaseId,
                Map.of("nodeId", nodeId, "phaseId", phaseId));
    }

    public static AppException wbsNodeCircularParent() {
        return new AppException(ProjectErrorCatalog.WBS_NODE_CIRCULAR_PARENT);
    }

    public static AppException wbsNodeProjectMismatch(UUID nodeId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.WBS_NODE_PROJECT_MISMATCH,
                "WBS node " + nodeId + " does not belong to project " + projectId,
                Map.of("nodeId", nodeId, "projectId", projectId));
    }

    public static AppException taskProjectMismatch(UUID taskId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.TASK_PROJECT_MISMATCH,
                "Task " + taskId + " does not belong to project " + projectId,
                Map.of("taskId", taskId, "projectId", projectId));
    }

    // ── Task ──────────────────────────────────────────────────────────────────

    public static AppException taskNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.TASK_NOT_FOUND,
                "Task not found: " + id, Map.of("id", id));
    }

    public static AppException taskCodeAlreadyExists(String code, UUID projectId) {
        return new AppException(ProjectErrorCatalog.TASK_CODE_ALREADY_EXISTS,
                "Task code already exists in project " + projectId + ": " + code,
                Map.of("code", code, "projectId", projectId));
    }

    public static AppException taskInvalidDateRange() {
        return new AppException(ProjectErrorCatalog.TASK_INVALID_DATE_RANGE);
    }

    public static AppException taskInvalidEstimate() {
        return new AppException(ProjectErrorCatalog.TASK_INVALID_ESTIMATE);
    }

    public static AppException taskCannotTransition(String currentStatus, String targetStatus) {
        return new AppException(ProjectErrorCatalog.TASK_CANNOT_TRANSITION,
                "Cannot transition task from " + currentStatus + " to " + targetStatus,
                Map.of("currentStatus", currentStatus, "targetStatus", targetStatus));
    }

    public static AppException taskAssigneeNotWorkspaceMember(UUID userId) {
        return new AppException(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER,
                "User " + userId + " is not an active workspace member",
                Map.of("userId", userId));
    }

    public static AppException taskEntityMismatch(String entity, UUID entityId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.TASK_ENTITY_MISMATCH,
                entity + " " + entityId + " does not belong to project " + projectId,
                Map.of("entityId", entityId, "projectId", projectId));
    }

    // ── Task Dependency ───────────────────────────────────────────────────────

    public static AppException taskDependencyNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_NOT_FOUND,
                "Task dependency not found: " + id, Map.of("id", id));
    }

    public static AppException taskDependencyProjectMismatch(UUID dependencyId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_PROJECT_MISMATCH,
                "Task dependency " + dependencyId + " does not belong to project " + projectId,
                Map.of("dependencyId", dependencyId, "projectId", projectId));
    }

    public static AppException taskDependencyAlreadyExists(UUID predecessorId, UUID successorId) {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_ALREADY_EXISTS,
                "Dependency already exists between tasks " + predecessorId + " and " + successorId,
                Map.of("predecessorId", predecessorId, "successorId", successorId));
    }

    public static AppException taskDependencyCircular() {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_CIRCULAR);
    }

    public static AppException taskDependencySelfReference(UUID taskId) {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_SELF_REFERENCE,
                "Task cannot depend on itself: " + taskId, Map.of("taskId", taskId));
    }

    public static AppException taskDependencyDifferentProjects(UUID predecessorId, UUID successorId) {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_DIFFERENT_PROJECTS,
                "Tasks " + predecessorId + " and " + successorId + " belong to different projects",
                Map.of("predecessorId", predecessorId, "successorId", successorId));
    }
}
