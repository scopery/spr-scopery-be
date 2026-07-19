package com.company.scopery.modules.documenthub.syncedblock.infrastructure.mapper;

import com.company.scopery.modules.documenthub.syncedblock.domain.enums.SyncedBlockStatus;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlock;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockReference;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevision;
import com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence.SyncedBlockJpaEntity;
import com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence.SyncedBlockReferenceJpaEntity;
import com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence.SyncedBlockRevisionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SyncedBlockPersistenceMapper {

    // -----------------------------------------------------------------------
    // SyncedBlock
    // -----------------------------------------------------------------------

    public SyncedBlock toDomain(SyncedBlockJpaEntity e) {
        return new SyncedBlock(
                e.getId(),
                e.getWorkspaceId(),
                e.getProjectId(),
                e.getTitle(),
                SyncedBlockStatus.valueOf(e.getStatus()),
                e.getCurrentRevisionNo(),
                e.getSchemaVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public SyncedBlockJpaEntity toJpaEntity(SyncedBlock d) {
        SyncedBlockJpaEntity e = new SyncedBlockJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setTitle(d.title());
        e.setStatus(d.status().name());
        e.setCurrentRevisionNo(d.currentRevisionNo());
        e.setSchemaVersion(d.schemaVersion());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }

    // -----------------------------------------------------------------------
    // SyncedBlockRevision
    // -----------------------------------------------------------------------

    public SyncedBlockRevision toDomain(SyncedBlockRevisionJpaEntity e) {
        return new SyncedBlockRevision(
                e.getId(),
                e.getSyncedBlockId(),
                e.getRevisionNo(),
                e.getAst(),
                e.getCreatedAt(),
                e.getCreatedBy()
        );
    }

    public SyncedBlockRevisionJpaEntity toJpaEntity(SyncedBlockRevision d) {
        SyncedBlockRevisionJpaEntity e = new SyncedBlockRevisionJpaEntity();
        e.setId(d.id());
        e.setSyncedBlockId(d.syncedBlockId());
        e.setRevisionNo(d.revisionNo());
        e.setAst(d.ast());
        e.setCreatedAt(d.createdAt());
        e.setCreatedBy(d.createdBy());
        return e;
    }

    // -----------------------------------------------------------------------
    // SyncedBlockReference
    // -----------------------------------------------------------------------

    public SyncedBlockReference toDomain(SyncedBlockReferenceJpaEntity e) {
        return new SyncedBlockReference(
                e.getId(),
                e.getSyncedBlockId(),
                e.getDocumentId(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public SyncedBlockReferenceJpaEntity toJpaEntity(SyncedBlockReference d) {
        SyncedBlockReferenceJpaEntity e = new SyncedBlockReferenceJpaEntity();
        e.setId(d.id());
        e.setSyncedBlockId(d.syncedBlockId());
        e.setDocumentId(d.documentId());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
