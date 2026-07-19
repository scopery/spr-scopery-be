package com.company.scopery.modules.project.templateversion.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.application.command.UpdateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateProjectTemplateVersionAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public UpdateProjectTemplateVersionAction(ProjectTemplateRepository templateRepository,
                                              ProjectTemplateVersionRepository versionRepository,
                                              TemplateVersionMutationGuard mutationGuard,
                                              TemplateAccessSupport authorizationSupport,
                                              ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateVersionResponse execute(UpdateProjectTemplateVersionCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);

        ProjectTemplateVersion version = mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());
        ProjectTemplateVersion saved = versionRepository.save(version.update(cmd.name(), cmd.description()));

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_VERSION,
                saved.id(),
                ProjectActivityActions.UPDATE_PROJECT_TEMPLATE_VERSION,
                "Template version updated: v" + saved.versionNumber()
        );

        return ProjectTemplateVersionResponse.from(saved);
    }
}
