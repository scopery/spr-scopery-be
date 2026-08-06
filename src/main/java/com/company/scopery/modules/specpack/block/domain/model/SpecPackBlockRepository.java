package com.company.scopery.modules.specpack.block.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecPackBlockRepository {
    SpecPackBlock save(SpecPackBlock block);
    SpecPackBlockRevision saveRevision(SpecPackBlockRevision revision);
    Optional<SpecPackBlock> findById(UUID id);
    Optional<SpecPackBlock> findByPackIdAndBlockKey(UUID specPackId, String blockKey);
    boolean existsByPackIdAndBlockKey(UUID specPackId, String blockKey);
    List<SpecPackBlock> findAllByPackIdOrdered(UUID specPackId);
    List<SpecPackBlockRevision> findRevisionsByBlockId(UUID blockId);
    Optional<SpecPackBlockRevision> findRevisionByBlockIdAndNumber(UUID blockId, int revisionNumber);
    void saveAll(List<SpecPackBlock> blocks);
}
