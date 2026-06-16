package com.company.scopery.modules.aiagent.agent.infrastructure.persistence;

import com.company.scopery.modules.aiagent.agent.domain.*;
import com.company.scopery.modules.aiagent.agent.infrastructure.mapper.AgentPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAgentRepository implements AgentRepository {

    private final SpringDataAgentJpaRepository springDataRepository;
    private final AgentPersistenceMapper mapper;

    public JpaAgentRepository(SpringDataAgentJpaRepository springDataRepository,
                               AgentPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Agent save(Agent agent) {
        AgentJpaEntity entity = mapper.toJpaEntity(agent);
        AgentJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Agent> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Agent> findAllByStatus(AgentStatus status) {
        return springDataRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCode(AgentCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public Page<Agent> findAll(String keyword, AgentType type, AgentStatus status,
                               AgentOutputFormat outputFormat, Pageable pageable) {
        Specification<AgentJpaEntity> spec = buildSearchSpec(keyword, type, status, outputFormat);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<AgentJpaEntity> buildSearchSpec(String keyword, AgentType type,
                                                           AgentStatus status,
                                                           AgentOutputFormat outputFormat) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toUpperCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.upper(root.get("name")), pattern),
                        cb.like(root.get("code"), pattern)
                ));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (outputFormat != null) {
                predicates.add(cb.equal(root.get("outputFormat"), outputFormat.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}