package com.company.scopery.modules.knowledge.documenttypefield.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.ReorderDocumentTypeFieldsCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldAccessGuard;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldPlatformPublisher;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReorderDocumentTypeFieldsAction {

    private final DocumentTypeFieldRepository fieldRepository;
    private final DocumentTypeFieldAccessGuard accessGuard;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypeFieldPlatformPublisher platformPublisher;

    public ReorderDocumentTypeFieldsAction(DocumentTypeFieldRepository fieldRepository,
                                           DocumentTypeFieldAccessGuard accessGuard,
                                           KnowledgeActivityLogger activityLogger,
                                           DocumentTypeFieldPlatformPublisher platformPublisher) {
        this.fieldRepository = fieldRepository;
        this.accessGuard = accessGuard;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public List<DocumentTypeFieldResponse> execute(ReorderDocumentTypeFieldsCommand command) {
        DocumentType dt = accessGuard.requireDocumentType(command.documentTypeId());
        accessGuard.requireWrite(dt, IamAuthorities.DOCUMENT_TYPE_FIELD_UPDATE);

        List<DocumentTypeField> existing = fieldRepository.findByDocumentTypeId(command.documentTypeId());
        Map<UUID, DocumentTypeField> byId = existing.stream()
                .collect(Collectors.toMap(DocumentTypeField::id, Function.identity()));

        if (command.orderedFieldIds() == null || command.orderedFieldIds().isEmpty()) {
            throw KnowledgeExceptions.documentTypeFieldReorderInvalid("orderedFieldIds must not be empty");
        }
        Set<UUID> ordered = new HashSet<>(command.orderedFieldIds());
        if (ordered.size() != command.orderedFieldIds().size()) {
            throw KnowledgeExceptions.documentTypeFieldReorderInvalid("orderedFieldIds contains duplicates");
        }
        if (!ordered.equals(byId.keySet())) {
            throw KnowledgeExceptions.documentTypeFieldReorderInvalid(
                    "orderedFieldIds must include every field of the document type exactly once");
        }

        List<DocumentTypeField> reordered = new ArrayList<>();
        int order = 0;
        for (UUID fieldId : command.orderedFieldIds()) {
            reordered.add(byId.get(fieldId).withDisplayOrder(order++));
        }
        fieldRepository.saveAll(reordered);

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, command.documentTypeId(),
                KnowledgeActivityActions.REORDER_DOCUMENT_TYPE_FIELDS,
                "Document type fields reordered for type " + command.documentTypeId());
        platformPublisher.enqueueReorder(command.documentTypeId(),
                Map.of("documentTypeId", command.documentTypeId(), "orderedFieldIds", command.orderedFieldIds()));

        return reordered.stream().map(DocumentTypeFieldResponse::from).toList();
    }
}
