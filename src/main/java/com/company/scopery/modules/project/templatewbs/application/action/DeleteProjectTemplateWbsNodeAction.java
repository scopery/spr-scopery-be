package com.company.scopery.modules.project.templatewbs.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatewbs.application.command.DeleteProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class DeleteProjectTemplateWbsNodeAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public DeleteProjectTemplateWbsNodeAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateWbsNodeRepository wbsRepository,
            ProjectTemplateTaskRepository taskRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateVersionMutationGuard mutationGuard,
            TemplateAccessSupport authorizationSupport,
            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.wbsRepository = wbsRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteProjectTemplateWbsNodeCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplateWbsNode node = wbsRepository.findById(cmd.templateWbsNodeId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeNotFound(cmd.templateWbsNodeId()));
        if (!node.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateWbsNodePathMismatch(node.id(), cmd.versionId());
        }

        List<ProjectTemplateWbsNode> children = wbsRepository.findChildrenByParentId(node.id());
        if (!children.isEmpty() && !cmd.cascade()) {
            throw ProjectExceptions.projectTemplateWbsNodeHasChildren(node.id());
        }

        List<UUID> toDelete = new ArrayList<>();
        toDelete.add(node.id());
        if (cmd.cascade()) {
            collectDescendants(node.id(), toDelete);
        }

        Set<UUID> deleteSet = new HashSet<>(toDelete);
        List<ProjectTemplateTask> tasks = taskRepository.findByTemplateVersionId(cmd.versionId()).stream()
                .filter(t -> t.templateWbsNodeId() != null && deleteSet.contains(t.templateWbsNodeId()))
                .toList();
        Set<UUID> taskIds = new HashSet<>();
        for (ProjectTemplateTask task : tasks) {
            taskIds.add(task.id());
        }
        for (ProjectTemplateTaskDependency dep : dependencyRepository.findByTemplateVersionId(cmd.versionId())) {
            if (taskIds.contains(dep.predecessorTemplateTaskId()) || taskIds.contains(dep.successorTemplateTaskId())) {
                dependencyRepository.deleteById(dep.id());
            }
        }
        for (UUID taskId : taskIds) {
            taskRepository.deleteById(taskId);
        }

        // delete deepest first
        for (int i = toDelete.size() - 1; i >= 0; i--) {
            wbsRepository.deleteById(toDelete.get(i));
        }

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_WBS_NODE,
                node.id(),
                ProjectActivityActions.DELETE_PROJECT_TEMPLATE_WBS_NODE,
                "Template WBS node deleted: " + node.title()
        );
    }

    private void collectDescendants(UUID parentId, List<UUID> acc) {
        for (ProjectTemplateWbsNode child : wbsRepository.findChildrenByParentId(parentId)) {
            acc.add(child.id());
            collectDescendants(child.id(), acc);
        }
    }
}
