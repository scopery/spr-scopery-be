package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence.entity;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAgentTableNames.PROMPT_VERSION,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_prompt_version_template_id_version_number",
        columnNames = {"template_id", "version_number"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_prompt_version_template_id",    columnList = "template_id"),
        @Index(name = "idx_aiagent_prompt_version_status",         columnList = "status"),
        @Index(name = "idx_aiagent_prompt_version_content_format", columnList = "content_format"),
        @Index(name = "idx_aiagent_prompt_version_version_number", columnList = "version_number")
    }
)
public class PromptVersionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_id", nullable = false, updatable = false)
    private UUID templateId;

    @Column(name = "version_number", nullable = false, updatable = false)
    private int versionNumber;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_format", nullable = false, length = 50)
    private String contentFormat;

    @Column(name = "variable_schema", columnDefinition = "TEXT")
    private String variableSchema;

    @Column(name = "change_note", columnDefinition = "TEXT")
    private String changeNote;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(name = "user_prompt_template", columnDefinition = "TEXT")
    private String userPromptTemplate;

    @Column(name = "response_format", length = 100)
    private String responseFormat;

    @Column(name = "response_schema_json", columnDefinition = "TEXT")
    private String responseSchemaJson;

    @Column(name = "temperature", precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(name = "top_p", precision = 5, scale = 2)
    private BigDecimal topP;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "activated_by", length = 100)
    private String activatedBy;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}
