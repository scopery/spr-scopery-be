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
    name = AiAssistantTableNames.SUGGESTED_QUESTION,
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_aiassistant_suggested_question_code", columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_aiassistant_suggested_question_page_code", columnList = "page_code"),
        @Index(name = "idx_aiassistant_suggested_question_status", columnList = "status"),
        @Index(name = "idx_aiassistant_suggested_question_locale", columnList = "locale"),
        @Index(name = "idx_aiassistant_suggested_question_display_order", columnList = "display_order")
    }
)
public class AiSuggestedQuestionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 200, updatable = false)
    private String code;

    @Column(name = "page_code", nullable = false, length = 100, updatable = false)
    private String pageCode;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "action_code", length = 100)
    private String actionCode;

    @Column(name = "locale", nullable = false, length = 20, updatable = false)
    private String locale;

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
