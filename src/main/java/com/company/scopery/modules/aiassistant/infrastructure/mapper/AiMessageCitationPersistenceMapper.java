package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.model.AiMessageCitation;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiMessageCitationJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiMessageCitationPersistenceMapper {

    private final ObjectMapper objectMapper;

    public AiMessageCitationPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiMessageCitationJpaEntity toJpaEntity(AiMessageCitation domain) {
        AiMessageCitationJpaEntity entity = new AiMessageCitationJpaEntity();
        entity.setId(domain.id());
        entity.setMessageId(domain.messageId());
        entity.setOrdinal(domain.ordinal());
        entity.setRetrievalTraceId(domain.retrievalTraceId());
        entity.setKnowledgeChunkId(domain.knowledgeChunkId());
        entity.setSourceType(domain.sourceType());
        entity.setSourceRefId(domain.sourceRefId());
        entity.setSourceVersionRefId(domain.sourceVersionRefId());
        entity.setTitle(domain.title());
        entity.setHeadingPath(toJson(domain.headingPath()));
        entity.setQuotedFragment(domain.quotedFragment());
        entity.setAppRoute(domain.appRoute());
        entity.setPermissionSignature(domain.permissionSignature());
        entity.setAccessValidationResult(domain.accessValidationResult());
        entity.setAccessValidatedAt(domain.accessValidatedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiMessageCitation toDomain(AiMessageCitationJpaEntity entity) {
        return AiMessageCitation.reconstitute(
                entity.getId(),
                entity.getMessageId(),
                entity.getOrdinal(),
                entity.getRetrievalTraceId(),
                entity.getKnowledgeChunkId(),
                entity.getSourceType(),
                entity.getSourceRefId(),
                entity.getSourceVersionRefId(),
                entity.getTitle(),
                fromJson(entity.getHeadingPath()),
                entity.getQuotedFragment(),
                entity.getAppRoute(),
                entity.getPermissionSignature(),
                entity.getAccessValidationResult(),
                entity.getAccessValidatedAt(),
                entity.getCreatedAt()
        );
    }

    private String toJson(List<String> list) {
        if (list == null) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
