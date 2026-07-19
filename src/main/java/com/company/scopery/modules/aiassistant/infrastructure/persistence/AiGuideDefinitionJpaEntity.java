package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAssistantTableNames.GUIDE_DEFINITION,
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_aiassistant_guide_definition_code", columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_aiassistant_guide_definition_page_code", columnList = "page_code"),
        @Index(name = "idx_aiassistant_guide_definition_status", columnList = "status"),
        @Index(name = "idx_aiassistant_guide_definition_locale", columnList = "locale")
    }
)
public class AiGuideDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 200, updatable = false)
    private String code;

    @Column(name = "page_code", nullable = false, length = 100, updatable = false)
    private String pageCode;

    @Column(name = "field_code", length = 100)
    private String fieldCode;

    @Column(name = "action_code", length = 100)
    private String actionCode;

    @Column(name = "locale", nullable = false, length = 20, updatable = false)
    private String locale;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "body_markdown", nullable = false, columnDefinition = "TEXT")
    private String bodyMarkdown;

    @Column(name = "metadata_version", nullable = false)
    private int metadataVersion;

    @Column(name = "source_kind", nullable = false, length = 50)
    private String sourceKind;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
