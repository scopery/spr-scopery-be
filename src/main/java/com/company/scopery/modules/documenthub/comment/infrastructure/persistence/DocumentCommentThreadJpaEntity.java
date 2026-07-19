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
@Table(name = DocumentHubTableNames.COMMENT_THREAD)
@Getter @Setter @NoArgsConstructor
public class DocumentCommentThreadJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "block_id", length = 128)
    private String blockId;

    @Column(name = "anchor_text", columnDefinition = "text")
    private String anchorText;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
