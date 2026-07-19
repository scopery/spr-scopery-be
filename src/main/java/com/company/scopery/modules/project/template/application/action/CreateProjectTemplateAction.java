package com.company.scopery.modules.project.template.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.template.application.command.CreateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateVisibility;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.template.domain.valueobject.TemplateCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectTemplateAction {

    private final ProjectTemplateRepository repository;
    private final TemplateAccessSupport accessSupport;
    private final ProjectActivityLogger activityLogger;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateProjectTemplateAction(ProjectTemplateRepository repository,
                                       TemplateAccessSupport accessSupport,
                                       ProjectActivityLogger activityLogger,
                                       ProjectPlatformPublisher platformPublisher) {
        this.repository = repository;
        this.accessSupport = accessSupport;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectTemplateResponse execute(CreateProjectTemplateCommand cmd) {
        ProjectTemplateScope scope = ProjectEnumParser.parseRequired(
                ProjectTemplateScope.class, cmd.scope(), "INVALID_SCOPE", "scope");
        accessSupport.requireCreate(scope, cmd.workspaceId());

        validateScopeIds(scope, cmd);

        TemplateCode code = TemplateCode.of(cmd.code());
        assertUniqueCode(scope, code.value(), cmd.organizationId(), cmd.workspaceId());

        ProjectTemplateCategory category = ProjectEnumParser.parseOptional(
                ProjectTemplateCategory.class, cmd.category(), "INVALID_CATEGORY", "category");
        ProjectTemplateVisibility visibility = ProjectEnumParser.parseOptional(
                ProjectTemplateVisibility.class, cmd.visibility(), "INVALID_VISIBILITY", "visibility");

        ProjectTemplate template = ProjectTemplate.create(
                code.value(),
                cmd.name(),
                cmd.description(),
                scope,
                cmd.organizationId(),
                cmd.workspaceId(),
                category,
                visibility,
                cmd.builtIn()
        );

        ProjectTemplate saved = repository.save(template);
        platformPublisher.enqueueTemplate(saved, "PROJECT_TEMPLATE_CREATED");
        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_CREATED,
                "Project template created: " + saved.code()
        );
        return ProjectTemplateResponse.from(saved);
    }

    private void validateScopeIds(ProjectTemplateScope scope, CreateProjectTemplateCommand cmd) {
        switch (scope) {
            case SYSTEM -> {
                if (cmd.organizationId() != null || cmd.workspaceId() != null) {
                    throw ProjectExceptions.projectTemplateInvalidScope(
                            "SYSTEM templates must not set organizationId or workspaceId");
                }
            }
            case ORGANIZATION -> {
                if (cmd.organizationId() == null || cmd.workspaceId() != null) {
                    throw ProjectExceptions.projectTemplateInvalidScope(
                            "ORGANIZATION templates require organizationId and must not set workspaceId");
                }
            }
            case WORKSPACE -> {
                if (cmd.workspaceId() == null) {
                    throw ProjectExceptions.projectTemplateInvalidScope(
                            "WORKSPACE templates require workspaceId");
                }
            }
        }
    }

    private void assertUniqueCode(ProjectTemplateScope scope, String code,
                                  java.util.UUID organizationId, java.util.UUID workspaceId) {
        boolean exists = switch (scope) {
            case SYSTEM -> repository.existsByCodeAndScope(code, scope);
            case ORGANIZATION -> repository.existsByCodeAndScopeAndOrganizationId(code, scope, organizationId);
            case WORKSPACE -> repository.existsByCodeAndScopeAndWorkspaceId(code, scope, workspaceId);
        };
        if (exists) {
            throw ProjectExceptions.projectTemplateCodeAlreadyExists(code);
        }
    }
}
