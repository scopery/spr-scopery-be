package com.company.scopery.modules.knowledge.graph.infrastructure.mapper;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeStatus;
import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeType;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNode;
import com.company.scopery.modules.knowledge.graph.infrastructure.persistence.KnowledgeGraphNodeJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class KnowledgeGraphNodePersistenceMapper {

    private static final TypeReference<List<String>> LIST_STRING_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public KnowledgeGraphNodePersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KnowledgeGraphNode toDomain(KnowledgeGraphNodeJpaEntity entity) {
        return new KnowledgeGraphNode(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                GraphNodeType.valueOf(entity.getNodeType()),
                entity.getSourceRefId(),
                entity.getSourceVersionRefId(),
                entity.getTitle(),
                entity.getPermissionSignature(),
                parseAclTokens(entity.getAclTokens()),
                GraphNodeStatus.valueOf(entity.getNodeStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion() != null ? entity.getVersion() : 0L
        );
    }

    public KnowledgeGraphNodeJpaEntity toJpaEntity(KnowledgeGraphNode domain) {
        KnowledgeGraphNodeJpaEntity entity = new KnowledgeGraphNodeJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setNodeType(domain.nodeType().name());
        entity.setSourceRefId(domain.sourceRefId());
        entity.setSourceVersionRefId(domain.sourceVersionRefId());
        entity.setTitle(domain.title());
        entity.setPermissionSignature(domain.permissionSignature());
        entity.setAclTokens(serializeAclTokens(domain.aclTokens()));
        entity.setNodeStatus(domain.nodeStatus().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            if (domain.version() > 0) {
                entity.setVersion(domain.version());
            }
        }
        return entity;
    }

    private List<String> parseAclTokens(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, LIST_STRING_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private String serializeAclTokens(List<String> aclTokens) {
        try {
            return objectMapper.writeValueAsString(aclTokens != null ? aclTokens : Collections.emptyList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
