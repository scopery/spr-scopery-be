package com.company.scopery.modules.project.templatetask.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatetask.application.command.DeleteProjectTemplateTaskCommand;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteProjectTemplateTaskAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public DeleteProjectTemplateTaskAction(ProjectTemplateRepository templateRepository,
                                           ProjectTemplateTaskRepository taskRepository,
                                           ProjectTemplateTaskDependencyRepository dependencyRepository,
                                           TemplateVersionMutationGuard mutationGuard,
                                           TemplateAccessSupport authorizationSupport,
                                           ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteProjectTemplateTaskCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplateTask task = taskRepository.findById(cmd.templateTaskId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateTaskNotFound(cmd.templateTaskId()));
        if (!task.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateTaskPathMismatch(task.id(), cmd.versionId());
        }

        for (ProjectTemplateTaskDependency dep : dependencyRepository.findByTemplateVersionId(cmd.versionId())) {
            if (dep.predecessorTemplateTaskId().equals(task.id())
                    || dep.successorTemplateTaskId().equals(task.id())) {
                dependencyRepository.deleteById(dep.id());
            }
        }

        taskRepository.deleteById(task.id());

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_TASK,
                task.id(),
                ProjectActivityActions.DELETE_PROJECT_TEMPLATE_TASK,
                "Template task deleted: " + task.title()
        );
    }
}
