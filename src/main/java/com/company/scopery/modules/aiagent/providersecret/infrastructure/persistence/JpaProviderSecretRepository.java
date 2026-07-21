package com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretStatus;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.mapper.ProviderSecretPersistenceMapper;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence.entity.ProviderSecretJpaEntity;
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
public class JpaProviderSecretRepository implements ProviderSecretRepository {

    private final SpringDataProviderSecretJpaRepository springDataRepository;
    private final ProviderSecretPersistenceMapper mapper;

    public JpaProviderSecretRepository(SpringDataProviderSecretJpaRepository springDataRepository,
                                       ProviderSecretPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProviderSecret save(ProviderSecret secret) {
        ProviderSecretJpaEntity entity = mapper.toJpaEntity(secret);
        ProviderSecretJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProviderSecret> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProviderSecret> findActiveByProviderIdAndSecretType(UUID providerId, ProviderSecretType secretType) {
        return springDataRepository.findByProviderIdAndSecretTypeAndStatus(
                        providerId, secretType.name(), ProviderSecretStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ProviderSecret> findActiveByProviderCodeAndSecretType(String providerCode, ProviderSecretType secretType) {
        return springDataRepository.findByProviderCodeAndSecretTypeAndStatus(
                        providerCode, secretType.name(), ProviderSecretStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<ProviderSecret> findAll(UUID providerId, ProviderSecretType secretType,
                                              ProviderSecretStatus status, PageQuery pageQuery) {
        Specification<ProviderSecretJpaEntity> spec = buildSearchSpec(providerId, secretType, status);
        Pageable pageable = toPageable(pageQuery);
        Page<ProviderSecret> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<ProviderSecretJpaEntity> buildSearchSpec(UUID providerId,
                                                                     ProviderSecretType secretType,
                                                                     ProviderSecretStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (providerId != null) {
                predicates.add(cb.equal(root.get("providerId"), providerId));
            }
            if (secretType != null) {
                predicates.add(cb.equal(root.get("secretType"), secretType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
