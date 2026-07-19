package com.company.scopery.modules.documenthub.template.application.action;
import com.company.scopery.modules.documenthub.template.application.command.CreateDocumentTemplateCommand;
import com.company.scopery.modules.documenthub.template.application.response.DocumentTemplateResponse;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplate;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDocumentTemplateAction {
    private final DocumentTemplateRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    public CreateDocumentTemplateAction(DocumentTemplateRepository repo, DocumentHubAuthorizationService authorization, DocumentHubActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DocumentTemplateResponse execute(CreateDocumentTemplateCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var saved = repo.save(DocumentTemplate.create(c.workspaceId(), c.code().trim(), c.name().trim(), c.description(), c.category()));
        activityLogger.logSuccess(DocumentHubEntityTypes.TEMPLATE, saved.id(), DocumentHubActivityActions.TEMPLATE_CREATED, "Template created");
        return DocumentTemplateResponse.from(saved);
    }
}
