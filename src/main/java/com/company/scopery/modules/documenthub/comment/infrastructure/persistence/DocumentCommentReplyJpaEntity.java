package com.company.scopery.modules.documenthub.comment.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.COMMENT_REPLY)
@Getter @Setter @NoArgsConstructor
public class DocumentCommentReplyJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Column(name = "thread_id", nullable = false)
    private UUID threadId;

    @Column(name = "body", nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
