package com.company.scopery.modules.documenthub.attachment.infrastructure.mapper;

import com.company.scopery.modules.documenthub.attachment.domain.enums.AttachmentStorageStatus;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachment;
import com.company.scopery.modules.documenthub.attachment.infrastructure.persistence.DocumentAttachmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentAttachmentPersistenceMapper {

    public DocumentAttachment toDomain(DocumentAttachmentJpaEntity e) {
        return new DocumentAttachment(e.getId(), e.getDocumentId(), e.getWorkspaceId(), e.getProjectId(),
                e.getBlockId(), e.getFileName(), e.getMediaType(), e.getFileSizeBytes(), e.getObjectKey(),
                AttachmentStorageStatus.valueOf(e.getStorageStatus()), e.getChecksum(),
                e.getPresignedUrl(), e.getPresignedExpiresAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentAttachmentJpaEntity toJpaEntity(DocumentAttachment d) {
        DocumentAttachmentJpaEntity e = new DocumentAttachmentJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setBlockId(d.blockId());
        e.setFileName(d.fileName());
        e.setMediaType(d.mediaType());
        e.setFileSizeBytes(d.fileSizeBytes());
        e.setObjectKey(d.objectKey());
        e.setStorageStatus(d.storageStatus().name());
        e.setChecksum(d.checksum());
        e.setPresignedUrl(d.presignedUrl());
        e.setPresignedExpiresAt(d.presignedExpiresAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
