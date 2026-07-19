package com.company.scopery.modules.project.templateversion.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.application.command.ArchiveProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveProjectTemplateVersionAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final TemplateAccessSupport authorizationSupport;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final ProjectActivityLogger activityLogger;

    public ArchiveProjectTemplateVersionAction(ProjectTemplateRepository templateRepository,
                                               ProjectTemplateVersionRepository versionRepository,
                                               TemplateAccessSupport authorizationSupport,
                                               CurrentUserAuthorizationService currentUserAuthorizationService,
                                               ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.authorizationSupport = authorizationSupport;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateVersionResponse execute(ArchiveProjectTemplateVersionCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireArchive(template);

        ProjectTemplateVersion version = versionRepository.findById(cmd.versionId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId()));
        if (!version.projectTemplateId().equals(template.id())) {
            throw ProjectExceptions.projectTemplateVersionNotFound(cmd.versionId());
        }
        if (version.status() == ProjectTemplateVersionStatus.ARCHIVED) {
            return ProjectTemplateVersionResponse.from(version);
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        ProjectTemplateVersion saved = versionRepository.save(version.archive(actorId));

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_VERSION,
                saved.id(),
                ProjectActivityActions.ARCHIVE_PROJECT_TEMPLATE_VERSION,
                "Template version archived: v" + saved.versionNumber()
        );

        return ProjectTemplateVersionResponse.from(saved);
    }
}
