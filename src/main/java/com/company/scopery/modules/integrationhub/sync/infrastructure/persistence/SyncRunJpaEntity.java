package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = IntegrationTableNames.SYNC_RUN)
@Getter
@Setter
@NoArgsConstructor
public class SyncRunJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "sync_job_id", nullable = false) private UUID syncJobId;
    @Column(nullable = false) private String status;
    @Column(name = "started_at") private Instant startedAt;
    @Column(name = "completed_at") private Instant completedAt;
    @Column(name = "processed_count", nullable = false) private Long processedCount = 0L;
    @Version private Integer version;
}
