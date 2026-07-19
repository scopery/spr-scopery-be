package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Legacy system create endpoint — delegates to unified {@link CreateDocumentTypeAction}.
 */
@Component
public class CreateSystemDocumentTypeAction {

    private final CreateDocumentTypeAction createDocumentTypeAction;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public CreateSystemDocumentTypeAction(CreateDocumentTypeAction createDocumentTypeAction,
                                          IamSystemAuthorizationService systemAuthorizationService) {
        this.createDocumentTypeAction = createDocumentTypeAction;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional
    public DocumentTypeResponse execute(CreateDocumentTypeCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        if (command.workspaceId() != null || command.organizationId() != null) {
            throw KnowledgeExceptions.documentTypeSystemScopeMustNotHaveWorkspaceId();
        }
        return createDocumentTypeAction.execute(new CreateDocumentTypeCommand(
                command.code(), command.name(), command.description(), "SYSTEM",
                null, null, command.category(), command.defaultClassification(),
                command.defaultReviewCycleDays(), command.defaultTemplateCode(),
                command.metadataSchemaJson()));
    }
}
