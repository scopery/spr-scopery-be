package com.company.scopery.modules.servicesupport.queue.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = SupportTableNames.SUPPORT_QUEUE)
@Getter @Setter @NoArgsConstructor
public class SupportQueueJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "queue_code", nullable = false) private String queueCode;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
