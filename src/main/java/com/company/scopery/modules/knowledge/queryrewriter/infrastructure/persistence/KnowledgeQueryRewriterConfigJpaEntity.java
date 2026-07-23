package com.company.scopery.modules.knowledge.queryrewriter.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_QUERY_REWRITER_CONFIG)
@Getter @Setter @NoArgsConstructor
public class KnowledgeQueryRewriterConfigJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 100)
    private String provider;

    @Column(length = 200)
    private String model;

    @Column(name = "max_tokens", nullable = false)
    private int maxTokens;

    @Column(name = "prompt_template", columnDefinition = "text")
    private String promptTemplate;

    @Version
    private Long version;
}
