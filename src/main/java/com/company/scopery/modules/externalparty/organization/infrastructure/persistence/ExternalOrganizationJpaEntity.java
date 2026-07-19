package com.company.scopery.modules.externalparty.organization.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ExternalPartyTableNames.ORGANIZATION) @Getter @Setter @NoArgsConstructor
public class ExternalOrganizationJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column private String code;
    @Column(nullable=false) private String name;
    @Column(name="organization_type", nullable=false) private String organizationType;
    @Column(nullable=false) private String status;
    @Column(name="tax_id") private String taxId;
    @Column private String website;
    @Column(columnDefinition="text") private String notes;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
