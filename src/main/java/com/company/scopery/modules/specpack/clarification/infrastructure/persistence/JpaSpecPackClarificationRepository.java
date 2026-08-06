package com.company.scopery.modules.specpack.clarification.infrastructure.persistence;

import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarification;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarificationRepository;
import com.company.scopery.modules.specpack.clarification.infrastructure.mapper.SpecPackClarificationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSpecPackClarificationRepository implements SpecPackClarificationRepository {

    private final SpringDataSpecPackClarificationJpaRepository springDataRepository;
    private final SpecPackClarificationPersistenceMapper mapper;

    public JpaSpecPackClarificationRepository(SpringDataSpecPackClarificationJpaRepository springDataRepository,
                                               SpecPackClarificationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public SpecPackClarification save(SpecPackClarification clarification) {
        SpecPackClarificationJpaEntity entity = mapper.toJpaEntity(clarification);
        SpecPackClarificationJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecPackClarification> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SpecPackClarification> findAllBySessionId(UUID sessionId) {
        return springDataRepository.findBySessionId(sessionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SpecPackClarification> findAllBySessionIdAndStatus(UUID sessionId, String status) {
        return springDataRepository.findBySessionIdAndStatus(sessionId, status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countBySessionIdAndPriorityAndStatus(UUID sessionId, String priority, String status) {
        return springDataRepository.countBySessionIdAndPriorityAndStatus(sessionId, priority, status);
    }
}
