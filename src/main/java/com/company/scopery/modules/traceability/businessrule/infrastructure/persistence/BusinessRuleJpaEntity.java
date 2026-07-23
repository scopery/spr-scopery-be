package com.company.scopery.modules.traceability.businessrule.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.BUSINESS_RULE)
@Getter
@Setter
@NoArgsConstructor
public class BusinessRuleJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "functional_item_id", nullable = false)
    private UUID functionalItemId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "status", nullable = false)
    private String status;

    @Version
    @Column(name = "version")
    private Integer version;
}
