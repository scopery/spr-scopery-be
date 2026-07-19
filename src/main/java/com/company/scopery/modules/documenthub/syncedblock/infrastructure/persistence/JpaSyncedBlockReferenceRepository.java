package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockReference;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockReferenceRepository;
import com.company.scopery.modules.documenthub.syncedblock.infrastructure.mapper.SyncedBlockPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSyncedBlockReferenceRepository implements SyncedBlockReferenceRepository {

    private final SpringDataSyncedBlockReferenceJpaRepository springData;
    private final SyncedBlockPersistenceMapper mapper;

    public JpaSyncedBlockReferenceRepository(SpringDataSyncedBlockReferenceJpaRepository springData,
                                              SyncedBlockPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public SyncedBlockReference save(SyncedBlockReference ref) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(ref)));
    }

    @Override
    public Optional<SyncedBlockReference> findBySyncedBlockIdAndDocumentId(UUID syncedBlockId, UUID documentId) {
        return springData.findBySyncedBlockIdAndDocumentId(syncedBlockId, documentId).map(mapper::toDomain);
    }

    @Override
    public List<SyncedBlockReference> findBySyncedBlockId(UUID syncedBlockId) {
        return springData.findBySyncedBlockId(syncedBlockId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByDocumentIdAndSyncedBlockId(UUID documentId, UUID syncedBlockId) {
        springData.deleteByDocumentIdAndSyncedBlockId(documentId, syncedBlockId);
    }
}
