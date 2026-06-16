package com.company.scopery.modules.aiagent.provider.infrastructure.persistence;

import com.company.scopery.modules.aiagent.provider.domain.Provider;
import com.company.scopery.modules.aiagent.provider.domain.ProviderCode;
import com.company.scopery.modules.aiagent.provider.domain.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.infrastructure.mapper.ProviderPersistenceMapper;
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
    public Page<Provider> findAll(String keyword, String type, ProviderStatus status, Pageable pageable) {
        Specification<ProviderJpaEntity> spec = buildSearchSpec(keyword, type, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<ProviderJpaEntity> buildSearchSpec(String keyword, String type, ProviderStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("type")), type.toLowerCase()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
