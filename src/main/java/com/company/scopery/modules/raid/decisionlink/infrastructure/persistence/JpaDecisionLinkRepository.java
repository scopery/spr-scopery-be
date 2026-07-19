package com.company.scopery.modules.raid.decisionlink.infrastructure.persistence;

import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLink;
import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLinkRepository;
import com.company.scopery.modules.raid.decisionlink.infrastructure.mapper.DecisionLinkPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDecisionLinkRepository implements DecisionLinkRepository {
    private final SpringDataDecisionLinkJpaRepository springData;
    private final DecisionLinkPersistenceMapper mapper;

    public JpaDecisionLinkRepository(SpringDataDecisionLinkJpaRepository springData, DecisionLinkPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DecisionLink save(DecisionLink link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public Optional<DecisionLink> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<DecisionLink> findByDecisionId(UUID decisionId) {
        return springData.findByDecisionIdOrderByCreatedAtAsc(decisionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndProjectId(UUID id, UUID projectId) {
        springData.deleteByIdAndProjectId(id, projectId);
    }
}
