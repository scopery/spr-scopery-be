package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.BLOCK_INDEX,
        uniqueConstraints = @UniqueConstraint(name = "uq_documenthub_block_index_doc_block", columnNames = {"document_id", "block_id"}))
@Getter @Setter @NoArgsConstructor
public class DocumentBlockIndexJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "block_id", nullable = false, length = 128)
    private String blockId;

    @Column(name = "block_type", nullable = false, length = 64)
    private String blockType;

    @Column(name = "heading_level")
    private Integer headingLevel;

    @Column(name = "heading_text", length = 1000)
    private String headingText;

    @Column(name = "plain_text", columnDefinition = "text")
    private String plainText;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;
}
