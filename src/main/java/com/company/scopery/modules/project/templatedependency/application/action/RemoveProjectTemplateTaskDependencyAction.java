package com.company.scopery.modules.project.templatedependency.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.application.command.RemoveProjectTemplateTaskDependencyCommand;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveProjectTemplateTaskDependencyAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public RemoveProjectTemplateTaskDependencyAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateVersionMutationGuard mutationGuard,
            TemplateAccessSupport authorizationSupport,
            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.dependencyRepository = dependencyRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveProjectTemplateTaskDependencyCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplateTaskDependency dep = dependencyRepository.findById(cmd.dependencyId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateDependencyNotFound(cmd.dependencyId()));
        if (!dep.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateDependencyNotFound(cmd.dependencyId());
        }

        dependencyRepository.deleteById(dep.id());

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_TASK_DEPENDENCY,
                dep.id(),
                ProjectActivityActions.REMOVE_PROJECT_TEMPLATE_TASK_DEPENDENCY,
                "Template dependency removed"
        );
    }
}
