package com.company.scopery.modules.servicesupport.supportcase.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.SUPPORT_CASE) @Getter @Setter @NoArgsConstructor
public class SupportCaseJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="case_number", nullable=false) private String caseNumber;
    @Column(name="request_type_code", nullable=false) private String requestTypeCode;
    @Column(nullable=false) private String source;
    @Column(nullable=false) private String priority;
    @Column(nullable=false) private String status;
    @Column(nullable=false) private String title;
    private String description;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(name="portal_visible", nullable=false) private boolean portalVisible;
    @Version private Integer version;
}
