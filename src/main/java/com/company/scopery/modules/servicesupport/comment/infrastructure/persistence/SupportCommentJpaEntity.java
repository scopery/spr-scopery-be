package com.company.scopery.modules.servicesupport.comment.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = SupportTableNames.COMMENT)
@Getter @Setter @NoArgsConstructor
public class SupportCommentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "support_case_id", nullable = false) private UUID supportCaseId;
    @Column(nullable = false) private String visibility;
    @Column(nullable = false, columnDefinition = "text") private String body;
    @Column(name = "author_user_id") private UUID authorUserId;
    @Version private Integer version;
}
