package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.SYNCED_BLOCK_REFERENCE,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_documenthub_synced_block_reference",
                columnNames = {"synced_block_id", "document_id"}))
@Getter
@Setter
@NoArgsConstructor
public class SyncedBlockReferenceJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "synced_block_id", nullable = false, updatable = false)
    private UUID syncedBlockId;

    @Column(name = "document_id", nullable = false, updatable = false)
    private UUID documentId;
}
