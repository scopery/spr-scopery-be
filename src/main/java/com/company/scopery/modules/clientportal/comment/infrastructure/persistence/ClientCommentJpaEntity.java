package com.company.scopery.modules.clientportal.comment.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.COMMENT) @Getter @Setter @NoArgsConstructor
public class ClientCommentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(nullable=false, columnDefinition="text") private String body;
    @Column(name="author_portal_account_id") private UUID authorPortalAccountId;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
