package com.company.scopery.modules.externalparty.authority.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = ExternalPartyTableNames.APPROVAL_AUTHORITY) @Getter @Setter @NoArgsConstructor
public class ProjectApprovalAuthorityJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="stakeholder_id", nullable=false) private UUID stakeholderId;
    @Column(name="authority_type", nullable=false) private String authorityType;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String notes;
    @Version private Integer version;
}
