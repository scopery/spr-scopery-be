package com.company.scopery.modules.traceability.specdocrevision.infrastructure.persistence;

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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.SPEC_DOC_REVISION)
@Getter
@Setter
@NoArgsConstructor
public class RegistrySpecDocRevisionJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "revision_no", nullable = false, length = 50)
    private String revisionNo;

    @Column(name = "target_sheet_name", length = 255)
    private String targetSheetName;

    @Column(name = "details", nullable = false, columnDefinition = "TEXT")
    private String details;

    @Column(name = "person_in_charge", length = 255)
    private String personInCharge;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "changed_at")
    private LocalDate changedAt;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(nullable = false, length = 50)
    private String status;

    @Version
    private Integer version;
}
