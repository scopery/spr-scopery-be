package com.company.scopery.modules.project.templatephase.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatephase.application.command.DeleteProjectTemplatePhaseCommand;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DeleteProjectTemplatePhaseAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public DeleteProjectTemplatePhaseAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplatePhaseRepository phaseRepository,
            ProjectTemplateTaskRepository taskRepository,
            ProjectTemplateWbsNodeRepository wbsRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateVersionMutationGuard mutationGuard,
            TemplateAccessSupport authorizationSupport,
            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.phaseRepository = phaseRepository;
        this.taskRepository = taskRepository;
        this.wbsRepository = wbsRepository;
        this.dependencyRepository = dependencyRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteProjectTemplatePhaseCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplatePhase phase = phaseRepository.findById(cmd.templatePhaseId())
                .orElseThrow(() -> ProjectExceptions.projectTemplatePhaseNotFound(cmd.templatePhaseId()));
        if (!phase.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplatePhasePathMismatch(phase.id(), cmd.versionId());
        }

        boolean hasTasks = taskRepository.existsByTemplatePhaseId(phase.id());
        if (hasTasks && !cmd.cascade()) {
            throw ProjectExceptions.projectTemplatePhaseHasTasks(phase.id());
        }

        if (cmd.cascade()) {
            List<ProjectTemplateTask> tasks = taskRepository.findByTemplateVersionId(cmd.versionId()).stream()
                    .filter(t -> phase.id().equals(t.templatePhaseId()))
                    .toList();
            Set<java.util.UUID> taskIds = tasks.stream().map(ProjectTemplateTask::id).collect(Collectors.toSet());
            for (ProjectTemplateTaskDependency dep : dependencyRepository.findByTemplateVersionId(cmd.versionId())) {
                if (taskIds.contains(dep.predecessorTemplateTaskId())
                        || taskIds.contains(dep.successorTemplateTaskId())) {
                    dependencyRepository.deleteById(dep.id());
                }
            }
            for (ProjectTemplateTask task : tasks) {
                taskRepository.deleteById(task.id());
            }
            for (ProjectTemplateWbsNode node : wbsRepository.findByTemplateVersionId(cmd.versionId())) {
                if (phase.id().equals(node.templatePhaseId())) {
                    // clear phase link by rewriting without cascade delete of WBS tree unless orphaned
                    wbsRepository.save(node.update(
                            node.code(), node.title(), node.description(), node.nodeType(),
                            null, node.deliverableDocumentTypeId()));
                }
            }
        }

        phaseRepository.deleteById(phase.id());

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_PHASE,
                phase.id(),
                ProjectActivityActions.DELETE_PROJECT_TEMPLATE_PHASE,
                "Template phase deleted: " + phase.name()
        );
    }
}
