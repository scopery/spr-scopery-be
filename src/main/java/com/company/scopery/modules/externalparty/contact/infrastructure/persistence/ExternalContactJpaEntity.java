package com.company.scopery.modules.externalparty.contact.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ExternalPartyTableNames.CONTACT) @Getter @Setter @NoArgsConstructor
public class ExternalContactJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="organization_id") private UUID organizationId;
    @Column(name="first_name", nullable=false) private String firstName;
    @Column(name="last_name", nullable=false) private String lastName;
    @Column private String email;
    @Column private String phone;
    @Column private String title;
    @Column(nullable=false) private String status;
    @Column(name="primary_flag", nullable=false) private Boolean primaryFlag;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
