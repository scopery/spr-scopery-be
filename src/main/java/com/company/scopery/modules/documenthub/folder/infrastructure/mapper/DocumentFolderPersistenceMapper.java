package com.company.scopery.modules.documenthub.folder.infrastructure.mapper;
import com.company.scopery.modules.documenthub.folder.domain.enums.FolderStatus;
import com.company.scopery.modules.documenthub.folder.domain.model.DocumentFolder;
import com.company.scopery.modules.documenthub.folder.infrastructure.persistence.DocumentFolderJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DocumentFolderPersistenceMapper {
    public DocumentFolder toDomain(DocumentFolderJpaEntity e) {
        return new DocumentFolder(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getParentFolderId(), e.getName(), e.getDescription(),
                FolderStatus.valueOf(e.getStatus()), e.getSortOrder(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DocumentFolderJpaEntity toJpaEntity(DocumentFolder d) {
        DocumentFolderJpaEntity e = new DocumentFolderJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setParentFolderId(d.parentFolderId());
        e.setName(d.name()); e.setDescription(d.description()); e.setStatus(d.status().name()); e.setSortOrder(d.sortOrder());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        if (d.createdAt()!=null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}
