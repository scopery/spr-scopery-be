package com.company.scopery.modules.project.template.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.application.command.DeactivateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeactivateProjectTemplateAction {

    private final ProjectTemplateRepository repository;
    private final TemplateAccessSupport accessSupport;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public DeactivateProjectTemplateAction(ProjectTemplateRepository repository,
                                           TemplateAccessSupport accessSupport,
                                           ProjectActivityLogger activityLogger,
                                           ProjectPlatformPublisher platformPublisher) {
        this.repository = repository;
        this.accessSupport = accessSupport;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateResponse execute(DeactivateProjectTemplateCommand cmd) {
        ProjectTemplate template = repository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.id()));
        accessSupport.requireUpdate(template);

        if (template.status() == ProjectTemplateStatus.ARCHIVED) {
            throw ProjectExceptions.projectTemplateArchived(cmd.id());
        }

        ProjectTemplate saved = repository.save(template.deactivate());
        platformPublisher.enqueueTemplate(saved, "PROJECT_TEMPLATE_DEACTIVATED");
        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE,
                saved.id(),
                ProjectActivityActions.DEACTIVATE_PROJECT_TEMPLATE,
                "Project template deactivated: " + saved.code()
        );
        return ProjectTemplateResponse.from(saved);
    }
}
