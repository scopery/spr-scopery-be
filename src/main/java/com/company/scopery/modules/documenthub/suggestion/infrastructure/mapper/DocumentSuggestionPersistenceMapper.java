package com.company.scopery.modules.documenthub.suggestion.infrastructure.mapper;

import com.company.scopery.modules.documenthub.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestion;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperation;
import com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence.DocumentSuggestionJpaEntity;
import com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence.DocumentSuggestionOperationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentSuggestionPersistenceMapper {

    public DocumentSuggestion toDomain(DocumentSuggestionJpaEntity e) {
        return new DocumentSuggestion(e.getId(), e.getDocumentId(), e.getWorkspaceId(), e.getProjectId(),
                e.getTargetRevisionNo(), e.getDescription(), SuggestionStatus.valueOf(e.getStatus()),
                e.getAcceptedBy(), e.getAcceptedAt(), e.getAcceptedRevisionNo(),
                e.getRejectedBy(), e.getRejectedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentSuggestionJpaEntity toJpaEntity(DocumentSuggestion d) {
        DocumentSuggestionJpaEntity e = new DocumentSuggestionJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setTargetRevisionNo(d.targetRevisionNo());
        e.setDescription(d.description());
        e.setStatus(d.status().name());
        e.setAcceptedBy(d.acceptedBy());
        e.setAcceptedAt(d.acceptedAt());
        e.setAcceptedRevisionNo(d.acceptedRevisionNo());
        e.setRejectedBy(d.rejectedBy());
        e.setRejectedAt(d.rejectedAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }

    public DocumentSuggestionOperation toOperationDomain(DocumentSuggestionOperationJpaEntity e) {
        return new DocumentSuggestionOperation(e.getId(), e.getSuggestionId(), e.getOpType(),
                e.getBlockId(), e.getPath(), e.getValue(), e.getOrdinal(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentSuggestionOperationJpaEntity toOperationJpaEntity(DocumentSuggestionOperation d) {
        DocumentSuggestionOperationJpaEntity e = new DocumentSuggestionOperationJpaEntity();
        e.setId(d.id());
        e.setSuggestionId(d.suggestionId());
        e.setOpType(d.opType());
        e.setBlockId(d.blockId());
        e.setPath(d.path());
        e.setValue(d.value());
        e.setOrdinal(d.ordinal());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
