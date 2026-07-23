package com.company.scopery.modules.traceability.nfrscope.infrastructure.persistence;

import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTarget;
import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTargetRepository;
import com.company.scopery.modules.traceability.nfrscope.infrastructure.mapper.NfrScopeTargetPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaNfrScopeTargetRepository implements NfrScopeTargetRepository {

    private final SpringDataNfrScopeTargetJpaRepository springData;
    private final NfrScopeTargetPersistenceMapper mapper;

    public JpaNfrScopeTargetRepository(SpringDataNfrScopeTargetJpaRepository springData,
                                        NfrScopeTargetPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public NfrScopeTarget save(NfrScopeTarget target) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(target)));
    }

    @Override
    public boolean existsByNfrIdAndTargetId(UUID nfrId, UUID targetId) {
        return springData.existsByIdNfrIdAndIdTargetId(nfrId, targetId);
    }

    @Override
    public Optional<NfrScopeTarget> findByNfrIdAndTargetId(UUID nfrId, UUID targetId) {
        return springData.findByIdNfrIdAndIdTargetId(nfrId, targetId).map(mapper::toDomain);
    }

    @Override
    public List<NfrScopeTarget> findByNfrId(UUID nfrId) {
        return springData.findByIdNfrId(nfrId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<NfrScopeTarget> findByTargetId(UUID targetId) {
        return springData.findByIdTargetId(targetId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByNfrIdAndTargetId(UUID nfrId, UUID targetId) {
        springData.deleteByIdNfrIdAndIdTargetId(nfrId, targetId);
    }
}
