package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_INDEX_DEFINITION)
@Getter @Setter @NoArgsConstructor
public class KnowledgeIndexDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String code;

    @Column(nullable = false, length = 40)
    private String environment;

    @Column(name = "index_family", nullable = false, length = 120)
    private String indexFamily;

    @Column(name = "schema_version", nullable = false, length = 20)
    private String schemaVersion;

    @Column(name = "embedding_profile_id", nullable = false)
    private UUID embeddingProfileId;

    @Column(name = "chunk_strategy_version", nullable = false, length = 40)
    private String chunkStrategyVersion;

    @Column(name = "read_alias", nullable = false, length = 255)
    private String readAlias;

    @Column(name = "write_alias", nullable = false, length = 255)
    private String writeAlias;

    @Column(name = "active_generation", length = 20)
    private String activeGeneration;

    @Column(name = "active_concrete_index", length = 255)
    private String activeConcreteIndex;

    @Column(name = "mapping_hash", length = 64)
    private String mappingHash;

    @Column(name = "definition_status", nullable = false, length = 24)
    private String definitionStatus;

    @Version
    private Long version;
}
