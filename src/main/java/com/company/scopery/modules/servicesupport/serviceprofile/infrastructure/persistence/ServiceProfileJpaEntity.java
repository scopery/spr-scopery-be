package com.company.scopery.modules.servicesupport.serviceprofile.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = SupportTableNames.SERVICE_PROFILE)
@Getter @Setter @NoArgsConstructor
public class ServiceProfileJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "scope_type", nullable = false) private String scopeType;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "portal_intake_enabled", nullable = false) private boolean portalIntakeEnabled;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
