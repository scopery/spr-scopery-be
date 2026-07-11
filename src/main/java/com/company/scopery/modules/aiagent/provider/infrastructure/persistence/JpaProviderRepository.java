package com.company.scopery.modules.aiagent.provider.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.provider.infrastructure.mapper.ProviderPersistenceMapper;
import com.company.scopery.modules.aiagent.provider.infrastructure.persistence.entity.ProviderJpaEntity;
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
public class JpaProviderRepository implements ProviderRepository {

    private final SpringDataProviderJpaRepository springDataRepository;
    private final ProviderPersistenceMapper mapper;

    public JpaProviderRepository(SpringDataProviderJpaRepository springDataRepository,
                                  ProviderPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Provider save(Provider provider) {
        ProviderJpaEntity entity = mapper.toJpaEntity(provider);
        // saveAndFlush ensures @PrePersist / @PreUpdate fires immediately so audit timestamps
        // (createdAt, updatedAt) are populated in the returned entity before mapping back to domain.
        ProviderJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Provider> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(ProviderCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public PageResult<Provider> findAll(String keyword, ProviderType type, ProviderStatus status, PageQuery pageQuery) {
        Specification<ProviderJpaEntity> spec = buildSearchSpec(keyword, type, status);
        Pageable pageable = toPageable(pageQuery);
        Page<Provider> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<ProviderJpaEntity> buildSearchSpec(String keyword, ProviderType type, ProviderStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
