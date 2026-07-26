package com.company.scopery.modules.documenthub.documentlink.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.LINK)
@Getter @Setter @NoArgsConstructor
public class DocumentLinkJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "target_type", nullable = false, length = 100)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "link_type", nullable = false, length = 50)
    private String linkType;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Version
    @Column(name = "version", nullable = false)
    private int version;
}
