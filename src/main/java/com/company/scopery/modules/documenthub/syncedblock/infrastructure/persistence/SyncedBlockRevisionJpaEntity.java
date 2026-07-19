package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.SYNCED_BLOCK_REVISION,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_documenthub_synced_block_revision",
                columnNames = {"synced_block_id", "revision_no"}))
@Getter
@Setter
@NoArgsConstructor
public class SyncedBlockRevisionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "synced_block_id", nullable = false, updatable = false)
    private UUID syncedBlockId;

    @Column(name = "revision_no", nullable = false, updatable = false)
    private long revisionNo;

    @Column(name = "ast", nullable = false, updatable = false, columnDefinition = "jsonb")
    private String ast;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;
}
