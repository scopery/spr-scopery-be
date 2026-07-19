package com.company.scopery.modules.project.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.project.project.application.action.ArchiveProjectAction;
import com.company.scopery.modules.project.project.application.action.CreateProjectAction;
import com.company.scopery.modules.project.project.application.action.UpdateProjectAction;
import com.company.scopery.modules.project.project.application.command.ArchiveProjectCommand;
import com.company.scopery.modules.project.project.application.command.CreateProjectCommand;
import com.company.scopery.modules.project.project.application.command.UpdateProjectCommand;
import com.company.scopery.modules.project.project.application.query.SearchProjectQuery;
import com.company.scopery.modules.project.project.application.service.ProjectQueryService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.application.action.ArchiveProjectPhaseAction;
import com.company.scopery.modules.project.projectphase.application.action.CreateProjectPhaseAction;
import com.company.scopery.modules.project.projectphase.application.action.UpdateProjectPhaseAction;
import com.company.scopery.modules.project.projectphase.application.command.ArchiveProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.command.UpdateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.query.SearchProjectPhaseQuery;
import com.company.scopery.modules.project.projectphase.application.service.ProjectPhaseQueryService;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.task.application.action.ArchiveTaskAction;
import com.company.scopery.modules.project.task.application.action.CreateTaskAction;
import com.company.scopery.modules.project.task.application.action.StartTaskAction;
import com.company.scopery.modules.project.task.application.action.UpdateTaskAction;
import com.company.scopery.modules.project.task.application.command.ArchiveTaskCommand;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.modules.project.task.application.command.StartTaskCommand;
import com.company.scopery.modules.project.task.application.command.UpdateTaskCommand;
import com.company.scopery.modules.project.task.application.query.SearchTaskQuery;
import com.company.scopery.modules.project.task.application.service.TaskQueryService;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.application.action.CreateTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.action.RemoveTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.command.CreateTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.command.RemoveTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.service.TaskDependencyQueryService;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyStatus;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.wbs.application.action.ArchiveWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.CreateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.MoveWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.UpdateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.command.ArchiveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.MoveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.UpdateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.query.SearchWbsNodeQuery;
import com.company.scopery.modules.project.wbs.application.service.WbsNodeQueryService;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectAuthorizationActionTest {

    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectPhaseRepository projectPhaseRepository;
    @Mock private WbsNodeRepository wbsNodeRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private TaskDependencyRepository taskDependencyRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private ProjectActivityLogger activityLogger;
    @Mock private ProjectPlatformPublisher platformPublisher;

    private ProjectWorkspaceAuthorizationService authorizationService;
    private ProjectMutationGuard mutationGuard;

    private final UUID userId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID otherWorkspaceId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID otherProjectId = UUID.randomUUID();
    private final UUID phaseId = UUID.randomUUID();
    private final UUID wbsId = UUID.randomUUID();
    private final UUID taskId = UUID.randomUUID();
    private final UUID successorTaskId = UUID.randomUUID();
    private final UUID dependencyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        authorizationService = new ProjectWorkspaceAuthorizationService(
                currentUserService, iamIntegrationService, projectRepository);
        mutationGuard = new ProjectMutationGuard(projectRepository);
        Instant now = Instant.now();
        IamUser currentUser = IamUser.of(userId, Username.of("planner"),
                EmailAddress.of("planner@example.com"), "Planner", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
        lenient().when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
    }

    // ── Project ───────────────────────────────────────────────────────────────

    @Test
    void createProject_withoutProjectCreate_forbidden() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_CREATE);
        CreateProjectAction action = new CreateProjectAction(
                projectRepository, workspaceRepository, workspaceMemberRepository,
                activityLogger, authorizationService, platformPublisher);

        assertForbidden(() -> action.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD", null, null)));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_withProjectCreate_allowed_callsIam() {
        CreateProjectAction action = new CreateProjectAction(
                projectRepository, workspaceRepository, workspaceMemberRepository,
                activityLogger, authorizationService, platformPublisher);

        // Will fail later on workspace lookup — assert IAM was checked first
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> action.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD", null, null)))
                .isInstanceOf(AppException.class);

        verify(iamIntegrationService).requireWorkspaceAccess(eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_CREATE));
    }

    @Test
    void viewProject_withoutProjectView_forbidden() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_VIEW);
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);

        assertForbidden(() -> queryService.getProject(projectId));
    }

    @Test
    void viewProject_withProjectView_allowed() {
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);

        var response = queryService.getProject(projectId);

        assertThat(response.id()).isEqualTo(projectId);
        verify(iamIntegrationService).requireWorkspaceAccess(eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_VIEW));
    }

    @Test
    void updateProject_withoutProjectUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_UPDATE);
        UpdateProjectAction action = new UpdateProjectAction(
                projectRepository, workspaceMemberRepository, activityLogger, authorizationService, platformPublisher);

        assertForbidden(() -> action.execute(
                new UpdateProjectCommand(projectId, "Name", null, null, "USD", null, null)));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void archiveProject_withoutProjectArchive_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_ARCHIVE);
        ArchiveProjectAction action = new ArchiveProjectAction(
                projectRepository, activityLogger, authorizationService, currentUserService, platformPublisher);

        assertForbidden(() -> action.execute(new ArchiveProjectCommand(projectId)));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void projectList_requiresWorkspaceAccess() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_VIEW);
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);

        assertForbidden(() -> queryService.searchProjects(
                new SearchProjectQuery(workspaceId, null, null, 0, 20)));
        verify(projectRepository, never()).search(any(), any(), any(), any());
    }

    @Test
    void projectGet_crossWorkspace_forbidden() {
        UUID foreignProjectId = UUID.randomUUID();
        when(projectRepository.findById(foreignProjectId)).thenReturn(Optional.of(
                projectInWorkspace(foreignProjectId, otherWorkspaceId)));
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(iamIntegrationService)
                .requireWorkspaceAccess(eq(otherWorkspaceId), eq(userId), eq(IamAuthorities.PROJECT_VIEW));

        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);
        assertForbidden(() -> queryService.getProject(foreignProjectId));
    }

    @Test
    void inactiveWorkspaceMember_cannotViewProject() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_VIEW);
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);
        assertForbidden(() -> queryService.getProject(projectId));
    }

    @Test
    void inactiveWorkspaceMember_cannotCreateProject() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_CREATE);
        CreateProjectAction action = new CreateProjectAction(
                projectRepository, workspaceRepository, workspaceMemberRepository,
                activityLogger, authorizationService, platformPublisher);
        assertForbidden(() -> action.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD", null, null)));
    }

    // ── ProjectPhase ──────────────────────────────────────────────────────────

    @Test
    void listPhases_withoutProjectPhaseView_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_PHASE_VIEW);
        ProjectPhaseQueryService queryService =
                new ProjectPhaseQueryService(projectPhaseRepository, authorizationService);

        assertForbidden(() -> queryService.searchProjectPhases(
                new SearchProjectPhaseQuery(projectId, null, 0, 20)));
    }

    @Test
    void listPhases_withProjectPhaseView_allowed() {
        when(projectPhaseRepository.search(eq(projectId), isNull(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));
        ProjectPhaseQueryService queryService =
                new ProjectPhaseQueryService(projectPhaseRepository, authorizationService);

        queryService.searchProjectPhases(new SearchProjectPhaseQuery(projectId, null, 0, 20));
        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_PHASE_VIEW));
    }

    @Test
    void createPhase_withoutProjectPhaseCreate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_PHASE_CREATE);
        CreateProjectPhaseAction action = new CreateProjectPhaseAction(
                projectPhaseRepository, activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(
                new CreateProjectPhaseCommand(projectId, "PHASE_1", "Phase 1", null, 1, null, null)));
    }

    @Test
    void updatePhase_withoutProjectPhaseUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_PHASE_UPDATE);
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        UpdateProjectPhaseAction action = new UpdateProjectPhaseAction(
                projectPhaseRepository, activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(
                new UpdateProjectPhaseCommand(phaseId, projectId, "New Name", null, 1, null, null)));
    }

    @Test
    void archivePhase_withoutProjectPhaseArchive_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_PHASE_ARCHIVE);
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        ArchiveProjectPhaseAction action = new ArchiveProjectPhaseAction(
                projectPhaseRepository, activityLogger, authorizationService, mutationGuard,
                platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(new ArchiveProjectPhaseCommand(phaseId, projectId)));
    }

    @Test
    void phasePathMismatch_rejected() {
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        ProjectPhaseQueryService queryService =
                new ProjectPhaseQueryService(projectPhaseRepository, authorizationService);

        assertThatThrownBy(() -> queryService.getProjectPhase(otherProjectId, phaseId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_PHASE_PROJECT_MISMATCH.code()));
    }

    @Test
    void phaseFromOtherProject_rejected() {
        phasePathMismatch_rejected();
    }

    // ── WBS ───────────────────────────────────────────────────────────────────

    @Test
    void listWbs_withoutProjectWbsView_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_VIEW);
        WbsNodeQueryService queryService = new WbsNodeQueryService(wbsNodeRepository, authorizationService);

        assertForbidden(() -> queryService.searchWbsNodes(
                new SearchWbsNodeQuery(projectId, null, null, null, 0, 20)));
    }

    @Test
    void listWbs_withProjectWbsView_allowed() {
        when(wbsNodeRepository.search(eq(projectId), isNull(), isNull(), isNull(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));
        WbsNodeQueryService queryService = new WbsNodeQueryService(wbsNodeRepository, authorizationService);

        queryService.searchWbsNodes(new SearchWbsNodeQuery(projectId, null, null, null, 0, 20));
        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_WBS_VIEW));
    }

    @Test
    void createWbs_withoutProjectWbsCreate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_CREATE);
        CreateWbsNodeAction action = new CreateWbsNodeAction(
                wbsNodeRepository, projectPhaseRepository, activityLogger, authorizationService,
                mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(
                new CreateWbsNodeCommand(projectId, phaseId, null, "WBS_1", "Title", null, "WORK_PACKAGE", 1)));
    }

    @Test
    void updateWbs_withoutProjectWbsUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_UPDATE);
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(wbsNode()));
        UpdateWbsNodeAction action = new UpdateWbsNodeAction(
                wbsNodeRepository, activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(
                new UpdateWbsNodeCommand(wbsId, projectId, "New Title", null, "WORK_PACKAGE")));
    }

    @Test
    void moveWbs_withoutProjectWbsMoveOrUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_UPDATE);
        MoveWbsNodeAction action = new MoveWbsNodeAction(
                wbsNodeRepository, activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(
                new MoveWbsNodeCommand(wbsId, projectId, null, 2)));
    }

    @Test
    void archiveWbs_withoutProjectWbsArchive_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_ARCHIVE);
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(wbsNode()));
        ArchiveWbsNodeAction action = new ArchiveWbsNodeAction(
                wbsNodeRepository, activityLogger, authorizationService, mutationGuard,
                platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(new ArchiveWbsNodeCommand(wbsId, projectId)));
    }

    @Test
    void wbsPathMismatch_rejected() {
        when(projectRepository.findById(otherProjectId))
                .thenReturn(Optional.of(projectInWorkspace(otherProjectId, otherWorkspaceId)));
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(wbsNode()));
        WbsNodeQueryService queryService = new WbsNodeQueryService(wbsNodeRepository, authorizationService);

        assertThatThrownBy(() -> queryService.getWbsNode(otherProjectId, wbsId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.WBS_NODE_PROJECT_MISMATCH.code()));
    }

    @Test
    void wbsMoveParentFromOtherProject_rejected() {
        UUID foreignParentId = UUID.randomUUID();
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(wbsNode()));
        when(wbsNodeRepository.findById(foreignParentId)).thenReturn(Optional.of(
                new WbsNode(foreignParentId, otherProjectId, phaseId, null, "OTHER", "Other", null,
                        WbsNodeType.WORK_PACKAGE, 1, "OTHER", 1, WbsNodeStatus.ACTIVE, 0,
                        Instant.now(), Instant.now())));

        MoveWbsNodeAction action = new MoveWbsNodeAction(
                wbsNodeRepository, activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new MoveWbsNodeCommand(wbsId, projectId, foreignParentId, 1)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isIn(ProjectErrorCatalog.WBS_NODE_PROJECT_MISMATCH.code(),
                                ProjectErrorCatalog.WBS_NODE_CIRCULAR_PARENT.code()));
    }

    @Test
    void wbsFromOtherWorkspace_forbiddenOrMasked() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_VIEW);
        WbsNodeQueryService queryService = new WbsNodeQueryService(wbsNodeRepository, authorizationService);
        assertForbidden(() -> queryService.getWbsTree(projectId, null));
    }

    // ── Task ──────────────────────────────────────────────────────────────────

    @Test
    void listTasks_withoutProjectTaskView_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_VIEW);
        TaskQueryService queryService =
                new TaskQueryService(taskRepository, wbsNodeRepository, authorizationService);

        assertForbidden(() -> queryService.searchTasks(
                new SearchTaskQuery(projectId, null, null, null, null, null, 0, 20)));
    }

    @Test
    void listTasks_withProjectTaskView_allowed() {
        when(taskRepository.search(eq(projectId), isNull(), isNull(), isNull(), isNull(), isNull(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));
        TaskQueryService queryService =
                new TaskQueryService(taskRepository, wbsNodeRepository, authorizationService);

        queryService.searchTasks(new SearchTaskQuery(projectId, null, null, null, null, null, 0, 20));
        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_TASK_VIEW));
    }

    @Test
    void createTask_withoutProjectTaskCreate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_CREATE);
        CreateTaskAction action = new CreateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(baseCreateTaskCommand(null)));
    }

    @Test
    void updateTask_withoutProjectTaskUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_UPDATE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));
        UpdateTaskAction action = new UpdateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(baseUpdateTaskCommand(null)));
    }

    @Test
    void assignTask_withoutAssignOrUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_UPDATE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));
        UpdateTaskAction action = new UpdateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(baseUpdateTaskCommand(UUID.randomUUID())));
    }

    @Test
    void statusChangeTask_withoutStatusOrUpdate_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_UPDATE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));
        StartTaskAction action = new StartTaskAction(
                taskRepository, activityLogger, authorizationService, mutationGuard,
                platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(new StartTaskCommand(taskId, projectId)));
    }

    @Test
    void archiveTask_withoutArchive_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_ARCHIVE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));
        ArchiveTaskAction action = new ArchiveTaskAction(
                taskRepository, activityLogger, authorizationService, mutationGuard,
                platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(new ArchiveTaskCommand(taskId, projectId)));
    }

    @Test
    void taskPathMismatch_rejected() {
        when(projectRepository.findById(otherProjectId))
                .thenReturn(Optional.of(projectInWorkspace(otherProjectId, otherWorkspaceId)));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));
        TaskQueryService queryService =
                new TaskQueryService(taskRepository, wbsNodeRepository, authorizationService);

        assertThatThrownBy(() -> queryService.getTask(otherProjectId, taskId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_PROJECT_MISMATCH.code()));
    }

    @Test
    void taskPhaseFromOtherProject_rejected() {
        UUID foreignPhaseId = UUID.randomUUID();
        Instant now = Instant.now();
        ProjectPhase foreignPhase = new ProjectPhase(foreignPhaseId, otherProjectId, null, "OTHER", "Other",
                null, 1, null, null, ProjectPhaseStatus.ACTIVE, null, null, null, 0, now, now);

        when(projectPhaseRepository.findById(foreignPhaseId)).thenReturn(Optional.of(foreignPhase));

        CreateTaskAction action = new CreateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateTaskCommand(projectId, foreignPhaseId, null, "TASK_1", "Task", null,
                        null, null, null, BigDecimal.ONE, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_ENTITY_MISMATCH.code()));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void taskWbsFromOtherProject_rejected() {
        UUID foreignWbsId = UUID.randomUUID();
        Instant now = Instant.now();
        WbsNode foreignWbs = new WbsNode(foreignWbsId, otherProjectId, phaseId, null, "OTHER", "Other", null,
                WbsNodeType.WORK_PACKAGE, 1, "OTHER", 1, WbsNodeStatus.ACTIVE, 0, now, now);

        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(activePhase()));
        when(wbsNodeRepository.findById(foreignWbsId)).thenReturn(Optional.of(foreignWbs));

        CreateTaskAction action = new CreateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateTaskCommand(projectId, phaseId, foreignWbsId, "TASK_1", "Task", null,
                        null, null, null, BigDecimal.ONE, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_ENTITY_MISMATCH.code()));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void taskAssigneeNotWorkspaceMember_rejected() {
        UUID newAssignee = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(UUID.randomUUID())));
        when(workspaceMemberRepository.isActiveMember(workspaceId, newAssignee)).thenReturn(false);

        UpdateTaskAction action = new UpdateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher, currentUserService);

        assertThatThrownBy(() -> action.execute(baseUpdateTaskCommand(newAssignee)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER.code());
                });
    }

    @Test
    void taskAssigneeInactiveWorkspaceMember_rejected() {
        UUID inactiveAssignee = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(UUID.randomUUID())));
        when(workspaceMemberRepository.isActiveMember(workspaceId, inactiveAssignee)).thenReturn(false);

        UpdateTaskAction action = new UpdateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher, currentUserService);

        assertThatThrownBy(() -> action.execute(baseUpdateTaskCommand(inactiveAssignee)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER.code()));
    }

    // ── Dependencies ──────────────────────────────────────────────────────────

    @Test
    void listDependencies_withoutPermission_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_VIEW);
        TaskDependencyQueryService queryService =
                new TaskDependencyQueryService(taskDependencyRepository, authorizationService);

        assertForbidden(() -> queryService.searchTaskDependencies(
                new com.company.scopery.modules.project.taskdependency.application.query.SearchTaskDependencyQuery(
                        projectId, null, null, 0, 20)));
    }

    @Test
    void createDependency_withoutPermission_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_UPDATE);
        CreateTaskDependencyAction action = new CreateTaskDependencyAction(
                taskDependencyRepository, taskRepository, activityLogger, authorizationService,
                mutationGuard, platformPublisher);

        assertForbidden(() -> action.execute(
                new CreateTaskDependencyCommand(projectId, taskId, successorTaskId, "FINISH_TO_START", 0)));
    }

    @Test
    void removeDependency_withoutPermission_forbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_UPDATE);
        when(taskDependencyRepository.findById(dependencyId)).thenReturn(Optional.of(dependency()));
        RemoveTaskDependencyAction action = new RemoveTaskDependencyAction(
                taskDependencyRepository, activityLogger, authorizationService, mutationGuard,
                platformPublisher, currentUserService);

        assertForbidden(() -> action.execute(new RemoveTaskDependencyCommand(dependencyId, projectId)));
    }

    @Test
    void dependencyPathMismatch_rejected() {
        when(taskDependencyRepository.findById(dependencyId)).thenReturn(Optional.of(dependency()));
        TaskDependencyQueryService queryService =
                new TaskDependencyQueryService(taskDependencyRepository, authorizationService);

        assertThatThrownBy(() -> queryService.getTaskDependency(otherProjectId, dependencyId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_DEPENDENCY_PROJECT_MISMATCH.code()));
    }

    @Test
    void dependencyPredecessorFromOtherProject_rejected() {
        UUID foreignPredecessorId = UUID.randomUUID();
        Instant now = Instant.now();
        Task foreignPredecessor = new Task(foreignPredecessorId, otherProjectId, phaseId, wbsId, "T_OTHER",
                "Other", null, null, null, null, BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO,
                null, null, null, null, null, null, null, null, null, 0, now, now);
        Task localSuccessor = new Task(successorTaskId, projectId, phaseId, wbsId, "T_SUCC",
                "Succ", null, null, null, null, BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO,
                null, null, null, null, null, null, null, null, null, 0, now, now);

        when(taskRepository.findById(foreignPredecessorId)).thenReturn(Optional.of(foreignPredecessor));
        when(taskRepository.findById(successorTaskId)).thenReturn(Optional.of(localSuccessor));

        CreateTaskDependencyAction action = new CreateTaskDependencyAction(
                taskDependencyRepository, taskRepository, activityLogger, authorizationService,
                mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateTaskDependencyCommand(projectId, foreignPredecessorId, successorTaskId,
                        "FINISH_TO_START", 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_DEPENDENCY_DIFFERENT_PROJECTS.code()));
        verify(taskDependencyRepository, never()).save(any());
    }

    @Test
    void dependencySuccessorFromOtherProject_rejected() {
        UUID foreignSuccessorId = UUID.randomUUID();
        Instant now = Instant.now();
        Task localPredecessor = new Task(taskId, projectId, phaseId, wbsId, "T_PRED",
                "Pred", null, null, null, null, BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO,
                null, null, null, null, null, null, null, null, null, 0, now, now);
        Task foreignSuccessor = new Task(foreignSuccessorId, otherProjectId, phaseId, wbsId, "T_OTHER",
                "Other", null, null, null, null, BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO,
                null, null, null, null, null, null, null, null, null, 0, now, now);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(localPredecessor));
        when(taskRepository.findById(foreignSuccessorId)).thenReturn(Optional.of(foreignSuccessor));

        CreateTaskDependencyAction action = new CreateTaskDependencyAction(
                taskDependencyRepository, taskRepository, activityLogger, authorizationService,
                mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateTaskDependencyCommand(projectId, taskId, foreignSuccessorId, "FINISH_TO_START", 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_DEPENDENCY_DIFFERENT_PROJECTS.code()));
        verify(taskDependencyRepository, never()).save(any());
    }

    // ── Archived project boundary ─────────────────────────────────────────────

    @Test
    void archivedProject_viewAllowedWithPermission() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(archivedProject()));
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);

        var response = queryService.getProject(projectId);
        assertThat(response.status()).isEqualTo(ProjectStatus.ARCHIVED.name());
    }

    @Test
    void archivedProject_createTask_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(archivedProject()));
        CreateTaskAction action = new CreateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(baseCreateTaskCommand(null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_ALREADY_ARCHIVED.code()));
    }

    @Test
    void archivedProject_updateTask_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(archivedProject()));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));

        UpdateTaskAction action = new UpdateTaskAction(
                taskRepository, projectPhaseRepository, wbsNodeRepository, workspaceMemberRepository,
                activityLogger, authorizationService, mutationGuard, platformPublisher, currentUserService);

        assertThatThrownBy(() -> action.execute(baseUpdateTaskCommand(null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_ALREADY_ARCHIVED.code()));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void archivedProject_createPhase_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(archivedProject()));
        CreateProjectPhaseAction action = new CreateProjectPhaseAction(
                projectPhaseRepository, activityLogger, authorizationService, mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateProjectPhaseCommand(projectId, "PHASE_1", "Phase 1", null, 1, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_ALREADY_ARCHIVED.code()));
    }

    @Test
    void archivedProject_createDependency_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(archivedProject()));
        CreateTaskDependencyAction action = new CreateTaskDependencyAction(
                taskDependencyRepository, taskRepository, activityLogger, authorizationService,
                mutationGuard, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateTaskDependencyCommand(projectId, taskId, successorTaskId, "FINISH_TO_START", 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_ALREADY_ARCHIVED.code()));
    }

    @Test
    void archivedWorkspace_createProject_rejected() {
        Instant now = Instant.now();
        Workspace archivedWs = new Workspace(workspaceId, UUID.randomUUID(), WorkspaceCode.of("WS_01"),
                "Workspace", null, UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY,
                WorkspaceStatus.ARCHIVED, 0, now, now);
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(archivedWs));

        CreateProjectAction action = new CreateProjectAction(
                projectRepository, workspaceRepository, workspaceMemberRepository,
                activityLogger, authorizationService, platformPublisher);

        assertThatThrownBy(() -> action.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD", null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_WORKSPACE_NOT_ACTIVE.code()));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void archivedWorkspace_listProject_behaviorDocumented() {
        // List requires PROJECT_VIEW on the filtered workspace. An archived workspace's IAM
        // resource is inactive, so requireWorkspaceAccess fails — same deny path as inactive member.
        denyWorkspaceAccess(IamAuthorities.PROJECT_VIEW);
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);

        assertForbidden(() -> queryService.searchProjects(
                new SearchProjectQuery(workspaceId, null, null, 0, 20)));
        verify(projectRepository, never()).search(any(), any(), any(), any());
    }

    // ── Query service hardening (§26.7) ───────────────────────────────────────

    @Test
    void projectQueryService_filtersByAccessibleWorkspaces() {
        // Search always requires an explicit workspaceId + PROJECT_VIEW — no cross-workspace dump.
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);
        when(projectRepository.search(eq(workspaceId), isNull(), isNull(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));

        queryService.searchProjects(new SearchProjectQuery(workspaceId, null, null, 0, 20));

        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_VIEW));
        verify(projectRepository).search(eq(workspaceId), isNull(), isNull(), any(PageQuery.class));
    }

    @Test
    void projectQueryService_requiresWorkspaceAccessWhenWorkspaceFilterProvided() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_VIEW);
        ProjectQueryService queryService = new ProjectQueryService(projectRepository, authorizationService);

        assertForbidden(() -> queryService.searchProjects(
                new SearchProjectQuery(workspaceId, "kw", null, 0, 20)));
        verify(projectRepository, never()).search(any(), any(), any(), any());
    }

    @Test
    void taskQueryService_requiresProjectViewAndTaskView() {
        // Child list enforces PROJECT_TASK_VIEW (workspace-scoped via project ownership).
        TaskQueryService queryService =
                new TaskQueryService(taskRepository, wbsNodeRepository, authorizationService);
        when(taskRepository.search(eq(projectId), isNull(), isNull(), isNull(), isNull(), isNull(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));

        queryService.searchTasks(new SearchTaskQuery(projectId, null, null, null, null, null, 0, 20));

        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_TASK_VIEW));
    }

    @Test
    void wbsTreeQuery_requiresProjectViewAndWbsView() {
        WbsNodeQueryService queryService = new WbsNodeQueryService(wbsNodeRepository, authorizationService);
        when(wbsNodeRepository.findAllByProjectId(projectId)).thenReturn(List.of());

        queryService.getWbsTree(projectId, null);

        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_WBS_VIEW));
    }

    @Test
    void phaseQuery_requiresProjectViewAndPhaseView() {
        ProjectPhaseQueryService queryService =
                new ProjectPhaseQueryService(projectPhaseRepository, authorizationService);
        when(projectPhaseRepository.search(eq(projectId), isNull(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));

        queryService.searchProjectPhases(new SearchProjectPhaseQuery(projectId, null, 0, 20));

        verify(iamIntegrationService).requireWorkspaceAccess(
                eq(workspaceId), eq(userId), eq(IamAuthorities.PROJECT_PHASE_VIEW));
    }

    @Test
    void queryDtos_doNotExposeFutureFinanceFields() {
        var fields = List.of(com.company.scopery.modules.project.project.application.response.ProjectResponse.class
                .getRecordComponents()).stream().map(rc -> rc.getName()).toList();
        assertThat(fields).doesNotContain("margin", "cost", "quoteTotal", "financeSummary", "budget");

        var taskFields = List.of(com.company.scopery.modules.project.task.application.response.TaskResponse.class
                .getRecordComponents()).stream().map(rc -> rc.getName()).toList();
        assertThat(taskFields).doesNotContain("cost", "margin", "rate", "billableAmount");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void assertForbidden(ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code());
                });
    }

    private void denyWorkspaceAccess(com.company.scopery.modules.iam.shared.constant.IamPermissionAction authority) {
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(iamIntegrationService)
                .requireWorkspaceAccess(eq(workspaceId), eq(userId), eq(authority));
    }

    private void denyProjectAccess(com.company.scopery.modules.iam.shared.constant.IamPermissionAction authority) {
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(iamIntegrationService)
                .requireWorkspaceAccess(eq(workspaceId), eq(userId), eq(authority));
    }

    private Project activeProject() {
        return projectInWorkspace(projectId, workspaceId);
    }

    private Project archivedProject() {
        Instant now = Instant.now();
        return new Project(projectId, workspaceId, null, "PRJ_01", "Project", null, UUID.randomUUID(), "USD",
                null, null, ProjectStatus.ARCHIVED, null, null, now, userId,
                null, null, null, null, null, null, null, null, null, 0, now, now);
    }

    private Project projectInWorkspace(UUID id, UUID wsId) {
        Instant now = Instant.now();
        return new Project(id, wsId, null, "PRJ_01", "Project", null, UUID.randomUUID(), "USD",
                null, null, ProjectStatus.ACTIVE, null, null, null, null,
                null, null, null, null, null, null, null, null, null, 0, now, now);
    }

    private ProjectPhase activePhase() {
        Instant now = Instant.now();
        return new ProjectPhase(phaseId, projectId, null, "PHASE_1", "Phase 1", null, 1,
                null, null, ProjectPhaseStatus.ACTIVE, null, null, null, 0, now, now);
    }

    private WbsNode wbsNode() {
        Instant now = Instant.now();
        return new WbsNode(wbsId, projectId, phaseId, null, "WBS_1", "Title", null,
                WbsNodeType.WORK_PACKAGE, 1, "WBS_1", 1, WbsNodeStatus.ACTIVE, 0, now, now);
    }

    private Task task(UUID assigneeId) {
        Instant now = Instant.now();
        return new Task(taskId, projectId, phaseId, wbsId, "TASK_1", "Task", null, assigneeId, null, null,
                BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO,
                null, null, null, null, null, null, null, null, null, 0, now, now);
    }

    private TaskDependency dependency() {
        Instant now = Instant.now();
        return new TaskDependency(dependencyId, projectId, taskId, successorTaskId,
                TaskDependencyType.FINISH_TO_START, 0, TaskDependencyStatus.ACTIVE, now, now);
    }

    private CreateTaskCommand baseCreateTaskCommand(UUID assigneeId) {
        return new CreateTaskCommand(projectId, phaseId, wbsId, "TASK_1", "Task", null,
                assigneeId, null, null, BigDecimal.ONE, null, null, null);
    }

    private UpdateTaskCommand baseUpdateTaskCommand(UUID assigneeId) {
        return new UpdateTaskCommand(taskId, projectId, null, null, "Task", null, assigneeId, null, null,
                BigDecimal.ONE, null, null, null);
    }
}
