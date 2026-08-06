package com.company.scopery.modules.specpack.agentsession.infrastructure.persistence;

import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentStage;
import com.company.scopery.modules.specpack.agentsession.infrastructure.mapper.SpecPackAgentSessionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSpecPackAgentSessionRepository implements SpecPackAgentSessionRepository {

    private final SpringDataSpecPackAgentSessionJpaRepository sessionRepo;
    private final SpringDataSpecPackAgentStageJpaRepository stageRepo;
    private final SpecPackAgentSessionPersistenceMapper mapper;

    public JpaSpecPackAgentSessionRepository(SpringDataSpecPackAgentSessionJpaRepository sessionRepo,
                                              SpringDataSpecPackAgentStageJpaRepository stageRepo,
                                              SpecPackAgentSessionPersistenceMapper mapper) {
        this.sessionRepo = sessionRepo;
        this.stageRepo = stageRepo;
        this.mapper = mapper;
    }

    @Override
    public SpecPackAgentSession save(SpecPackAgentSession session) {
        SpecPackAgentSessionJpaEntity entity = mapper.toJpaEntity(session);
        SpecPackAgentSessionJpaEntity saved = sessionRepo.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecPackAgentSession> findById(UUID sessionId) {
        return sessionRepo.findById(sessionId).map(mapper::toDomain);
    }

    @Override
    public List<SpecPackAgentSession> findAllByProjectId(UUID projectId) {
        return sessionRepo.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public SpecPackAgentStage saveStage(SpecPackAgentStage stage) {
        SpecPackAgentStageJpaEntity entity = mapper.toStageJpaEntity(stage);
        SpecPackAgentStageJpaEntity saved = stageRepo.save(entity);
        return mapper.toStageDomain(saved);
    }

    @Override
    public List<SpecPackAgentStage> findStagesBySessionId(UUID sessionId) {
        return stageRepo.findBySessionId(sessionId).stream().map(mapper::toStageDomain).toList();
    }

    @Override
    public Optional<SpecPackAgentStage> findStageBySessionIdAndCode(UUID sessionId, String stageCode) {
        return stageRepo.findBySessionIdAndStageCode(sessionId, stageCode).map(mapper::toStageDomain);
    }
}
