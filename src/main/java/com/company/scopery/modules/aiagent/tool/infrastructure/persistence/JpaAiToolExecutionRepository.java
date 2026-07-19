package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolExecutionStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolExecution;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolExecutionRepository;
import com.company.scopery.modules.aiagent.tool.infrastructure.mapper.AiToolExecutionPersistenceMapper;
import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolExecutionJpaEntity;
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
public class JpaAiToolExecutionRepository implements AiToolExecutionRepository {

    private final SpringDataAiToolExecutionJpaRepository springDataRepository;
    private final AiToolExecutionPersistenceMapper mapper;

    public JpaAiToolExecutionRepository(SpringDataAiToolExecutionJpaRepository springDataRepository,
                                        AiToolExecutionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiToolExecution save(AiToolExecution execution) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(execution)));
    }

    @Override
    public Optional<AiToolExecution> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<AiToolExecution> findAll(UUID toolId, UUID agentId, AiToolExecutionStatus status,
                                               PageQuery pageQuery) {
        Specification<AiToolExecutionJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (toolId != null) {
                predicates.add(cb.equal(root.get("toolId"), toolId));
            }
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
        Page<AiToolExecution> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }
}
