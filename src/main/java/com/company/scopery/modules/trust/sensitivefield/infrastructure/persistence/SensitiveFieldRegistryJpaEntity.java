package com.company.scopery.modules.trust.sensitivefield.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TrustTableNames.SENSITIVE_FIELD_REGISTRY)
@Getter
@Setter
@NoArgsConstructor
public class SensitiveFieldRegistryJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "object_type_code", nullable = false) private String objectTypeCode;
    @Column(name = "field_path", nullable = false) private String fieldPath;
    @Column(nullable = false) private String classification;
    @Column(name = "masking_strategy", nullable = false) private String maskingStrategy;
    @Column(name = "access_logging_required", nullable = false) private boolean accessLoggingRequired = true;
    @Column(name = "search_index_allowed", nullable = false) private boolean searchIndexAllowed;
    @Column(name = "export_allowed", nullable = false) private boolean exportAllowed;
    @Column(nullable = false) private boolean enabled = true;
    @Version private Integer version;
}
