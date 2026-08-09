package com.company.scopery.modules.elicitation.round.infrastructure.persistence;

import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.round.infrastructure.mapper.ElicitationRoundPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaElicitationRoundRepository implements ElicitationRoundRepository {

    private final SpringDataElicitationRoundJpaRepository springDataRepo;
    private final ElicitationRoundPersistenceMapper mapper;

    public JpaElicitationRoundRepository(SpringDataElicitationRoundJpaRepository springDataRepo,
                                          ElicitationRoundPersistenceMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public ElicitationRound save(ElicitationRound round) {
        ElicitationRoundJpaEntity entity = mapper.toJpaEntity(round);
        ElicitationRoundJpaEntity saved = springDataRepo.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ElicitationRound> findById(UUID id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ElicitationRound> findAllBySessionId(UUID sessionId) {
        return springDataRepo.findBySessionIdOrderByRoundNumberAsc(sessionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ElicitationRound> findActiveBySessionId(UUID sessionId) {
        return springDataRepo.findFirstBySessionIdAndStatus(sessionId, "ACTIVE").map(mapper::toDomain);
    }

    @Override
    public Optional<ElicitationRound> findFirstBySessionIdOrderByRoundNumberAsc(UUID sessionId) {
        return springDataRepo.findFirstBySessionIdOrderByRoundNumberAsc(sessionId).map(mapper::toDomain);
    }

    @Override
    public int countBySessionId(UUID sessionId) {
        return springDataRepo.countBySessionId(sessionId);
    }
}
