package com.company.scopery.modules.aiagent.deployment.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.deployment.infrastructure.mapper.ModelDeploymentPersistenceMapper;
import com.company.scopery.modules.aiagent.deployment.infrastructure.persistence.entity.ModelDeploymentJpaEntity;
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
public class JpaModelDeploymentRepository implements ModelDeploymentRepository {

    private final SpringDataModelDeploymentJpaRepository springDataRepository;
    private final ModelDeploymentPersistenceMapper mapper;

    public JpaModelDeploymentRepository(SpringDataModelDeploymentJpaRepository springDataRepository,
                                         ModelDeploymentPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ModelDeployment save(ModelDeployment deployment) {
        ModelDeploymentJpaEntity entity = mapper.toJpaEntity(deployment);
        ModelDeploymentJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ModelDeployment> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ModelDeployment> findAllByStatus(ModelDeploymentStatus status) {
        return springDataRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByModelIdAndCode(UUID modelId, ModelDeploymentCode code) {
        return springDataRepository.existsByModelIdAndCode(modelId, code.value());
    }

    @Override
    public PageResult<ModelDeployment> findAll(UUID modelId, ModelDeploymentEnvironment environment,
                                                String keyword, ModelDeploymentStatus status,
                                                Boolean isDefault, PageQuery pageQuery) {
        Specification<ModelDeploymentJpaEntity> spec =
                buildSearchSpec(modelId, environment, keyword, status, isDefault);
        Pageable pageable = toPageable(pageQuery);
        Page<ModelDeployment> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    @Override
    public int clearDefaultFlags(UUID modelId, ModelDeploymentEnvironment environment, UUID excludeId) {
        if (excludeId == null) {
            return springDataRepository.clearAllDefaultFlags(modelId, environment.name());
        } else {
            return springDataRepository.clearOtherDefaultFlags(modelId, environment.name(), excludeId);
        }
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<ModelDeploymentJpaEntity> buildSearchSpec(UUID modelId,
                                                                     ModelDeploymentEnvironment environment,
                                                                     String keyword,
                                                                     ModelDeploymentStatus status,
                                                                     Boolean isDefault) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (modelId != null) {
                predicates.add(cb.equal(root.get("modelId"), modelId));
            }
            if (environment != null) {
                predicates.add(cb.equal(root.get("environment"), environment.name()));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("providerDeploymentId")), pattern)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (isDefault != null) {
                predicates.add(cb.equal(root.get("isDefault"), isDefault));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
