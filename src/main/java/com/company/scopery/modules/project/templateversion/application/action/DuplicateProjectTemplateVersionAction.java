package com.company.scopery.modules.project.templateversion.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templateversion.application.command.DuplicateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DuplicateProjectTemplateVersionAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public DuplicateProjectTemplateVersionAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateVersionRepository versionRepository,
            ProjectTemplatePhaseRepository phaseRepository,
            ProjectTemplateWbsNodeRepository wbsRepository,
            ProjectTemplateTaskRepository taskRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateAccessSupport authorizationSupport,
            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.phaseRepository = phaseRepository;
        this.wbsRepository = wbsRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateVersionResponse execute(DuplicateProjectTemplateVersionCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        if (template.status() == ProjectTemplateStatus.ARCHIVED) {
            throw ProjectExceptions.projectTemplateArchived(template.id());
        }
        authorizationSupport.requireUpdate(template);

        ProjectTemplateVersion source = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId()));
        if (!source.projectTemplateId().equals(template.id())) {
            throw ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId());
        }

        int nextNumber = versionRepository.findMaxVersionNumber(template.id()).orElse(0) + 1;
        ProjectTemplateVersion draft = versionRepository.save(ProjectTemplateVersion.create(
                template.id(),
                nextNumber,
                source.name() != null ? source.name() + " (copy)" : "v" + nextNumber,
                source.description()
        ));

        Map<UUID, UUID> phaseMap = new HashMap<>();
        for (ProjectTemplatePhase phase : phaseRepository.findByTemplateVersionId(source.id())) {
            ProjectTemplatePhase copy = ProjectTemplatePhase.create(
                    draft.id(),
                    phase.phaseDefinitionId(),
                    phase.code(),
                    phase.name(),
                    phase.description(),
                    phase.displayOrder(),
                    phase.defaultDurationDays(),
                    phase.startOffsetDays(),
                    phase.deliverableDocumentTypeId()
            );
            phaseMap.put(phase.id(), phaseRepository.save(copy).id());
        }

        Map<UUID, UUID> wbsMap = new HashMap<>();
        List<ProjectTemplateWbsNode> nodes = wbsRepository.findByTemplateVersionId(source.id()).stream()
                .sorted(Comparator.comparingInt(ProjectTemplateWbsNode::depth)
                        .thenComparingInt(ProjectTemplateWbsNode::orderIndex))
                .toList();
        for (ProjectTemplateWbsNode node : nodes) {
            ProjectTemplateWbsNode copy = ProjectTemplateWbsNode.create(
                    draft.id(),
                    node.parentId() != null ? wbsMap.get(node.parentId()) : null,
                    node.templatePhaseId() != null ? phaseMap.get(node.templatePhaseId()) : null,
                    node.code(),
                    node.title(),
                    node.description(),
                    node.nodeType(),
                    node.depth(),
                    node.orderIndex(),
                    node.deliverableDocumentTypeId()
            );
            wbsMap.put(node.id(), wbsRepository.save(copy).id());
        }

        Map<UUID, UUID> taskMap = new HashMap<>();
        for (ProjectTemplateTask task : taskRepository.findByTemplateVersionId(source.id())) {
            ProjectTemplateTask copy = ProjectTemplateTask.create(
                    draft.id(),
                    phaseMap.get(task.templatePhaseId()),
                    task.templateWbsNodeId() != null ? wbsMap.get(task.templateWbsNodeId()) : null,
                    task.code(),
                    task.title(),
                    task.description(),
                    task.defaultPriority(),
                    task.estimateHours(),
                    task.dueOffsetDays(),
                    task.startOffsetDays(),
                    task.defaultAssigneeRoleCode(),
                    task.deliverableDocumentTypeId()
            );
            taskMap.put(task.id(), taskRepository.save(copy).id());
        }

        for (ProjectTemplateTaskDependency dep : dependencyRepository.findByTemplateVersionId(source.id())) {
            UUID pred = taskMap.get(dep.predecessorTemplateTaskId());
            UUID succ = taskMap.get(dep.successorTemplateTaskId());
            if (pred == null || succ == null) {
                continue;
            }
            dependencyRepository.save(ProjectTemplateTaskDependency.create(
                    draft.id(), pred, succ, dep.dependencyType(), dep.lagDays()));
        }

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_VERSION,
                draft.id(),
                ProjectActivityActions.DUPLICATE_PROJECT_TEMPLATE_VERSION,
                "Template version duplicated to v" + draft.versionNumber()
        );

        return ProjectTemplateVersionResponse.from(draft);
    }
}
