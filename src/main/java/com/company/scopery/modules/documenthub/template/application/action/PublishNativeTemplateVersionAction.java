package com.company.scopery.modules.documenthub.template.application.action;

import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.template.application.command.PublishNativeTemplateVersionCommand;
import com.company.scopery.modules.documenthub.template.application.response.NativeTemplateVersionResponse;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariable;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariableRepository;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersion;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersionRepository;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PublishNativeTemplateVersionAction {

    private final DocumentTemplateRepository templateRepo;
    private final DocumentTemplateVersionRepository versionRepo;
    private final DocumentTemplateVariableRepository variableRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public PublishNativeTemplateVersionAction(DocumentTemplateRepository templateRepo,
                                               DocumentTemplateVersionRepository versionRepo,
                                               DocumentTemplateVariableRepository variableRepo,
                                               DocumentHubAuthorizationService authorization,
                                               DocumentHubActivityLogger activityLogger) {
        this.templateRepo = templateRepo;
        this.versionRepo = versionRepo;
        this.variableRepo = variableRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public NativeTemplateVersionResponse execute(PublishNativeTemplateVersionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var template = templateRepo.findByIdAndWorkspaceId(c.templateId(), c.workspaceId())
                .orElseThrow(() -> DocumentHubExceptions.templateNotFound(c.templateId()));

        var version = versionRepo.save(DocumentTemplateVersion.createNative(
                template.id(), 1, c.ast()));

        if (c.variables() != null && !c.variables().isEmpty()) {
            List<DocumentTemplateVariable> variables = c.variables().stream()
                    .map(v -> DocumentTemplateVariable.create(
                            version.id(), v.variableKey(), v.label(), v.variableType(),
                            v.required(), v.defaultValue(), v.sensitive(), v.ordinal()))
                    .toList();
            variableRepo.saveAll(variables);
        }

        activityLogger.logSuccess(DocumentHubEntityTypes.NATIVE_TEMPLATE, template.id(),
                DocumentHubActivityActions.TEMPLATE_CREATED, "Native template version published");

        return NativeTemplateVersionResponse.from(version);
    }
}
