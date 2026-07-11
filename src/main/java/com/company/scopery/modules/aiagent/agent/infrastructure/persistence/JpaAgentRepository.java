package com.company.scopery.modules.aiagent.agent.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;
import com.company.scopery.modules.aiagent.agent.infrastructure.mapper.AgentPersistenceMapper;
import com.company.scopery.modules.aiagent.agent.infrastructure.persistence.entity.AgentJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResult<Agent> findAll(String keyword, AgentType type, AgentStatus status,
                               AgentOutputFormat outputFormat, PageQuery pageQuery) {
        Specification<AgentJpaEntity> spec = buildSearchSpec(keyword, type, status, outputFormat);
        Pageable pageable = toPageable(pageQuery);
        Page<Agent> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
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