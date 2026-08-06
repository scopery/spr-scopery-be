package com.company.scopery.modules.specpack.block.infrastructure.persistence;

import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;
import com.company.scopery.modules.specpack.block.infrastructure.mapper.SpecPackBlockPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSpecPackBlockRepository implements SpecPackBlockRepository {

    private final SpringDataSpecPackBlockJpaRepository blockRepo;
    private final SpringDataSpecPackBlockRevisionJpaRepository revisionRepo;
    private final SpecPackBlockPersistenceMapper mapper;

    public JpaSpecPackBlockRepository(SpringDataSpecPackBlockJpaRepository blockRepo,
                                       SpringDataSpecPackBlockRevisionJpaRepository revisionRepo,
                                       SpecPackBlockPersistenceMapper mapper) {
        this.blockRepo = blockRepo;
        this.revisionRepo = revisionRepo;
        this.mapper = mapper;
    }

    @Override
    public SpecPackBlock save(SpecPackBlock block) {
        SpecPackBlockJpaEntity entity = mapper.toJpaEntity(block);
        SpecPackBlockJpaEntity saved = blockRepo.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public SpecPackBlockRevision saveRevision(SpecPackBlockRevision revision) {
        SpecPackBlockRevisionJpaEntity entity = mapper.toRevisionJpaEntity(revision);
        SpecPackBlockRevisionJpaEntity saved = revisionRepo.saveAndFlush(entity);
        return mapper.toRevisionDomain(saved);
    }

    @Override
    public Optional<SpecPackBlock> findById(UUID id) {
        return blockRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SpecPackBlock> findByPackIdAndBlockKey(UUID specPackId, String blockKey) {
        return blockRepo.findByPackIdAndBlockKey(specPackId, blockKey).map(mapper::toDomain);
    }

    @Override
    public boolean existsByPackIdAndBlockKey(UUID specPackId, String blockKey) {
        return blockRepo.existsByPackIdAndBlockKey(specPackId, blockKey);
    }

    @Override
    public List<SpecPackBlock> findAllByPackIdOrdered(UUID specPackId) {
        return blockRepo.findAllByPackIdOrdered(specPackId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SpecPackBlockRevision> findRevisionsByBlockId(UUID blockId) {
        return revisionRepo.findByBlockId(blockId).stream().map(mapper::toRevisionDomain).toList();
    }

    @Override
    public Optional<SpecPackBlockRevision> findRevisionByBlockIdAndNumber(UUID blockId, int revisionNumber) {
        return revisionRepo.findByBlockIdAndRevisionNumber(blockId, revisionNumber).map(mapper::toRevisionDomain);
    }

    @Override
    public void saveAll(List<SpecPackBlock> blocks) {
        List<SpecPackBlockJpaEntity> entities = blocks.stream().map(mapper::toJpaEntity).toList();
        blockRepo.saveAllAndFlush(entities);
    }
}
