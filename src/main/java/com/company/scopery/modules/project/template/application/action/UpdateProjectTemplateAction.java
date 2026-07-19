package com.company.scopery.modules.project.template.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.template.application.command.UpdateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateVisibility;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateProjectTemplateAction {

    private final ProjectTemplateRepository repository;
    private final TemplateAccessSupport accessSupport;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public UpdateProjectTemplateAction(ProjectTemplateRepository repository,
                                       TemplateAccessSupport accessSupport,
                                       ProjectActivityLogger activityLogger,
                                       ProjectPlatformPublisher platformPublisher) {
        this.repository = repository;
        this.accessSupport = accessSupport;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateResponse execute(UpdateProjectTemplateCommand cmd) {
        ProjectTemplate template = repository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.id()));
        accessSupport.requireUpdate(template);

        if (template.status() == ProjectTemplateStatus.ARCHIVED) {
            throw ProjectExceptions.projectTemplateArchived(cmd.id());
        }

        ProjectTemplateCategory category = ProjectEnumParser.parseOptional(
                ProjectTemplateCategory.class, cmd.category(), "INVALID_CATEGORY", "category");
        ProjectTemplateVisibility visibility = ProjectEnumParser.parseOptional(
                ProjectTemplateVisibility.class, cmd.visibility(), "INVALID_VISIBILITY", "visibility");

        ProjectTemplate saved = repository.save(template.update(
                cmd.name(), cmd.description(), category, visibility));
        platformPublisher.enqueueTemplate(saved, "PROJECT_TEMPLATE_UPDATED");
        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_UPDATED,
                "Project template updated: " + saved.code()
        );
        return ProjectTemplateResponse.from(saved);
    }
}
