package com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.SUGGESTION_OPERATION)
@Getter @Setter @NoArgsConstructor
public class DocumentSuggestionOperationJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "op_type", nullable = false, length = 32)
    private String opType;

    @Column(name = "block_id", length = 128)
    private String blockId;

    @Column(name = "path", length = 500)
    private String path;

    @Column(columnDefinition = "jsonb")
    private String value;

    @Column(nullable = false)
    private int ordinal;
}
