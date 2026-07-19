package com.company.scopery.modules.trust.sensitiveobject.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TrustTableNames.SENSITIVE_OBJECT_REGISTRY)
@Getter
@Setter
@NoArgsConstructor
public class SensitiveObjectRegistryJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "object_type_code", nullable = false) private String objectTypeCode;
    @Column(nullable = false) private String classification;
    @Column(name = "access_logging_required", nullable = false) private boolean accessLoggingRequired = true;
    @Column(name = "export_reason_required", nullable = false) private boolean exportReasonRequired;
    @Column(name = "search_index_allowed", nullable = false) private boolean searchIndexAllowed;
    @Column(nullable = false) private boolean enabled = true;
    @Version private Integer version;
}
