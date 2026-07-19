package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Legacy workspace create endpoint — delegates to unified {@link CreateDocumentTypeAction}.
 */
@Component
public class CreateWorkspaceDocumentTypeAction {

    private final CreateDocumentTypeAction createDocumentTypeAction;

    public CreateWorkspaceDocumentTypeAction(CreateDocumentTypeAction createDocumentTypeAction) {
        this.createDocumentTypeAction = createDocumentTypeAction;
    }

    @Transactional
    public DocumentTypeResponse execute(CreateDocumentTypeCommand command) {
        if (command.workspaceId() == null) {
            throw KnowledgeExceptions.documentTypeWorkspaceScopeRequiresWorkspaceId();
        }
        return createDocumentTypeAction.execute(new CreateDocumentTypeCommand(
                command.code(), command.name(), command.description(), "WORKSPACE",
                command.organizationId(), command.workspaceId(), command.category(),
                command.defaultClassification(), command.defaultReviewCycleDays(),
                command.defaultTemplateCode(), command.metadataSchemaJson()));
    }
}
