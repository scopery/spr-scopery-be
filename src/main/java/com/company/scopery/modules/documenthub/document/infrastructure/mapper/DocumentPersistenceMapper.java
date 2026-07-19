package com.company.scopery.modules.documenthub.document.infrastructure.mapper;

import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.infrastructure.persistence.DocumentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentPersistenceMapper {

    public Document toDomain(DocumentJpaEntity e) {
        return new Document(
                e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getFolderId(),
                e.getDocumentTypeCode(), e.getCode(), e.getTitle(), e.getDescription(),
                DocumentStatus.valueOf(e.getStatus()), e.getClassification(), e.getCurrentVersionId(),
                e.isLocked(), e.getApprovedAt(), e.getApprovedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt(),
                e.getContentMode() != null ? ContentMode.valueOf(e.getContentMode()) : ContentMode.FILE,
                e.getParentDocumentId(), e.getCurrentContentRevisionId(), e.getCurrentContentRevisionNo(),
                e.getEditorSchemaVersion(), e.getContentChecksum(), e.getContentUpdatedAt(), e.getContentUpdatedBy(),
                e.getTemplateVersionId(), e.getPageIcon(), e.getPageCoverObjectKey(),
                e.getContentWidth() != null ? ContentWidth.valueOf(e.getContentWidth()) : ContentWidth.CENTERED,
                e.isClientVisible());
    }

    public DocumentJpaEntity toJpaEntity(Document d) {
        DocumentJpaEntity e = new DocumentJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setFolderId(d.folderId());
        e.setDocumentTypeCode(d.documentTypeCode());
        e.setCode(d.code());
        e.setTitle(d.title());
        e.setDescription(d.description());
        e.setStatus(d.status().name());
        e.setClassification(d.classification());
        e.setCurrentVersionId(d.currentVersionId());
        e.setLocked(d.locked());
        e.setApprovedAt(d.approvedAt());
        e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setTraceId(d.traceId());
        e.setContentMode(d.contentMode() != null ? d.contentMode().name() : ContentMode.FILE.name());
        e.setParentDocumentId(d.parentDocumentId());
        e.setCurrentContentRevisionId(d.currentContentRevisionId());
        e.setCurrentContentRevisionNo(d.currentContentRevisionNo());
        e.setEditorSchemaVersion(d.editorSchemaVersion());
        e.setContentChecksum(d.contentChecksum());
        e.setContentUpdatedAt(d.contentUpdatedAt());
        e.setContentUpdatedBy(d.contentUpdatedBy());
        e.setTemplateVersionId(d.templateVersionId());
        e.setPageIcon(d.pageIcon());
        e.setPageCoverObjectKey(d.pageCoverObjectKey());
        e.setContentWidth(d.contentWidth() != null ? d.contentWidth().name() : ContentWidth.CENTERED.name());
        e.setClientVisible(d.clientVisible());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
            e.setVersion(d.version());
        }
        return e;
    }
}
