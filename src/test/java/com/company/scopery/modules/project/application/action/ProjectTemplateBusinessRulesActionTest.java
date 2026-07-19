package com.company.scopery.modules.project.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.template.application.action.ActivateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.ApplyProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.ArchiveProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.CreateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.UpdateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.command.ActivateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.ApplyProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.ArchiveProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.CreateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.UpdateProjectTemplateCommand;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.application.action.CreateProjectTemplateTaskDependencyAction;
import com.company.scopery.modules.project.templatedependency.application.command.CreateProjectTemplateTaskDependencyCommand;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templateversion.application.action.PublishProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.action.UpdateProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.command.PublishProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.command.UpdateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectTemplateBusinessRulesActionTest {

    @Mock private ProjectTemplateRepository templateRepository;
    @Mock private ProjectTemplateVersionRepository versionRepository;
    @Mock private ProjectTemplatePhaseRepository templatePhaseRepository;
    @Mock private ProjectTemplateWbsNodeRepository templateWbsRepository;
    @Mock private ProjectTemplateTaskRepository templateTaskRepository;
    @Mock private ProjectTemplateTaskDependencyRepository templateDependencyRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectPhaseRepository projectPhaseRepository;
    @Mock private WbsNodeRepository wbsNodeRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private TaskDependencyRepository taskDependencyRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private ProjectActivityLogger activityLogger;
    @Mock private ProjectWorkspaceAuthorizationService authorizationService;
    @Mock private ProjectPlatformPublisher platformPublisher;
    @Mock private CurrentUserAuthorizationService currentUserAuthorizationService;
    @Mock private IamSystemAuthorizationService systemAuthorizationService;

    private TemplateAccessSupport accessSupport;
    private TemplateVersionMutationGuard mutationGuard;

    private CreateProjectTemplateAction createProjectTemplateAction;
    private UpdateProjectTemplateAction updateProjectTemplateAction;
    private ActivateProjectTemplateAction activateProjectTemplateAction;
    private ArchiveProjectTemplateAction archiveProjectTemplateAction;
    private PublishProjectTemplateVersionAction publishProjectTemplateVersionAction;
    private UpdateProjectTemplateVersionAction updateProjectTemplateVersionAction;
    private ApplyProjectTemplateAction applyProjectTemplateAction;
    private CreateProjectTemplateTaskDependencyAction createTemplateDependencyAction;

    private final UUID organizationId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID templateId = UUID.randomUUID();
    private final UUID versionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        accessSupport = new TemplateAccessSupport(authorizationService, systemAuthorizationService);
        mutationGuard = new TemplateVersionMutationGuard(versionRepository);

        createProjectTemplateAction = new CreateProjectTemplateAction(
                templateRepository, accessSupport, activityLogger, platformPublisher);
        updateProjectTemplateAction = new UpdateProjectTemplateAction(
                templateRepository, accessSupport, activityLogger, platformPublisher);
        activateProjectTemplateAction = new ActivateProjectTemplateAction(
                templateRepository, versionRepository, accessSupport, activityLogger, platformPublisher);
        archiveProjectTemplateAction = new ArchiveProjectTemplateAction(
                templateRepository, accessSupport, currentUserAuthorizationService, activityLogger, platformPublisher);
        publishProjectTemplateVersionAction = new PublishProjectTemplateVersionAction(
                templateRepository, versionRepository, templatePhaseRepository, templateWbsRepository,
                templateTaskRepository, templateDependencyRepository, accessSupport,
                currentUserAuthorizationService, activityLogger, platformPublisher);
        updateProjectTemplateVersionAction = new UpdateProjectTemplateVersionAction(
                templateRepository, versionRepository, mutationGuard, accessSupport, activityLogger);
        applyProjectTemplateAction = new ApplyProjectTemplateAction(
                templateRepository, versionRepository, templatePhaseRepository, templateWbsRepository,
                templateTaskRepository, templateDependencyRepository, projectRepository, projectPhaseRepository,
                wbsNodeRepository, taskRepository, taskDependencyRepository, workspaceRepository,
                workspaceMemberRepository, authorizationService, currentUserAuthorizationService,
                activityLogger, platformPublisher);
        createTemplateDependencyAction = new CreateProjectTemplateTaskDependencyAction(
                templateRepository, templateTaskRepository, templateDependencyRepository,
                mutationGuard, accessSupport, activityLogger);

        Instant now = Instant.now();
        IamUser currentUser = IamUser.of(UUID.randomUUID(), Username.of("actor"),
                EmailAddress.of("actor@example.com"), "Actor", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserAuthorizationService.resolveCurrentUser()).thenReturn(currentUser);
        lenient().when(templateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(versionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(projectPhaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(templateDependencyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createTemplate_success() {
        when(templateRepository.existsByCodeAndScopeAndWorkspaceId("WEB_APP", ProjectTemplateScope.WORKSPACE, workspaceId))
                .thenReturn(false);

        var response = createProjectTemplateAction.execute(new CreateProjectTemplateCommand(
                "web_app", "Web App", "desc", "WORKSPACE", null, workspaceId,
                "WEB_APP", "WORKSPACE", false));

        assertThat(response.code()).isEqualTo("WEB_APP");
        assertThat(response.status()).isEqualTo("DRAFT");
        verify(platformPublisher).enqueueTemplate(any(), org.mockito.ArgumentMatchers.eq("PROJECT_TEMPLATE_CREATED"));
    }

    @Test
    void createTemplate_duplicateCode_conflict() {
        when(templateRepository.existsByCodeAndScopeAndWorkspaceId("WEB_APP", ProjectTemplateScope.WORKSPACE, workspaceId))
                .thenReturn(true);

        assertThatThrownBy(() -> createProjectTemplateAction.execute(new CreateProjectTemplateCommand(
                "WEB_APP", "Web App", null, "WORKSPACE", null, workspaceId, null, null, false)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void updateArchivedTemplate_rejected() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.ARCHIVED, null)));

        assertThatThrownBy(() -> updateProjectTemplateAction.execute(
                new UpdateProjectTemplateCommand(templateId, "New", null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_ARCHIVED.code()));
    }

    @Test
    void activateWithoutPublished_rejected() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.DRAFT, null)));

        assertThatThrownBy(() -> activateProjectTemplateAction.execute(new ActivateProjectTemplateCommand(templateId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_NO_PUBLISHED_VERSION.code()));
    }

    @Test
    void publish_withoutPhases_rejected() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.DRAFT, null)));
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(version(ProjectTemplateVersionStatus.DRAFT)));
        when(templatePhaseRepository.findByTemplateVersionId(versionId)).thenReturn(List.of());

        assertThatThrownBy(() -> publishProjectTemplateVersionAction.execute(
                new PublishProjectTemplateVersionCommand(templateId, versionId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_STRUCTURE_INVALID.code()));
    }

    @Test
    void updatePublishedVersion_rejected() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.ACTIVE, versionId)));
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(
                version(ProjectTemplateVersionStatus.PUBLISHED)));

        assertThatThrownBy(() -> updateProjectTemplateVersionAction.execute(
                new UpdateProjectTemplateVersionCommand(templateId, versionId, "v2", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_VERSION_NOT_DRAFT.code()));
    }

    @Test
    void apply_createsProjectStructureMapping() {
        UUID phaseTplId = UUID.randomUUID();
        ProjectTemplatePhase tplPhase = ProjectTemplatePhase.create(
                versionId, null, "DISC", "Discovery", null, 1, null, null, null);
        // force known id via reconstructing record
        tplPhase = new ProjectTemplatePhase(
                phaseTplId, versionId, null, "DISC", "Discovery", null, 1,
                null, null, null, 0, Instant.now(), Instant.now());

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.ACTIVE, versionId)));
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(
                version(ProjectTemplateVersionStatus.PUBLISHED)));
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace(WorkspaceStatus.ACTIVE)));
        when(projectRepository.existsByWorkspaceIdAndCode(workspaceId, "FROM_TPL")).thenReturn(false);
        when(templatePhaseRepository.findByTemplateVersionId(versionId)).thenReturn(List.of(tplPhase));
        when(templateWbsRepository.findByTemplateVersionId(versionId)).thenReturn(List.of());
        when(templateTaskRepository.findByTemplateVersionId(versionId)).thenReturn(List.of());

        var response = applyProjectTemplateAction.execute(new ApplyProjectTemplateCommand(
                templateId, versionId, workspaceId, "FROM_TPL", "From Template", "desc",
                null, "USD", null, null, true, true, true));

        assertThat(response.code()).isEqualTo("FROM_TPL");
        assertThat(response.sourceTemplateId()).isEqualTo(templateId);
        assertThat(response.sourceTemplateVersionId()).isEqualTo(versionId);
        assertThat(response.sourceTemplateAppliedAt()).isNotNull();

        ArgumentCaptor<ProjectPhase> phaseCaptor = ArgumentCaptor.forClass(ProjectPhase.class);
        verify(projectPhaseRepository).save(phaseCaptor.capture());
        assertThat(phaseCaptor.getValue().code()).isEqualTo("DISC");
        assertThat(phaseCaptor.getValue().name()).isEqualTo("Discovery");
    }

    @Test
    void apply_inactiveTemplate_rejected() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.INACTIVE, versionId)));

        assertThatThrownBy(() -> applyProjectTemplateAction.execute(new ApplyProjectTemplateCommand(
                templateId, versionId, workspaceId, "X", "X", null, null, null, null, null, true, true, true)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_INACTIVE.code()));
    }

    @Test
    void archive_blocksApply() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.DRAFT, versionId)));
        archiveProjectTemplateAction.execute(new ArchiveProjectTemplateCommand(templateId));

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.ARCHIVED, versionId)));

        assertThatThrownBy(() -> applyProjectTemplateAction.execute(new ApplyProjectTemplateCommand(
                templateId, versionId, workspaceId, "X", "X", null, null, null, null, null, true, true, true)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_ARCHIVED.code()));
    }

    @Test
    void templateDependency_cycle_rejected() {
        UUID taskA = UUID.randomUUID();
        UUID taskB = UUID.randomUUID();
        UUID taskC = UUID.randomUUID();

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(
                template(ProjectTemplateStatus.DRAFT, versionId)));
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(
                version(ProjectTemplateVersionStatus.DRAFT)));
        when(templateTaskRepository.findById(taskA)).thenReturn(Optional.of(templateTask(taskA)));
        when(templateTaskRepository.findById(taskC)).thenReturn(Optional.of(templateTask(taskC)));
        when(templateDependencyRepository.existsByPredecessorAndSuccessorAndType(
                taskC, taskA, TaskDependencyType.FINISH_TO_START)).thenReturn(false);

        ProjectTemplateTaskDependency ab = ProjectTemplateTaskDependency.create(
                versionId, taskA, taskB, TaskDependencyType.FINISH_TO_START, 0);
        ProjectTemplateTaskDependency bc = ProjectTemplateTaskDependency.create(
                versionId, taskB, taskC, TaskDependencyType.FINISH_TO_START, 0);
        when(templateDependencyRepository.findOutgoingDependencies(taskA)).thenReturn(List.of(ab));
        when(templateDependencyRepository.findOutgoingDependencies(taskB)).thenReturn(List.of(bc));

        assertThatThrownBy(() -> createTemplateDependencyAction.execute(
                new CreateProjectTemplateTaskDependencyCommand(
                        templateId, versionId, taskC, taskA, "FINISH_TO_START", 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.PROJECT_TEMPLATE_DEPENDENCY_CYCLE_DETECTED.code()));
    }

    private ProjectTemplate template(ProjectTemplateStatus status, UUID currentVersionId) {
        Instant now = Instant.now();
        return new ProjectTemplate(
                templateId, "WEB_APP", "Web App", null, ProjectTemplateScope.WORKSPACE,
                organizationId, workspaceId, null, null, status, currentVersionId, false,
                status == ProjectTemplateStatus.ARCHIVED ? now : null,
                status == ProjectTemplateStatus.ARCHIVED ? UUID.randomUUID() : null,
                0, now, now);
    }

    private ProjectTemplateVersion version(ProjectTemplateVersionStatus status) {
        Instant now = Instant.now();
        return new ProjectTemplateVersion(
                versionId, templateId, 1, "v1", null, status,
                status == ProjectTemplateVersionStatus.PUBLISHED ? now : null,
                status == ProjectTemplateVersionStatus.PUBLISHED ? UUID.randomUUID() : null,
                null, null, 0, now, now);
    }

    private ProjectTemplateTask templateTask(UUID id) {
        Instant now = Instant.now();
        return new ProjectTemplateTask(
                id, versionId, UUID.randomUUID(), null, "T1", "Task", null, null, null,
                null, null, null, null, 0, now, now);
    }

    private Workspace workspace(WorkspaceStatus status) {
        Instant now = Instant.now();
        return new Workspace(workspaceId, organizationId, WorkspaceCode.of("WS_01"), "Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY,
                status, 0, now, now);
    }
}
