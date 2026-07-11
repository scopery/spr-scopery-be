package com.company.scopery.modules.project.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.project.project.application.action.CreateProjectAction;
import com.company.scopery.modules.project.project.application.command.CreateProjectCommand;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.application.action.CreateProjectPhaseAction;
import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.task.application.action.CreateTaskAction;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.application.action.CreateTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.command.CreateTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.wbs.application.action.ArchiveWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.CreateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.MoveWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.command.ArchiveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.MoveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectBusinessRulesActionTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectPhaseRepository projectPhaseRepository;
    @Mock private WbsNodeRepository wbsNodeRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private TaskDependencyRepository taskDependencyRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private ProjectActivityLogger activityLogger;
    @Mock private ProjectWorkspaceAuthorizationService authorizationService;

    private CreateProjectAction createProjectAction;
    private CreateProjectPhaseAction createProjectPhaseAction;
    private CreateWbsNodeAction createWbsNodeAction;
    private MoveWbsNodeAction moveWbsNodeAction;
    private ArchiveWbsNodeAction archiveWbsNodeAction;
    private CreateTaskAction createTaskAction;
    private CreateTaskDependencyAction createTaskDependencyAction;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID phaseId = UUID.randomUUID();
    private final UUID wbsId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createProjectAction = new CreateProjectAction(projectRepository, activityLogger, authorizationService);
        createProjectPhaseAction = new CreateProjectPhaseAction(projectRepository, projectPhaseRepository, activityLogger, authorizationService);
        createWbsNodeAction = new CreateWbsNodeAction(wbsNodeRepository, projectRepository, projectPhaseRepository, activityLogger, authorizationService);
        moveWbsNodeAction = new MoveWbsNodeAction(wbsNodeRepository, activityLogger, authorizationService);
        archiveWbsNodeAction = new ArchiveWbsNodeAction(wbsNodeRepository, activityLogger, authorizationService);
        createTaskAction = new CreateTaskAction(taskRepository, projectRepository, projectPhaseRepository,
                wbsNodeRepository, workspaceMemberRepository, activityLogger, authorizationService);
        createTaskDependencyAction = new CreateTaskDependencyAction(taskDependencyRepository, taskRepository, activityLogger, authorizationService);
    }

    // ── Project ───────────────────────────────────────────────────────────────

    @Test
    void createProject_duplicateCodeInWorkspace_throwsConflict() {
        when(projectRepository.existsByWorkspaceIdAndCode(workspaceId, "PRJ_01")).thenReturn(true);

        assertThatThrownBy(() -> createProjectAction.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD", null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.PROJECT_CODE_ALREADY_EXISTS.code());
                });
    }

    @Test
    void createProject_invalidDateRange_throwsBadRequest() {
        when(projectRepository.existsByWorkspaceIdAndCode(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> createProjectAction.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD",
                        LocalDate.of(2026, 6, 1), LocalDate.of(2026, 5, 1))))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_INVALID_DATE_RANGE.code()));
    }

    // ── Project Phase ─────────────────────────────────────────────────────────

    @Test
    void createProjectPhase_duplicateCode_throwsConflict() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.existsByProjectIdAndCode(projectId, "PHASE_1")).thenReturn(true);

        assertThatThrownBy(() -> createProjectPhaseAction.execute(
                new CreateProjectPhaseCommand(projectId, "PHASE_1", "Phase 1", null, 1, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_PHASE_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void createProjectPhase_displayOrderConflict_throwsConflict() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.existsByProjectIdAndCode(any(), any())).thenReturn(false);
        when(projectPhaseRepository.existsByProjectIdAndDisplayOrder(projectId, 1)).thenReturn(true);

        assertThatThrownBy(() -> createProjectPhaseAction.execute(
                new CreateProjectPhaseCommand(projectId, "PHASE_1", "Phase 1", null, 1, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_PHASE_DISPLAY_ORDER_CONFLICT.code()));
    }

    // ── WBS ───────────────────────────────────────────────────────────────────

    @Test
    void createWbsNode_parentProjectMismatch_throwsUnprocessable() {
        UUID otherProjectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        WbsNode parent = wbsNode(parentId, otherProjectId, phaseId, null);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        when(wbsNodeRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> createWbsNodeAction.execute(
                new CreateWbsNodeCommand(projectId, phaseId, parentId, "WBS_1", "Title", null, "WORK_PACKAGE", 1)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.WBS_NODE_PROJECT_MISMATCH.code()));
    }

    @Test
    void createWbsNode_parentPhaseMismatch_throwsUnprocessable() {
        UUID otherPhaseId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        WbsNode parent = wbsNode(parentId, projectId, otherPhaseId, null);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        when(wbsNodeRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> createWbsNodeAction.execute(
                new CreateWbsNodeCommand(projectId, phaseId, parentId, "WBS_1", "Title", null, "WORK_PACKAGE", 1)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.WBS_NODE_PHASE_MISMATCH.code()));
    }

    @Test
    void moveWbsNode_circularParent_throwsUnprocessable() {
        UUID childId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        WbsNode child = wbsNode(childId, projectId, phaseId, parentId);
        WbsNode parent = wbsNode(parentId, projectId, phaseId, childId);

        when(wbsNodeRepository.findById(childId)).thenReturn(Optional.of(child));
        when(wbsNodeRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> moveWbsNodeAction.execute(
                new MoveWbsNodeCommand(childId, projectId, parentId, 1)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.WBS_NODE_CIRCULAR_PARENT.code()));
    }

    @Test
    void archiveWbsNode_activeChildrenOrLinkedTasks_throwsUnprocessable() {
        WbsNode node = wbsNode(wbsId, projectId, phaseId, null);
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(node));
        when(wbsNodeRepository.hasActiveChildrenOrLinkedTasks(wbsId)).thenReturn(true);

        assertThatThrownBy(() -> archiveWbsNodeAction.execute(new ArchiveWbsNodeCommand(wbsId, projectId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.WBS_NODE_CANNOT_ARCHIVE.code()));
    }

    @Test
    void archiveWbsNode_linkedTasksOnly_throwsUnprocessable() {
        WbsNode leaf = wbsNode(wbsId, projectId, phaseId, null);
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(leaf));
        when(wbsNodeRepository.hasActiveChildrenOrLinkedTasks(wbsId)).thenReturn(true);

        assertThatThrownBy(() -> archiveWbsNodeAction.execute(new ArchiveWbsNodeCommand(wbsId, projectId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.WBS_NODE_CANNOT_ARCHIVE.code()));
    }

    // ── Task ──────────────────────────────────────────────────────────────────

    @Test
    void createTask_assigneeNotWorkspaceMember_throwsUnprocessable() {
        UUID assigneeId = UUID.randomUUID();
        mockTaskCreateContext();
        when(workspaceMemberRepository.isActiveMember(workspaceId, assigneeId)).thenReturn(false);

        assertThatThrownBy(() -> createTaskAction.execute(baseTaskCommand(assigneeId, BigDecimal.valueOf(8))))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER.code()));
    }

    @Test
    void createTask_assigneeInactiveWorkspaceMember_throwsUnprocessable() {
        UUID assigneeId = UUID.randomUUID();
        mockTaskCreateContext();
        when(workspaceMemberRepository.isActiveMember(workspaceId, assigneeId)).thenReturn(false);

        assertThatThrownBy(() -> createTaskAction.execute(baseTaskCommand(assigneeId, BigDecimal.ONE)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER.code());
                });
    }

    @Test
    void createTask_invalidEstimateHours_throwsBadRequest() {
        mockTaskCreateContext();

        assertThatThrownBy(() -> createTaskAction.execute(baseTaskCommand(null, BigDecimal.ZERO)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_INVALID_ESTIMATE.code()));

        assertThatThrownBy(() -> createTaskAction.execute(baseTaskCommand(null, BigDecimal.valueOf(-1))))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_INVALID_ESTIMATE.code()));
    }

    @Test
    void createTask_phaseProjectMismatch_throwsUnprocessable() {
        UUID otherProjectId = UUID.randomUUID();
        ProjectPhase wrongPhase = activePhase(otherProjectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(wrongPhase));

        assertThatThrownBy(() -> createTaskAction.execute(baseTaskCommand(null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_ENTITY_MISMATCH.code()));
    }

    @Test
    void createTask_wbsProjectMismatch_throwsUnprocessable() {
        UUID otherProjectId = UUID.randomUUID();
        WbsNode wrongWbs = wbsNode(wbsId, otherProjectId, phaseId, null);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(wrongWbs));

        assertThatThrownBy(() -> createTaskAction.execute(baseTaskCommand(null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_ENTITY_MISMATCH.code()));
    }

    // ── Task Dependency ───────────────────────────────────────────────────────

    @Test
    void createTaskDependency_selfReference_throwsUnprocessable() {
        UUID taskId = UUID.randomUUID();

        assertThatThrownBy(() -> createTaskDependencyAction.execute(
                new CreateTaskDependencyCommand(projectId, taskId, taskId, "FINISH_TO_START", 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_DEPENDENCY_SELF_REFERENCE.code()));
    }

    @Test
    void createTaskDependency_circularChain_throwsUnprocessable() {
        UUID taskA = UUID.randomUUID();
        UUID taskB = UUID.randomUUID();
        UUID taskC = UUID.randomUUID();
        UUID taskD = UUID.randomUUID();

        TaskDependency ab = TaskDependency.create(projectId, taskA, taskB, TaskDependencyType.FINISH_TO_START, 0);
        TaskDependency bc = TaskDependency.create(projectId, taskB, taskC, TaskDependencyType.FINISH_TO_START, 0);
        TaskDependency cd = TaskDependency.create(projectId, taskC, taskD, TaskDependencyType.FINISH_TO_START, 0);

        when(taskRepository.findById(taskA)).thenReturn(Optional.of(task(taskA)));
        when(taskRepository.findById(taskD)).thenReturn(Optional.of(task(taskD)));
        when(taskDependencyRepository.existsByPredecessorAndSuccessorAndType(taskA, taskD, TaskDependencyType.FINISH_TO_START))
                .thenReturn(false);
        when(taskDependencyRepository.findActiveDependenciesFrom(taskD)).thenReturn(List.of(cd));
        when(taskDependencyRepository.findActiveDependenciesFrom(taskC)).thenReturn(List.of(bc));
        when(taskDependencyRepository.findActiveDependenciesFrom(taskB)).thenReturn(List.of(ab));

        assertThatThrownBy(() -> createTaskDependencyAction.execute(
                new CreateTaskDependencyCommand(projectId, taskA, taskD, "FINISH_TO_START", 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_DEPENDENCY_CIRCULAR.code()));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Project activeProject() {
        Instant now = Instant.now();
        return new Project(projectId, workspaceId, "PRJ_01", "Project", null, UUID.randomUUID(), "USD",
                null, null, ProjectStatus.ACTIVE, 0, now, now);
    }

    private ProjectPhase activePhase() {
        return activePhase(projectId);
    }

    private ProjectPhase activePhase(UUID ownerProjectId) {
        Instant now = Instant.now();
        return new ProjectPhase(phaseId, ownerProjectId, null, "PHASE_1", "Phase 1", null, 1,
                null, null, ProjectPhaseStatus.ACTIVE, 0, now, now);
    }

    private WbsNode wbsNode(UUID id, UUID ownerProjectId, UUID ownerPhaseId, UUID parentId) {
        Instant now = Instant.now();
        return new WbsNode(id, ownerProjectId, ownerPhaseId, parentId, "WBS_1", "Title", null,
                WbsNodeType.WORK_PACKAGE, 1, "WBS_1", 1, WbsNodeStatus.ACTIVE, 0, now, now);
    }

    private Task task(UUID id) {
        Instant now = Instant.now();
        return new Task(id, projectId, phaseId, wbsId, "TASK_1", "Task", null, null, null, null,
                BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO, 0, now, now);
    }

    private void mockTaskCreateContext() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(wbsNode(wbsId, projectId, phaseId, null)));
        when(taskRepository.existsByProjectIdAndCode(any(), any())).thenReturn(false);
    }

    private CreateTaskCommand baseTaskCommand(UUID assigneeId, BigDecimal estimateHours) {
        return new CreateTaskCommand(projectId, phaseId, wbsId, "TASK_1", "Task", null,
                assigneeId, null, null, estimateHours, null, null, null);
    }
}
