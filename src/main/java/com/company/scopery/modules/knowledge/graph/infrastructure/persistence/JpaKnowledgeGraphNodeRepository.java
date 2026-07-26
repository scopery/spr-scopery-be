package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeType;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNode;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNodeRepository;
import com.company.scopery.modules.knowledge.graph.infrastructure.mapper.KnowledgeGraphNodePersistenceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeGraphNodeRepository implements KnowledgeGraphNodeRepository {

    private final SpringDataKnowledgeGraphNodeJpaRepository springData;
    private final KnowledgeGraphNodePersistenceMapper mapper;
    private final ObjectMapper objectMapper;

    public JpaKnowledgeGraphNodeRepository(SpringDataKnowledgeGraphNodeJpaRepository springData,
                                            KnowledgeGraphNodePersistenceMapper mapper,
                                            ObjectMapper objectMapper) {
        this.springData = springData;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public KnowledgeGraphNode save(KnowledgeGraphNode node) {
        KnowledgeGraphNodeJpaEntity entity = mapper.toJpaEntity(node);
        KnowledgeGraphNodeJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void upsertNode(KnowledgeGraphNode node) {
        String aclTokens;
        try {
            aclTokens = objectMapper.writeValueAsString(
                    node.aclTokens() != null ? node.aclTokens() : List.of());
        } catch (Exception e) {
            aclTokens = "[]";
        }
        springData.upsertNode(
                node.id(), node.workspaceId(), node.projectId(),
                node.nodeType().name(),
                node.sourceRefId(), node.sourceVersionRefId(),
                node.title(), node.permissionSignature(),
                aclTokens, node.nodeStatus().name(),
                Instant.now());
    }

    @Override
    public Optional<KnowledgeGraphNode> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<KnowledgeGraphNode> findByRef(UUID workspaceId, GraphNodeType nodeType,
                                                   UUID sourceRefId, UUID sourceVersionRefId) {
        return springData.findByWorkspaceIdAndNodeTypeAndSourceRefIdAndSourceVersionRefId(
                workspaceId, nodeType.name(), sourceRefId, sourceVersionRefId
        ).map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeGraphNode> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
