package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_EMBEDDING_PROFILE)
@Getter @Setter @NoArgsConstructor
public class EmbeddingProfileJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String code;

    @Column(nullable = false, length = 40)
    private String provider;

    @Column(nullable = false, length = 120)
    private String model;

    @Column(nullable = false)
    private Integer dimensions;

    @Column(name = "max_input_tokens", nullable = false)
    private Integer maxInputTokens;

    @Column(name = "distance_metric", nullable = false, length = 20)
    private String distanceMetric;

    @Column(nullable = false, length = 40)
    private String normalization;

    @Column(name = "profile_version", nullable = false)
    private Integer profileVersion;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "non_secret_config", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String nonSecretConfig;

    @Version
    private Long version;
}
