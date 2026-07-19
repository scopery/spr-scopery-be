package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.MENTION)
@Getter @Setter @NoArgsConstructor
public class DocumentMentionJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "block_id", length = 128)
    private String blockId;

    @Column(name = "mention_type", nullable = false, length = 32)
    private String mentionType;

    @Column(name = "mentioned_resource_type", nullable = false, length = 128)
    private String mentionedResourceType;

    @Column(name = "mentioned_resource_id", nullable = false)
    private UUID mentionedResourceId;
}
