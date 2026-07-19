package com.company.scopery.modules.documenthub.share.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = DocumentHubTableNames.SHARE) @Getter @Setter @NoArgsConstructor
public class DocumentShareJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="document_id", nullable=false) private UUID documentId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="share_type", nullable=false) private String shareType;
    @Column(name="grantee_type", nullable=false) private String granteeType;
    @Column(name="grantee_id") private UUID granteeId;
    @Column(name="expires_at") private Instant expiresAt;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
