package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.USE_CASE)
@Getter
@Setter
@NoArgsConstructor
public class UseCaseJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "primary_function_id")
    private UUID primaryFunctionId;

    @Column(nullable = false, length = 50)
    private String key;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(columnDefinition = "text")
    private String goal;

    @Column(name = "primary_actor_name", length = 255)
    private String primaryActorName;

    @Column(name = "trigger_text", columnDefinition = "text")
    private String triggerText;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "completeness_status", nullable = false, length = 30)
    private String completenessStatus;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Version
    private Integer version;
}
