package com.company.scopery.modules.project.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.project.project.application.action.CreateProjectAction;
import com.company.scopery.modules.project.project.application.command.CreateProjectCommand;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.application.action.CreateProjectPhaseAction;
import com.company.scopery.modules.project.projectphase.application.action.UpdateProjectPhaseAction;
import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.command.UpdateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.task.application.action.CreateTaskAction;
import com.company.scopery.modules.project.task.application.action.UpdateTaskAction;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.modules.project.task.application.command.UpdateTaskCommand;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.application.action.CreateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.UpdateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.UpdateWbsNodeCommand;
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
import java.util.Optional;
import java.util.UUID;

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
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private ProjectActivityLogger activityLogger;

    private ProjectWorkspaceAuthorizationService authorizationService;

    private final UUID userId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID phaseId = UUID.randomUUID();
    private final UUID wbsId = UUID.randomUUID();
    private final UUID taskId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        authorizationService = new ProjectWorkspaceAuthorizationService(
                currentUserService, iamIntegrationService, projectRepository);
        Instant now = Instant.now();
        IamUser currentUser = new IamUser(userId, Username.of("planner"),
                EmailAddress.of("planner@example.com"), "Planner", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
        lenient().when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
    }

    @Test
    void createProject_unauthorizedUser_throwsForbidden() {
        denyWorkspaceAccess(IamAuthorities.PROJECT_CREATE);
        CreateProjectAction action = new CreateProjectAction(projectRepository, activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(
                new CreateProjectCommand(workspaceId, "PRJ_01", "Project", null, null, "USD", null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code());
                });

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProjectPhase_unauthorizedUser_throwsForbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_PHASE_CREATE);
        CreateProjectPhaseAction action = new CreateProjectPhaseAction(
                projectRepository, projectPhaseRepository, activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(
                new CreateProjectPhaseCommand(projectId, "PHASE_1", "Phase 1", null, 1, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code()));

        verify(projectPhaseRepository, never()).save(any());
    }

    @Test
    void updateProjectPhase_unauthorizedUser_throwsForbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_PHASE_UPDATE);
        ProjectPhase phase = activePhase();
        when(projectPhaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        UpdateProjectPhaseAction action = new UpdateProjectPhaseAction(
                projectPhaseRepository, activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(
                new UpdateProjectPhaseCommand(phaseId, projectId, "New Name", null, 1, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code()));

        verify(projectPhaseRepository, never()).save(any());
    }

    @Test
    void createWbsNode_unauthorizedUser_throwsForbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_CREATE);

        CreateWbsNodeAction action = new CreateWbsNodeAction(
                wbsNodeRepository, projectRepository, projectPhaseRepository, activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(
                new CreateWbsNodeCommand(projectId, phaseId, null, "WBS_1", "Title", null, "WORK_PACKAGE", 1)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code()));

        verify(wbsNodeRepository, never()).save(any());
    }

    @Test
    void updateWbsNode_unauthorizedUser_throwsForbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_WBS_UPDATE);
        WbsNode node = wbsNode();
        when(wbsNodeRepository.findById(wbsId)).thenReturn(Optional.of(node));

        UpdateWbsNodeAction action = new UpdateWbsNodeAction(wbsNodeRepository, activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(
                new UpdateWbsNodeCommand(wbsId, projectId, "New Title", null, "WORK_PACKAGE")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code()));

        verify(wbsNodeRepository, never()).save(any());
    }

    @Test
    void createTask_unauthorizedUser_throwsForbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_CREATE);

        CreateTaskAction action = new CreateTaskAction(taskRepository, projectRepository, projectPhaseRepository,
                wbsNodeRepository, workspaceMemberRepository, activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(baseCreateTaskCommand(null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code()));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_unauthorizedUser_throwsForbidden() {
        denyProjectAccess(IamAuthorities.PROJECT_TASK_UPDATE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(null)));

        UpdateTaskAction action = new UpdateTaskAction(taskRepository, projectRepository, workspaceMemberRepository,
                activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(baseUpdateTaskCommand(null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code()));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_nonWorkspaceMemberAssignee_throwsUnprocessable() {
        UUID newAssignee = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(UUID.randomUUID())));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(workspaceMemberRepository.isActiveMember(workspaceId, newAssignee)).thenReturn(false);

        UpdateTaskAction action = new UpdateTaskAction(taskRepository, projectRepository, workspaceMemberRepository,
                activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(baseUpdateTaskCommand(newAssignee)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER.code());
                });

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_inactiveWorkspaceMemberAssignee_throwsUnprocessable() {
        UUID existingAssignee = UUID.randomUUID();
        UUID inactiveAssignee = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(existingAssignee)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(workspaceMemberRepository.isActiveMember(workspaceId, inactiveAssignee)).thenReturn(false);

        UpdateTaskAction action = new UpdateTaskAction(taskRepository, projectRepository, workspaceMemberRepository,
                activityLogger, authorizationService);

        assertThatThrownBy(() -> action.execute(baseUpdateTaskCommand(inactiveAssignee)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER.code());
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
        Instant now = Instant.now();
        return new Project(projectId, workspaceId, "PRJ_01", "Project", null, UUID.randomUUID(), "USD",
                null, null, ProjectStatus.ACTIVE, 0, now, now);
    }

    private ProjectPhase activePhase() {
        Instant now = Instant.now();
        return new ProjectPhase(phaseId, projectId, null, "PHASE_1", "Phase 1", null, 1,
                null, null, ProjectPhaseStatus.ACTIVE, 0, now, now);
    }

    private WbsNode wbsNode() {
        Instant now = Instant.now();
        return new WbsNode(wbsId, projectId, phaseId, null, "WBS_1", "Title", null,
                WbsNodeType.WORK_PACKAGE, 1, "WBS_1", 1, WbsNodeStatus.ACTIVE, 0, now, now);
    }

    private Task task(UUID assigneeId) {
        Instant now = Instant.now();
        return new Task(taskId, projectId, phaseId, wbsId, "TASK_1", "Task", null, assigneeId, null, null,
                BigDecimal.ONE, null, null, TaskPriority.MEDIUM, TaskStatus.TODO, 0, now, now);
    }

    private CreateTaskCommand baseCreateTaskCommand(UUID assigneeId) {
        return new CreateTaskCommand(projectId, phaseId, wbsId, "TASK_1", "Task", null,
                assigneeId, null, null, BigDecimal.ONE, null, null, null);
    }

    private UpdateTaskCommand baseUpdateTaskCommand(UUID assigneeId) {
        return new UpdateTaskCommand(taskId, projectId, "Task", null, assigneeId, null, null,
                BigDecimal.ONE, null, null, null);
    }
}
