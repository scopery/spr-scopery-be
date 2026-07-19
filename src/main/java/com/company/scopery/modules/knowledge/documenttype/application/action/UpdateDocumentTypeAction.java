package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.application.support.DocumentTypePlatformPublisher;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.util.KnowledgeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
public class UpdateDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypePlatformPublisher platformPublisher;

    public UpdateDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                    CurrentUserAuthorizationService currentUserAuthorizationService,
                                    WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                    IamSystemAuthorizationService systemAuthorizationService,
                                    KnowledgeActivityLogger activityLogger,
                                    DocumentTypePlatformPublisher platformPublisher) {
        this.documentTypeRepository = documentTypeRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public DocumentTypeResponse execute(UpdateDocumentTypeCommand command) {
        DocumentType dt = findOrThrow(command.id());
        if (dt.isArchived()) {
            throw KnowledgeExceptions.documentTypeArchivedCannotBeModified(dt.id());
        }
        requireManageAccess(dt, IamAuthorities.DOCUMENT_TYPE_UPDATE);

        if (!platformPublisher.isValidJson(command.metadataSchemaJson())) {
            throw KnowledgeExceptions.documentTypeInvalidMetadataSchema();
        }

        DocumentClassification classification = command.defaultClassification() != null
                ? parseClassification(command.defaultClassification())
                : dt.defaultClassification();

        String category = command.category() != null ? command.category() : dt.category();
        Integer reviewDays = command.defaultReviewCycleDays() != null
                ? command.defaultReviewCycleDays() : dt.defaultReviewCycleDays();
        String templateCode = command.defaultTemplateCode() != null
                ? command.defaultTemplateCode() : dt.defaultTemplateCode();
        String metadataSchema = command.metadataSchemaJson() != null
                ? command.metadataSchemaJson() : dt.metadataSchemaJson();

        DocumentType updated = dt.update(command.name(), command.description(), category,
                classification, reviewDays, templateCode, metadataSchema);
        DocumentType saved = documentTypeRepository.save(updated);

        UUID actorId = resolveActorIdSafe();
        if (!Objects.equals(
                dt.defaultClassification() != null ? dt.defaultClassification().name() : null,
                saved.defaultClassification() != null ? saved.defaultClassification().name() : null)) {
            platformPublisher.auditClassificationChanged(actorId, dt, saved);
        }
        if (!Objects.equals(dt.metadataSchemaJson(), saved.metadataSchemaJson())) {
            platformPublisher.auditSchemaChanged(actorId, dt, saved);
        }

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.UPDATE_DOCUMENT_TYPE,
                "Document type updated: " + saved.code().value());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_UPDATED");
        return DocumentTypeResponse.from(saved);
    }

    private DocumentClassification parseClassification(String raw) {
        try {
            return KnowledgeEnumParser.parseRequired(
                    DocumentClassification.class, raw,
                    KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_CLASSIFICATION.code(),
                    "defaultClassification");
        } catch (Exception e) {
            throw KnowledgeExceptions.documentTypeInvalidClassification(raw);
        }
    }

    private void requireManageAccess(DocumentType dt, IamPermissionAction workspaceAuthority) {
        if (dt.isSystem() || dt.isOrganization()) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        } else {
            UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(dt.workspaceId(), actorId, workspaceAuthority);
        }
    }

    private UUID resolveActorIdSafe() {
        try {
            return currentUserAuthorizationService.resolveCurrentUser().id();
        } catch (Exception e) {
            return null;
        }
    }

    private DocumentType findOrThrow(UUID id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
    }
}
