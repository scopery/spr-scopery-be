package com.company.scopery.modules.documenthub.attachment.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.ATTACHMENT)
@Getter @Setter @NoArgsConstructor
public class DocumentAttachmentJpaEntity extends AuditableJpaEntity {

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

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "media_type", length = 255)
    private String mediaType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "object_key", length = 1000)
    private String objectKey;

    @Column(name = "storage_status", nullable = false, length = 32)
    private String storageStatus;

    @Column(name = "checksum", length = 128)
    private String checksum;

    @Column(name = "presigned_url", length = 2000)
    private String presignedUrl;

    @Column(name = "presigned_expires_at")
    private Instant presignedExpiresAt;
}
