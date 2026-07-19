package com.company.scopery.modules.project.template.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.application.command.ArchiveProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveProjectTemplateAction {

    private final ProjectTemplateRepository repository;
    private final TemplateAccessSupport accessSupport;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public ArchiveProjectTemplateAction(ProjectTemplateRepository repository,
                                        TemplateAccessSupport accessSupport,
                                        CurrentUserAuthorizationService currentUserAuthorizationService,
                                        ProjectActivityLogger activityLogger,
                                        ProjectPlatformPublisher platformPublisher) {
        this.repository = repository;
        this.accessSupport = accessSupport;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateResponse execute(ArchiveProjectTemplateCommand cmd) {
        ProjectTemplate template = repository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.id()));
        accessSupport.requireArchive(template);

        if (template.status() == ProjectTemplateStatus.ARCHIVED) {
            throw ProjectExceptions.projectTemplateArchived(cmd.id());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        ProjectTemplate saved = repository.save(template.archive(actorId));
        platformPublisher.enqueueTemplate(saved, "PROJECT_TEMPLATE_ARCHIVED");
        platformPublisher.auditTemplateArchived(actorId, saved);
        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_ARCHIVED,
                "Project template archived: " + saved.code()
        );
        return ProjectTemplateResponse.from(saved);
    }
}
