package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;

import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = IntegrationTableNames.SYNC_CURSOR)
@Getter
@Setter
@NoArgsConstructor
public class SyncCursorJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "sync_job_id", nullable = false) private UUID syncJobId;
    @Column(name = "cursor_key", nullable = false) private String cursorKey;
    @Column(name = "cursor_value", columnDefinition = "text") private String cursorValue;
    @Column(name = "last_successful_sync_at") private Instant lastSuccessfulSyncAt;
    @Version private Integer version;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
}
