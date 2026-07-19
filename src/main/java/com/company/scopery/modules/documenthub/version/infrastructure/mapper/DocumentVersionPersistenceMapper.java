package com.company.scopery.modules.documenthub.version.infrastructure.mapper;
import com.company.scopery.modules.documenthub.version.domain.enums.DocumentVersionStatus;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersion;
import com.company.scopery.modules.documenthub.version.infrastructure.persistence.DocumentVersionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DocumentVersionPersistenceMapper {
    public DocumentVersion toDomain(DocumentVersionJpaEntity e) {
        String ss = e.getStorageStatus();
        return new DocumentVersion(e.getId(), e.getDocumentId(), e.getProjectId(), e.getWorkspaceId(), e.getVersionNumber(), e.getStorageKey(),
                e.getFileName(), e.getContentType(), e.getFileSizeBytes(), e.getChecksum(), DocumentVersionStatus.valueOf(e.getStatus()),
                e.getChangeNotes(), e.getUploadedBy(), e.getUploadedAt(),
                e.getStorageProvider(), ss != null ? ss : "NOT_APPLICABLE",
                e.getStorageEtag(), e.getUploadCompletedAt(), e.getStorageVerifiedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentVersionJpaEntity toJpaEntity(DocumentVersion d) {
        DocumentVersionJpaEntity e = new DocumentVersionJpaEntity();
        e.setId(d.id()); e.setDocumentId(d.documentId()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setVersionNumber(d.versionNumber()); e.setStorageKey(d.storageKey()); e.setFileName(d.fileName());
        e.setContentType(d.contentType()); e.setFileSizeBytes(d.fileSizeBytes()); e.setChecksum(d.checksum());
        e.setStatus(d.status().name()); e.setChangeNotes(d.changeNotes()); e.setUploadedBy(d.uploadedBy()); e.setUploadedAt(d.uploadedAt());
        e.setStorageProvider(d.storageProvider());
        e.setStorageStatus(d.storageStatus() != null ? d.storageStatus() : "NOT_APPLICABLE");
        e.setStorageEtag(d.storageEtag());
        e.setUploadCompletedAt(d.uploadCompletedAt());
        e.setStorageVerifiedAt(d.storageVerifiedAt());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
