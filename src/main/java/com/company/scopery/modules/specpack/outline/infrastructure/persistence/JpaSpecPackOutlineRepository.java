package com.company.scopery.modules.specpack.outline.infrastructure.persistence;

import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutline;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutlineRepository;
import com.company.scopery.modules.specpack.outline.infrastructure.mapper.SpecPackOutlinePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSpecPackOutlineRepository implements SpecPackOutlineRepository {

    private final SpringDataSpecPackOutlineJpaRepository springDataRepository;
    private final SpecPackOutlinePersistenceMapper mapper;

    public JpaSpecPackOutlineRepository(SpringDataSpecPackOutlineJpaRepository springDataRepository,
                                         SpecPackOutlinePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public SpecPackOutline save(SpecPackOutline outline) {
        SpecPackOutlineJpaEntity entity = mapper.toJpaEntity(outline);
        SpecPackOutlineJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecPackOutline> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SpecPackOutline> findAllBySessionId(UUID sessionId) {
        return springDataRepository.findBySessionId(sessionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SpecPackOutline> findAllBySessionIdAndStatus(UUID sessionId, String status) {
        return springDataRepository.findBySessionIdAndStatus(sessionId, status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<SpecPackOutline> findLatestDraftOrApproved(UUID sessionId) {
        return springDataRepository.findLatestDraftOrApproved(sessionId).map(mapper::toDomain);
    }
}
