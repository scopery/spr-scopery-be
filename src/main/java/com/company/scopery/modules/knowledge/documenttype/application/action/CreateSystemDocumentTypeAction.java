package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateSystemDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final KnowledgeActivityLogger activityLogger;

    public CreateSystemDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                          IamSystemAuthorizationService systemAuthorizationService,
                                          KnowledgeActivityLogger activityLogger) {
        this.documentTypeRepository = documentTypeRepository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentTypeResponse execute(CreateDocumentTypeCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());

        if (command.workspaceId() != null) {
            throw KnowledgeExceptions.documentTypeSystemScopeMustNotHaveWorkspaceId();
        }
        DocumentTypeCode code = DocumentTypeCode.of(command.code());
        if (documentTypeRepository.existsByCodeAndScopeSystem(code)) {
            throw KnowledgeExceptions.documentTypeCodeAlreadyExists(code.value());
        }
        DocumentType saved = documentTypeRepository.save(
                DocumentType.createSystem(code, command.name(), command.description()));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.CREATE_DOCUMENT_TYPE,
                "System document type created: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }
}
