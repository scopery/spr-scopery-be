package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.SCHEMA_DEFINITION)
public class AiActionSchemaDefinitionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "schema_code", nullable = false, length = 100)
    private String schemaCode;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "schema_json", nullable = false, columnDefinition = "TEXT")
    private String schemaJson;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
