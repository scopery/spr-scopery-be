package com.company.scopery.modules.project.template.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.application.command.ActivateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivateProjectTemplateAction {

    private final ProjectTemplateRepository repository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final TemplateAccessSupport accessSupport;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public ActivateProjectTemplateAction(ProjectTemplateRepository repository,
                                         ProjectTemplateVersionRepository versionRepository,
                                         TemplateAccessSupport accessSupport,
                                         ProjectActivityLogger activityLogger,
                                         ProjectPlatformPublisher platformPublisher) {
        this.repository = repository;
        this.versionRepository = versionRepository;
        this.accessSupport = accessSupport;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateResponse execute(ActivateProjectTemplateCommand cmd) {
        ProjectTemplate template = repository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.id()));
        accessSupport.requireUpdate(template);

        if (template.status() == ProjectTemplateStatus.ARCHIVED) {
            throw ProjectExceptions.projectTemplateArchived(cmd.id());
        }

        if (template.currentVersionId() == null) {
            throw ProjectExceptions.projectTemplateNoPublishedVersion(cmd.id());
        }

        ProjectTemplateVersion current = versionRepository.findById(template.currentVersionId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNoPublishedVersion(cmd.id()));
        if (current.status() != ProjectTemplateVersionStatus.PUBLISHED) {
            throw ProjectExceptions.projectTemplateNoPublishedVersion(cmd.id());
        }

        ProjectTemplate saved = repository.save(template.activate(template.currentVersionId()));
        platformPublisher.enqueueTemplate(saved, "PROJECT_TEMPLATE_ACTIVATED");
        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE,
                saved.id(),
                ProjectActivityActions.ACTIVATE_PROJECT_TEMPLATE,
                "Project template activated: " + saved.code()
        );
        return ProjectTemplateResponse.from(saved);
    }
}
