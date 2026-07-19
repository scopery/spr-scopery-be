package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_SOURCE)
@Getter @Setter @NoArgsConstructor
public class KnowledgeSourceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "source_type", nullable = false) private String sourceType;
    @Column(name = "source_ref_id", nullable = false) private UUID sourceRefId;
    @Column(name = "source_version_ref_id", nullable = false) private UUID sourceVersionRefId;
    @Column(nullable = false, length = 500) private String title;
    @Column(nullable = false, length = 16) private String language;
    @Column(nullable = false, length = 32) private String classification;
    @Column(name = "content_hash", nullable = false, length = 64) private String contentHash;
    @Column(name = "permission_signature", nullable = false, length = 96) private String permissionSignature;
    @Column(name = "acl_tokens", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON) private String aclTokens;
    @Column(name = "source_status", nullable = false, length = 32) private String sourceStatus;
    @Column(name = "last_observed_at", nullable = false) private Instant lastObservedAt;
    @Column(name = "last_indexed_at") private Instant lastIndexedAt;
    @Version private Long version;
}
