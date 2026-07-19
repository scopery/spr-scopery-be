package com.company.scopery.modules.clientportal.feedback.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.FEEDBACK) @Getter @Setter @NoArgsConstructor
public class ClientFeedbackJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String category;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String body;
    @Column(nullable=false) private String status;
    @Column(name="submitted_by_portal_account_id") private UUID submittedByPortalAccountId;
    @Version private Integer version;
}
