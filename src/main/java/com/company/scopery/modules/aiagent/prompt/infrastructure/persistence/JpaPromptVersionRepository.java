package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.infrastructure.mapper.PromptVersionPersistenceMapper;
import com.company.scopery.modules.aiagent.prompt.infrastructure.persistence.entity.PromptVersionJpaEntity;
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
    public PageResult<PromptVersion> findAll(UUID templateId, PromptVersionStatus status,
                                             PromptContentFormat contentFormat, PageQuery pageQuery) {
        Specification<PromptVersionJpaEntity> spec = buildSearchSpec(templateId, status, contentFormat);
        Pageable pageable = toPageable(pageQuery);
        Page<PromptVersion> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
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