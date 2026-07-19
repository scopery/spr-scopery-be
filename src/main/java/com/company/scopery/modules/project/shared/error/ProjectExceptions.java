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

    public static AppException projectWorkspaceNotFound(UUID workspaceId) {
        return new AppException(ProjectErrorCatalog.PROJECT_WORKSPACE_NOT_FOUND,
                "Workspace not found: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException projectWorkspaceNotActive(UUID workspaceId) {
        return new AppException(ProjectErrorCatalog.PROJECT_WORKSPACE_NOT_ACTIVE,
                "Workspace is not active: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException projectOwnerNotWorkspaceMember(UUID ownerUserId) {
        return new AppException(ProjectErrorCatalog.PROJECT_OWNER_NOT_WORKSPACE_MEMBER,
                "Owner is not an active workspace member: " + ownerUserId,
                Map.of("ownerUserId", ownerUserId));
    }

    public static AppException projectCannotActivate(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_CANNOT_ACTIVATE,
                "Only a DRAFT project can be activated: " + id, Map.of("id", id));
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

    public static AppException taskEstimateRequired() {
        return new AppException(ProjectErrorCatalog.TASK_ESTIMATE_REQUIRED);
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

    public static AppException taskDependencyTaskNotEligible(UUID taskId, String status) {
        return new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_TASK_NOT_ELIGIBLE,
                "Task " + taskId + " with status " + status + " is not eligible for dependencies",
                Map.of("taskId", taskId, "status", status));
    }

    // ── Phase Definition (Phase 11) ───────────────────────────────────────────

    public static AppException phaseDefinitionInvalidScope(String detail) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_INVALID_SCOPE, detail, Map.of());
    }

    public static AppException phaseDefinitionArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_ARCHIVED,
                "Phase definition is archived: " + id, Map.of("id", id));
    }

    public static AppException phaseDefinitionInactive(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_INACTIVE,
                "Phase definition is inactive: " + id, Map.of("id", id));
    }

    public static AppException phaseDefinitionBuiltInCannotDelete(UUID id) {
        return new AppException(ProjectErrorCatalog.PHASE_DEFINITION_BUILT_IN_CANNOT_DELETE,
                "Built-in phase definition cannot be hard-deleted: " + id, Map.of("id", id));
    }

    // ── Project Template (Phase 11) ───────────────────────────────────────────

    public static AppException projectTemplateNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_NOT_FOUND,
                "Project template not found: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateCodeAlreadyExists(String code) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_CODE_ALREADY_EXISTS,
                "Project template code already exists: " + code, Map.of("code", code));
    }

    public static AppException projectTemplateInvalidScope(String detail) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_INVALID_SCOPE, detail, Map.of());
    }

    public static AppException projectTemplateArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_ARCHIVED,
                "Project template is archived: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateInactive(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_INACTIVE,
                "Project template is inactive: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateNoPublishedVersion(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_NO_PUBLISHED_VERSION,
                "Template has no published version: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateAccessDenied(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_ACCESS_DENIED,
                "Access denied to template: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateVersionNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_NOT_FOUND,
                "Template version not found: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateVersionNotDraft(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_NOT_DRAFT,
                "Template version is not DRAFT: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateVersionNotPublished(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_NOT_PUBLISHED,
                "Template version is not PUBLISHED: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateVersionAlreadyPublished(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_ALREADY_PUBLISHED,
                "Template version already published: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateVersionStructureInvalid(String detail) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_STRUCTURE_INVALID, detail, Map.of());
    }

    public static AppException projectTemplatePhaseNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_PHASE_NOT_FOUND,
                "Template phase not found: " + id, Map.of("id", id));
    }

    public static AppException projectTemplatePhasePathMismatch(UUID phaseId, UUID versionId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_PHASE_PATH_MISMATCH,
                "Template phase " + phaseId + " does not belong to version " + versionId,
                Map.of("phaseId", phaseId, "versionId", versionId));
    }

    public static AppException projectTemplatePhaseHasTasks(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_PHASE_HAS_TASKS,
                "Template phase has tasks: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateWbsNodeNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_WBS_NODE_NOT_FOUND,
                "Template WBS node not found: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateWbsNodePathMismatch(UUID nodeId, UUID versionId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_WBS_NODE_PATH_MISMATCH,
                "Template WBS node " + nodeId + " does not belong to version " + versionId,
                Map.of("nodeId", nodeId, "versionId", versionId));
    }

    public static AppException projectTemplateWbsNodeParentInvalid(UUID parentId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_WBS_NODE_PARENT_INVALID,
                "Invalid template WBS parent: " + parentId, Map.of("parentId", parentId));
    }

    public static AppException projectTemplateWbsNodeCycleDetected() {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_WBS_NODE_CYCLE_DETECTED);
    }

    public static AppException projectTemplateWbsNodeHasChildren(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_WBS_NODE_HAS_CHILDREN,
                "Template WBS node has children: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateTaskNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_TASK_NOT_FOUND,
                "Template task not found: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateTaskPathMismatch(UUID taskId, UUID versionId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_TASK_PATH_MISMATCH,
                "Template task " + taskId + " does not belong to version " + versionId,
                Map.of("taskId", taskId, "versionId", versionId));
    }

    public static AppException projectTemplateTaskInvalidEstimate() {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_TASK_INVALID_ESTIMATE);
    }

    public static AppException projectTemplateTaskPhaseMismatch(UUID taskPhaseId, UUID versionId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_TASK_PHASE_MISMATCH,
                "Template phase " + taskPhaseId + " does not belong to version " + versionId,
                Map.of("templatePhaseId", taskPhaseId, "versionId", versionId));
    }

    public static AppException projectTemplateTaskWbsMismatch(UUID wbsId, UUID versionId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_TASK_WBS_MISMATCH,
                "Template WBS " + wbsId + " does not belong to version " + versionId,
                Map.of("templateWbsNodeId", wbsId, "versionId", versionId));
    }

    public static AppException projectTemplateDependencyNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_DEPENDENCY_NOT_FOUND,
                "Template dependency not found: " + id, Map.of("id", id));
    }

    public static AppException projectTemplateDependencyDuplicate(UUID pred, UUID succ) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_DEPENDENCY_DUPLICATE,
                "Template dependency already exists", Map.of("predecessorId", pred, "successorId", succ));
    }

    public static AppException projectTemplateDependencySelfNotAllowed(UUID taskId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_DEPENDENCY_SELF_NOT_ALLOWED,
                "Template task cannot depend on itself: " + taskId, Map.of("taskId", taskId));
    }

    public static AppException projectTemplateDependencyCycleDetected() {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_DEPENDENCY_CYCLE_DETECTED);
    }

    public static AppException projectTemplateDependencyTaskMismatch() {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_DEPENDENCY_TASK_MISMATCH);
    }

    public static AppException projectTemplateApplyFailed(String detail) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_APPLY_FAILED, detail, Map.of());
    }

    public static AppException projectTemplateApplyWorkspaceInactive(UUID workspaceId) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_APPLY_WORKSPACE_INACTIVE,
                "Workspace is not active: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException projectTemplateApplyPermissionDenied() {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_APPLY_PERMISSION_DENIED);
    }

    public static AppException projectTemplateApplyDuplicateProjectCode(String code) {
        return new AppException(ProjectErrorCatalog.PROJECT_TEMPLATE_APPLY_DUPLICATE_PROJECT_CODE,
                "Project code already exists: " + code, Map.of("code", code));
    }

    public static AppException scheduleRunNotFound(UUID id) {
        if (id == null) {
            return new AppException(ProjectErrorCatalog.SCHEDULE_RUN_NOT_FOUND,
                    "No current schedule run exists for this project");
        }
        return new AppException(ProjectErrorCatalog.SCHEDULE_RUN_NOT_FOUND,
                "Schedule run not found: " + id, Map.of("id", id));
    }

    public static AppException scheduleRunProjectArchived(UUID projectId) {
        return new AppException(ProjectErrorCatalog.SCHEDULE_RUN_PROJECT_ARCHIVED,
                "Archived project cannot be scheduled: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException scheduleRunInvalidDateRange() {
        return new AppException(ProjectErrorCatalog.SCHEDULE_RUN_INVALID_DATE_RANGE);
    }

    public static AppException scheduleRunRangeTooLarge() {
        return new AppException(ProjectErrorCatalog.SCHEDULE_RUN_RANGE_TOO_LARGE);
    }

    public static AppException scheduleRunNotCancellable(UUID id) {
        return new AppException(ProjectErrorCatalog.SCHEDULE_RUN_NOT_CANCELLABLE,
                "Schedule run is not cancellable: " + id, Map.of("id", id));
    }

    public static AppException taskScheduleNotFound(UUID taskId) {
        return new AppException(ProjectErrorCatalog.TASK_SCHEDULE_NOT_FOUND,
                "Task schedule not found: " + taskId, Map.of("taskId", taskId));
    }

    // ── Phase 14 — Gantt ──────────────────────────────────────────────────────

    public static AppException ganttScheduleRunNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.GANTT_SCHEDULE_RUN_NOT_FOUND,
                "Gantt schedule run not found: " + id, Map.of("id", id));
    }

    public static AppException ganttScheduleRunProjectMismatch(UUID scheduleRunId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.GANTT_SCHEDULE_RUN_PROJECT_MISMATCH,
                "Schedule run does not belong to project",
                Map.of("scheduleRunId", scheduleRunId, "projectId", projectId));
    }

    public static AppException projectMilestoneNotFound(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_NOT_FOUND,
                "Project milestone not found: " + id, Map.of("id", id));
    }

    public static AppException projectMilestonePathMismatch(UUID milestoneId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_PATH_MISMATCH,
                "Milestone does not belong to project",
                Map.of("milestoneId", milestoneId, "projectId", projectId));
    }

    public static AppException projectMilestoneInvalidDate() {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_INVALID_DATE);
    }

    public static AppException projectMilestonePhaseMismatch(UUID phaseId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_PHASE_MISMATCH,
                "Milestone phase does not belong to project",
                Map.of("phaseId", phaseId, "projectId", projectId));
    }

    public static AppException projectMilestoneWbsMismatch(UUID wbsNodeId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_WBS_MISMATCH,
                "Milestone WBS node does not belong to project",
                Map.of("wbsNodeId", wbsNodeId, "projectId", projectId));
    }

    public static AppException projectMilestoneArchived(UUID id) {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_ARCHIVED,
                "Milestone is archived: " + id, Map.of("id", id));
    }

    public static AppException projectMilestoneCodeAlreadyExists(String code, UUID projectId) {
        return new AppException(ProjectErrorCatalog.PROJECT_MILESTONE_CODE_ALREADY_EXISTS,
                "Milestone code already exists: " + code,
                Map.of("code", code, "projectId", projectId));
    }

    public static AppException ganttTaskNotFound(UUID taskId) {
        return new AppException(ProjectErrorCatalog.GANTT_TASK_NOT_FOUND,
                "Gantt task not found: " + taskId, Map.of("taskId", taskId));
    }

    public static AppException ganttTaskPathMismatch(UUID taskId, UUID projectId) {
        return new AppException(ProjectErrorCatalog.GANTT_TASK_PATH_MISMATCH,
                "Task does not belong to project",
                Map.of("taskId", taskId, "projectId", projectId));
    }

    public static AppException ganttTaskOverrideNotFound(UUID taskId) {
        return new AppException(ProjectErrorCatalog.GANTT_TASK_OVERRIDE_NOT_FOUND,
                "Active schedule override not found for task: " + taskId, Map.of("taskId", taskId));
    }

    public static AppException ganttTaskOverrideInvalidDateRange() {
        return new AppException(ProjectErrorCatalog.GANTT_TASK_OVERRIDE_INVALID_DATE_RANGE);
    }

    public static AppException ganttTaskOverrideReasonRequired() {
        return new AppException(ProjectErrorCatalog.GANTT_TASK_OVERRIDE_REASON_REQUIRED);
    }

    public static AppException ganttRecalculationFailed(String message) {
        return new AppException(ProjectErrorCatalog.GANTT_RECALCULATION_FAILED, message);
    }

    public static AppException ganttInvalidExportFormat(String format) {
        return new AppException(ProjectErrorCatalog.GANTT_INVALID_EXPORT_FORMAT,
                "Unsupported Gantt export format: " + format, Map.of("format", format));
    }

    public static AppException postBaselineEditBlocked(UUID projectId) {
        return new AppException(ProjectErrorCatalog.POST_BASELINE_EDIT_BLOCKED,
                "Direct edit blocked after current baseline; create a ChangeRequest",
                Map.of("projectId", projectId));
    }
}
