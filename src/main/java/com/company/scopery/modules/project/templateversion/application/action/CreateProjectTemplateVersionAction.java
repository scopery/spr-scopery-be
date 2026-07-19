package com.company.scopery.modules.project.templateversion.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.application.command.CreateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectTemplateVersionAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateProjectTemplateVersionAction(ProjectTemplateRepository templateRepository,
                                              ProjectTemplateVersionRepository versionRepository,
                                              TemplateAccessSupport authorizationSupport,
                                              ProjectActivityLogger activityLogger,
                                              ProjectPlatformPublisher platformPublisher) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateVersionResponse execute(CreateProjectTemplateVersionCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));

        if (template.status() == ProjectTemplateStatus.ARCHIVED) {
            throw ProjectExceptions.projectTemplateArchived(template.id());
        }

        authorizationSupport.requireUpdate(template);

        int nextNumber = versionRepository.findMaxVersionNumber(template.id()).orElse(0) + 1;
        ProjectTemplateVersion version = ProjectTemplateVersion.create(
                template.id(), nextNumber, cmd.name(), cmd.description());
        ProjectTemplateVersion saved = versionRepository.save(version);

        platformPublisher.enqueueTemplateVersion(saved, "PROJECT_TEMPLATE_VERSION_CREATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_VERSION,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_VERSION_CREATED,
                "Template version created: v" + saved.versionNumber()
        );

        return ProjectTemplateVersionResponse.from(saved);
    }
}
