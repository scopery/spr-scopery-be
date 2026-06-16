package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence;

import com.company.scopery.modules.aiagent.prompt.domain.*;
import com.company.scopery.modules.aiagent.prompt.infrastructure.mapper.PromptTemplatePersistenceMapper;
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
public class JpaPromptTemplateRepository implements PromptTemplateRepository {

    private final SpringDataPromptTemplateJpaRepository springDataRepository;
    private final PromptTemplatePersistenceMapper mapper;

    public JpaPromptTemplateRepository(SpringDataPromptTemplateJpaRepository springDataRepository,
                                       PromptTemplatePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public PromptTemplate save(PromptTemplate template) {
        PromptTemplateJpaEntity entity = mapper.toJpaEntity(template);
        PromptTemplateJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PromptTemplate> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByAgentIdAndCode(UUID agentId, PromptTemplateCode code) {
        return springDataRepository.existsByAgentIdAndCode(agentId, code.value());
    }

    @Override
    public Page<PromptTemplate> findAll(UUID agentId, String keyword,
                                        PromptTemplateStatus status, Pageable pageable) {
        Specification<PromptTemplateJpaEntity> spec = buildSearchSpec(agentId, keyword, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<PromptTemplateJpaEntity> buildSearchSpec(UUID agentId, String keyword,
                                                                    PromptTemplateStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toUpperCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.upper(root.get("name")), pattern),
                        cb.like(root.get("code"), pattern)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
