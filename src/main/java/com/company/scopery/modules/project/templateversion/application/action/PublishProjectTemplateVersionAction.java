package com.company.scopery.modules.project.templateversion.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templateversion.application.command.PublishProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PublishProjectTemplateVersionAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateAccessSupport authorizationSupport;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public PublishProjectTemplateVersionAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateVersionRepository versionRepository,
            ProjectTemplatePhaseRepository phaseRepository,
            ProjectTemplateWbsNodeRepository wbsRepository,
            ProjectTemplateTaskRepository taskRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateAccessSupport authorizationSupport,
            CurrentUserAuthorizationService currentUserAuthorizationService,
            ProjectActivityLogger activityLogger,
            ProjectPlatformPublisher platformPublisher) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.phaseRepository = phaseRepository;
        this.wbsRepository = wbsRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.authorizationSupport = authorizationSupport;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateVersionResponse execute(PublishProjectTemplateVersionCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requirePublish(template);

        ProjectTemplateVersion version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId()));
        if (!version.projectTemplateId().equals(template.id())) {
            throw ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId());
        }
        if (version.status() == ProjectTemplateVersionStatus.PUBLISHED) {
            throw ProjectExceptions.projectTemplateVersionAlreadyPublished(version.id());
        }
        if (version.status() != ProjectTemplateVersionStatus.DRAFT) {
            throw ProjectExceptions.projectTemplateVersionNotDraft(version.id());
        }

        validateStructure(version.id());

        // Archive previous current published version if different
        if (template.currentVersionId() != null && !template.currentVersionId().equals(version.id())) {
            versionRepository.findById(template.currentVersionId()).ifPresent(prev -> {
                if (prev.status() == ProjectTemplateVersionStatus.PUBLISHED) {
                    UUID actor = currentUserAuthorizationService.resolveCurrentUser().id();
                    versionRepository.save(prev.archive(actor));
                }
            });
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        ProjectTemplateVersion published = versionRepository.save(version.publish(actorId));
        templateRepository.save(template.withCurrentVersionId(published.id()));

        platformPublisher.enqueueTemplateVersion(published, "PROJECT_TEMPLATE_VERSION_PUBLISHED");
        platformPublisher.auditTemplateVersionPublished(actorId, published, template);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_VERSION,
                published.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_VERSION_PUBLISHED,
                "Template version published: v" + published.versionNumber()
        );

        return ProjectTemplateVersionResponse.from(published);
    }

    private void validateStructure(UUID versionId) {
        List<ProjectTemplatePhase> phases = phaseRepository.findByTemplateVersionId(versionId);
        if (phases.isEmpty()) {
            throw ProjectExceptions.projectTemplateVersionStructureInvalid("At least one template phase is required");
        }

        Set<UUID> phaseIds = phases.stream().map(ProjectTemplatePhase::id).collect(Collectors.toSet());

        List<ProjectTemplateWbsNode> nodes = wbsRepository.findByTemplateVersionId(versionId);
        Map<UUID, ProjectTemplateWbsNode> nodeById = nodes.stream()
                .collect(Collectors.toMap(ProjectTemplateWbsNode::id, n -> n));
        for (ProjectTemplateWbsNode node : nodes) {
            if (node.parentId() != null && !nodeById.containsKey(node.parentId())) {
                throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                        "WBS parent missing for node " + node.id());
            }
            if (node.templatePhaseId() != null && !phaseIds.contains(node.templatePhaseId())) {
                throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                        "WBS phase mismatch for node " + node.id());
            }
            // cycle check upward
            Set<UUID> seen = new HashSet<>();
            UUID cur = node.parentId();
            while (cur != null) {
                if (!seen.add(cur)) {
                    throw ProjectExceptions.projectTemplateVersionStructureInvalid("WBS cycle detected");
                }
                ProjectTemplateWbsNode parent = nodeById.get(cur);
                cur = parent != null ? parent.parentId() : null;
            }
        }

        List<ProjectTemplateTask> tasks = taskRepository.findByTemplateVersionId(versionId);
        Set<UUID> taskIds = tasks.stream().map(ProjectTemplateTask::id).collect(Collectors.toSet());
        Set<UUID> wbsIds = nodeById.keySet();
        for (ProjectTemplateTask task : tasks) {
            if (!phaseIds.contains(task.templatePhaseId())) {
                throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                        "Task phase mismatch for " + task.id());
            }
            if (task.templateWbsNodeId() != null && !wbsIds.contains(task.templateWbsNodeId())) {
                throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                        "Task WBS mismatch for " + task.id());
            }
        }

        List<ProjectTemplateTaskDependency> deps = dependencyRepository.findByTemplateVersionId(versionId);
        for (ProjectTemplateTaskDependency dep : deps) {
            if (!taskIds.contains(dep.predecessorTemplateTaskId())
                    || !taskIds.contains(dep.successorTemplateTaskId())) {
                throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                        "Dependency references unknown template task");
            }
        }
        for (ProjectTemplateTaskDependency dep : deps) {
            if (wouldCreateCycle(dep.predecessorTemplateTaskId(), dep.successorTemplateTaskId(), deps)) {
                throw ProjectExceptions.projectTemplateVersionStructureInvalid("Dependency cycle detected");
            }
        }
    }

    private boolean wouldCreateCycle(UUID predecessorId, UUID successorId,
                                     List<ProjectTemplateTaskDependency> all) {
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(successorId);
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            if (current.equals(predecessorId)) {
                return true;
            }
            if (!visited.add(current)) {
                continue;
            }
            all.stream()
                    .filter(d -> d.predecessorTemplateTaskId().equals(current))
                    .forEach(d -> queue.add(d.successorTemplateTaskId()));
        }
        return false;
    }
}
