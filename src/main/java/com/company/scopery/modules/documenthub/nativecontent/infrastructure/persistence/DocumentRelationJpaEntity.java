package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.RELATION)
@Getter @Setter @NoArgsConstructor
public class DocumentRelationJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "source_document_id", nullable = false)
    private UUID sourceDocumentId;

    @Column(name = "target_document_id", nullable = false)
    private UUID targetDocumentId;

    @Column(name = "relation_type", nullable = false, length = 64)
    private String relationType;

    @Column(name = "block_id", length = 128)
    private String blockId;
}
