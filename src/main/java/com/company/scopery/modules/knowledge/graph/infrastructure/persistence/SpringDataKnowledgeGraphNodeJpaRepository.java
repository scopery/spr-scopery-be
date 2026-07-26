package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeGraphNodeJpaRepository extends JpaRepository<KnowledgeGraphNodeJpaEntity, UUID> {
    List<KnowledgeGraphNodeJpaEntity> findByWorkspaceId(UUID workspaceId);
    Optional<KnowledgeGraphNodeJpaEntity> findByWorkspaceIdAndNodeTypeAndSourceRefIdAndSourceVersionRefId(
            UUID workspaceId, String nodeType, UUID sourceRefId, UUID sourceVersionRefId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT INTO knowledge_graph_node
                (id, workspace_id, project_id, node_type, source_ref_id, source_version_ref_id,
                 title, permission_signature, acl_tokens, node_status, version, created_at, updated_at,
                 created_by, updated_by)
            VALUES (:id, :workspaceId, :projectId, :nodeType, :sourceRefId, :sourceVersionRefId,
                    :title, :permSig, CAST(:aclTokens AS jsonb), :nodeStatus, 0, :now, :now, 'SYSTEM', 'SYSTEM')
            ON CONFLICT (workspace_id, node_type, source_ref_id, source_version_ref_id)
            DO UPDATE SET
                title               = EXCLUDED.title,
                permission_signature = EXCLUDED.permission_signature,
                acl_tokens          = EXCLUDED.acl_tokens,
                node_status         = EXCLUDED.node_status,
                updated_at          = EXCLUDED.updated_at,
                updated_by          = 'SYSTEM'
            """, nativeQuery = true)
    void upsertNode(@Param("id") UUID id,
                    @Param("workspaceId") UUID workspaceId,
                    @Param("projectId") UUID projectId,
                    @Param("nodeType") String nodeType,
                    @Param("sourceRefId") UUID sourceRefId,
                    @Param("sourceVersionRefId") UUID sourceVersionRefId,
                    @Param("title") String title,
                    @Param("permSig") String permSig,
                    @Param("aclTokens") String aclTokens,
                    @Param("nodeStatus") String nodeStatus,
                    @Param("now") Instant now);
}
