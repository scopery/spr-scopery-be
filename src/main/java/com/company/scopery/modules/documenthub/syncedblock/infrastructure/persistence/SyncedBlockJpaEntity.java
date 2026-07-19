package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.SYNCED_BLOCK)
@Getter
@Setter
@NoArgsConstructor
public class SyncedBlockJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "title")
    private String title;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "current_revision_no", nullable = false)
    private long currentRevisionNo;

    @Column(name = "schema_version")
    private Integer schemaVersion;
}
