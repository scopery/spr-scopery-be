package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence;

import com.company.scopery.modules.aiagent.prompt.domain.*;
import com.company.scopery.modules.aiagent.prompt.infrastructure.mapper.PromptVersionPersistenceMapper;
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
public class JpaPromptVersionRepository implements PromptVersionRepository {

    private final SpringDataPromptVersionJpaRepository springDataRepository;
    private final PromptVersionPersistenceMapper mapper;

    public JpaPromptVersionRepository(SpringDataPromptVersionJpaRepository springDataRepository,
                                      PromptVersionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public PromptVersion save(PromptVersion version) {
        PromptVersionJpaEntity entity = mapper.toJpaEntity(version);
        PromptVersionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PromptVersion> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PromptVersion> findAllByStatus(PromptVersionStatus status) {
        return springDataRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public int getMaxVersionNumber(UUID templateId) {
        return springDataRepository.getMaxVersionNumber(templateId);
    }

    @Override
    public int archiveOtherActiveVersions(UUID templateId, UUID excludeId) {
        return springDataRepository.archiveOtherActiveVersions(templateId, excludeId);
    }

    @Override
    public Page<PromptVersion> findAll(UUID templateId, PromptVersionStatus status,
                                       PromptContentFormat contentFormat, Pageable pageable) {
        Specification<PromptVersionJpaEntity> spec = buildSearchSpec(templateId, status, contentFormat);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<PromptVersionJpaEntity> buildSearchSpec(UUID templateId,
                                                                   PromptVersionStatus status,
                                                                   PromptContentFormat contentFormat) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (templateId != null) {
                predicates.add(cb.equal(root.get("templateId"), templateId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (contentFormat != null) {
                predicates.add(cb.equal(root.get("contentFormat"), contentFormat.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}