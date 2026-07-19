package com.company.scopery.modules.resourcereference.resourcetype.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = ResourceReferenceTableNames.RESOURCE_TYPE,
        uniqueConstraints = @UniqueConstraint(name = "uq_resourceref_resource_type_code", columnNames = "code"))
@Getter @Setter @NoArgsConstructor
public class MentionResourceTypeJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 128)
    private String code;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(length = 1000)
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem;

    @Column(nullable = false)
    private boolean enabled;
}
