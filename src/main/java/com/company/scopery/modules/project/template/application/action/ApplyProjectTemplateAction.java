package com.company.scopery.modules.project.template.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.project.domain.valueobject.ProjectCode;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.template.application.command.ApplyProjectTemplateCommand;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import com.company.scopery.modules.project.templatewbs.domain.enums.TemplateWbsNodeType;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ApplyProjectTemplateAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplatePhaseRepository templatePhaseRepository;
    private final ProjectTemplateWbsNodeRepository templateWbsRepository;
    private final ProjectTemplateTaskRepository templateTaskRepository;
    private final ProjectTemplateTaskDependencyRepository templateDependencyRepository;
    private final ProjectRepository projectRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final WbsNodeRepository wbsNodeRepository;
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public ApplyProjectTemplateAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateVersionRepository versionRepository,
            ProjectTemplatePhaseRepository templatePhaseRepository,
            ProjectTemplateWbsNodeRepository templateWbsRepository,
            ProjectTemplateTaskRepository templateTaskRepository,
            ProjectTemplateTaskDependencyRepository templateDependencyRepository,
            ProjectRepository projectRepository,
            ProjectPhaseRepository projectPhaseRepository,
            WbsNodeRepository wbsNodeRepository,
            TaskRepository taskRepository,
            TaskDependencyRepository taskDependencyRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            ProjectWorkspaceAuthorizationService authorizationService,
            CurrentUserAuthorizationService currentUserAuthorizationService,
            ProjectActivityLogger activityLogger,
            ProjectPlatformPublisher platformPublisher) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.templatePhaseRepository = templatePhaseRepository;
        this.templateWbsRepository = templateWbsRepository;
        this.templateTaskRepository = templateTaskRepository;
        this.templateDependencyRepository = templateDependencyRepository;
        this.projectRepository = projectRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.wbsNodeRepository = wbsNodeRepository;
        this.taskRepository = taskRepository;
        this.taskDependencyRepository = taskDependencyRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.authorizationService = authorizationService;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResponse execute(ApplyProjectTemplateCommand cmd) {
        // PROJECT_CREATE + PROJECT_TEMPLATE_APPLY
        authorizationService.requireProjectCreate(cmd.workspaceId());
        authorizationService.requireTemplateApply(cmd.workspaceId());

        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));

        if (template.status() != ProjectTemplateStatus.ACTIVE) {
            if (template.status() == ProjectTemplateStatus.ARCHIVED) {
                throw ProjectExceptions.projectTemplateArchived(template.id());
            }
            throw ProjectExceptions.projectTemplateInactive(template.id());
        }

        ProjectTemplateVersion version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId()));

        if (!version.projectTemplateId().equals(template.id())) {
            throw ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId());
        }
        if (version.status() != ProjectTemplateVersionStatus.PUBLISHED) {
            throw ProjectExceptions.projectTemplateVersionNotPublished(version.id());
        }

        Workspace workspace = workspaceRepository.findById(cmd.workspaceId())
                .orElseThrow(() -> ProjectExceptions.projectWorkspaceNotFound(cmd.workspaceId()));
        if (workspace.status() != WorkspaceStatus.ACTIVE) {
            throw ProjectExceptions.projectTemplateApplyWorkspaceInactive(cmd.workspaceId());
        }

        if (cmd.ownerUserId() != null
                && !workspaceMemberRepository.isActiveMember(cmd.workspaceId(), cmd.ownerUserId())) {
            throw ProjectExceptions.projectOwnerNotWorkspaceMember(cmd.ownerUserId());
        }

        ProjectCode code = ProjectCode.of(cmd.projectCode());
        if (projectRepository.existsByWorkspaceIdAndCode(cmd.workspaceId(), code.value())) {
            throw ProjectExceptions.projectTemplateApplyDuplicateProjectCode(code.value());
        }

        if (cmd.plannedStartDate() != null && cmd.plannedEndDate() != null
                && cmd.plannedEndDate().isBefore(cmd.plannedStartDate())) {
            throw ProjectExceptions.projectInvalidDateRange();
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();

        Project project = Project.create(
                cmd.workspaceId(),
                workspace.organizationId(),
                code.value(),
                cmd.projectName(),
                cmd.projectDescription(),
                cmd.ownerUserId(),
                cmd.defaultCurrency(),
                cmd.plannedStartDate(),
                cmd.plannedEndDate()
        ).withSourceTemplate(template.id(), version.id(), Instant.now());

        Project savedProject = projectRepository.save(project);

        List<ProjectTemplatePhase> templatePhases = templatePhaseRepository.findByTemplateVersionId(version.id());
        Map<UUID, UUID> phaseIdMap = new HashMap<>();
        LocalDate baseStart = cmd.plannedStartDate();

        for (ProjectTemplatePhase tp : templatePhases) {
            LocalDate phaseStart = null;
            LocalDate phaseEnd = null;
            if (baseStart != null) {
                int startOffset = tp.startOffsetDays() != null ? tp.startOffsetDays() : 0;
                phaseStart = baseStart.plusDays(startOffset);
                if (tp.defaultDurationDays() != null && tp.defaultDurationDays() > 0) {
                    phaseEnd = phaseStart.plusDays(tp.defaultDurationDays() - 1L);
                }
            }

            String phaseCode = tp.code() != null && !tp.code().isBlank()
                    ? tp.code()
                    : "PHASE_" + tp.displayOrder();

            ProjectPhase phase;
            if (tp.phaseDefinitionId() != null) {
                phase = ProjectPhase.createFromDefinition(
                        savedProject.id(),
                        tp.phaseDefinitionId(),
                        phaseCode,
                        tp.name(),
                        tp.description(),
                        tp.displayOrder(),
                        phaseStart,
                        phaseEnd
                );
            } else {
                phase = ProjectPhase.create(
                        savedProject.id(),
                        phaseCode,
                        tp.name(),
                        tp.description(),
                        tp.displayOrder(),
                        phaseStart,
                        phaseEnd
                );
            }
            ProjectPhase savedPhase = projectPhaseRepository.save(phase);
            phaseIdMap.put(tp.id(), savedPhase.id());
        }

        List<ProjectTemplateWbsNode> templateNodes = templateWbsRepository.findByTemplateVersionId(version.id())
                .stream()
                .sorted(Comparator.comparingInt(ProjectTemplateWbsNode::depth)
                        .thenComparingInt(ProjectTemplateWbsNode::orderIndex))
                .toList();

        Map<UUID, UUID> wbsIdMap = new HashMap<>();
        Map<UUID, WbsNode> createdWbs = new HashMap<>();

        for (ProjectTemplateWbsNode tn : templateNodes) {
            UUID projectPhaseId = tn.templatePhaseId() != null
                    ? phaseIdMap.get(tn.templatePhaseId())
                    : phaseIdMap.values().stream().findFirst().orElse(null);
            if (projectPhaseId == null) {
                throw ProjectExceptions.projectTemplateApplyFailed(
                        "Template WBS node requires at least one template phase");
            }

            UUID parentProjectId = tn.parentId() != null ? wbsIdMap.get(tn.parentId()) : null;
            WbsNode parent = parentProjectId != null ? createdWbs.get(parentProjectId) : null;

            String nodeCode = tn.code() != null && !tn.code().isBlank()
                    ? tn.code()
                    : "WBS_" + tn.orderIndex();

            int level;
            String path;
            if (parent == null) {
                level = 1;
                path = nodeCode;
            } else {
                level = parent.level() + 1;
                path = parent.path() + "/" + nodeCode;
            }

            WbsNodeType nodeType = mapWbsType(tn.nodeType());

            WbsNode node = WbsNode.create(
                    savedProject.id(),
                    projectPhaseId,
                    parentProjectId,
                    nodeCode,
                    tn.title(),
                    tn.description(),
                    nodeType,
                    level,
                    path,
                    tn.orderIndex()
            );
            WbsNode savedNode = wbsNodeRepository.save(node);
            wbsIdMap.put(tn.id(), savedNode.id());
            createdWbs.put(savedNode.id(), savedNode);
        }

        Map<UUID, UUID> taskIdMap = new HashMap<>();
        if (cmd.includeTemplateTasks()) {
            List<ProjectTemplateTask> templateTasks = templateTaskRepository.findByTemplateVersionId(version.id());
            for (ProjectTemplateTask tt : templateTasks) {
                UUID projectPhaseId = phaseIdMap.get(tt.templatePhaseId());
                if (projectPhaseId == null) {
                    throw ProjectExceptions.projectTemplateApplyFailed(
                            "Template task phase mapping missing for " + tt.id());
                }
                UUID wbsNodeId = tt.templateWbsNodeId() != null ? wbsIdMap.get(tt.templateWbsNodeId()) : null;

                String taskCode = tt.code() != null && !tt.code().isBlank()
                        ? tt.code()
                        : "TASK_" + tt.id().toString().substring(0, 8).toUpperCase();

                TaskPriority priority = tt.defaultPriority() != null ? tt.defaultPriority() : TaskPriority.MEDIUM;

                LocalDate plannedStart = null;
                LocalDate dueDate = null;
                if (baseStart != null) {
                    if (tt.startOffsetDays() != null) {
                        plannedStart = baseStart.plusDays(tt.startOffsetDays());
                    }
                    if (tt.dueOffsetDays() != null) {
                        dueDate = baseStart.plusDays(tt.dueOffsetDays());
                    }
                }

                // Phase 09 requires estimateHours > 0 on project tasks. When the template
                // omits an estimate (or copyEstimateHours is false), default to 1.0 hours.
                java.math.BigDecimal estimateHours;
                if (cmd.copyEstimateHours() && tt.estimateHours() != null) {
                    estimateHours = tt.estimateHours();
                } else {
                    estimateHours = java.math.BigDecimal.ONE;
                }

                Task task = Task.create(
                        savedProject.id(),
                        projectPhaseId,
                        wbsNodeId,
                        taskCode,
                        tt.title(),
                        tt.description(),
                        null,
                        tt.defaultAssigneeRoleCode(),
                        null,
                        estimateHours,
                        plannedStart,
                        dueDate,
                        priority
                );
                Task savedTask = taskRepository.save(task);
                taskIdMap.put(tt.id(), savedTask.id());
            }
        }

        if (cmd.includeTemplateDependencies() && cmd.includeTemplateTasks()) {
            List<ProjectTemplateTaskDependency> deps =
                    templateDependencyRepository.findByTemplateVersionId(version.id());
            for (ProjectTemplateTaskDependency dep : deps) {
                UUID pred = taskIdMap.get(dep.predecessorTemplateTaskId());
                UUID succ = taskIdMap.get(dep.successorTemplateTaskId());
                if (pred == null || succ == null) {
                    continue;
                }
                TaskDependency created = TaskDependency.create(
                        savedProject.id(),
                        pred,
                        succ,
                        dep.dependencyType(),
                        dep.lagDays()
                );
                taskDependencyRepository.save(created);
            }
        }

        platformPublisher.enqueueProject(savedProject, "PROJECT_CREATED");
        platformPublisher.enqueueTemplate(template, "PROJECT_TEMPLATE_APPLIED");
        platformPublisher.auditTemplateApplied(actorId, savedProject, template, version);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE,
                template.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_APPLIED,
                "Template applied to project: " + savedProject.code()
        );

        return ProjectResponse.from(savedProject);
    }

    private static WbsNodeType mapWbsType(TemplateWbsNodeType type) {
        if (type == null) {
            return WbsNodeType.WORK_PACKAGE;
        }
        return switch (type) {
            case WORK_PACKAGE -> WbsNodeType.WORK_PACKAGE;
            case DELIVERABLE -> WbsNodeType.DELIVERABLE;
            case TASK_GROUP -> WbsNodeType.TASK_GROUP;
        };
    }
}
