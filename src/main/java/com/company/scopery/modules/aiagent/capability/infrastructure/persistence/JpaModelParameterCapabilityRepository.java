package com.company.scopery.modules.aiagent.capability.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterCapabilityStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapabilityRepository;
import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;
import com.company.scopery.modules.aiagent.capability.infrastructure.mapper.ModelParameterCapabilityPersistenceMapper;
import com.company.scopery.modules.aiagent.capability.infrastructure.persistence.entity.ModelParameterCapabilityJpaEntity;
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
public class JpaModelParameterCapabilityRepository implements ModelParameterCapabilityRepository {

    private final SpringDataModelParameterCapabilityJpaRepository springDataRepository;
    private final ModelParameterCapabilityPersistenceMapper mapper;

    public JpaModelParameterCapabilityRepository(
            SpringDataModelParameterCapabilityJpaRepository springDataRepository,
            ModelParameterCapabilityPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ModelParameterCapability save(ModelParameterCapability capability) {
        ModelParameterCapabilityJpaEntity entity = mapper.toJpaEntity(capability);
        ModelParameterCapabilityJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ModelParameterCapability> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByModelIdAndParameterName(UUID modelId, ModelParameterName parameterName) {
        return springDataRepository.existsByModelIdAndParameterName(modelId, parameterName.value());
    }

    @Override
    public PageResult<ModelParameterCapability> findAll(UUID modelId, String parameterName,
                                                          ModelParameterSupportStatus supportStatus,
                                                          ModelParameterValueType valueType,
                                                          ModelParameterCapabilityStatus status,
                                                          PageQuery pageQuery) {
        Specification<ModelParameterCapabilityJpaEntity> spec =
                buildSearchSpec(modelId, parameterName, supportStatus, valueType, status);
        Pageable pageable = toPageable(pageQuery);
        Page<ModelParameterCapability> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<ModelParameterCapabilityJpaEntity> buildSearchSpec(
            UUID modelId, String parameterName,
            ModelParameterSupportStatus supportStatus,
            ModelParameterValueType valueType,
            ModelParameterCapabilityStatus status) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (modelId != null) {
                predicates.add(cb.equal(root.get("modelId"), modelId));
            }
            if (parameterName != null && !parameterName.isBlank()) {
                String pattern = "%" + parameterName.toUpperCase() + "%";
                predicates.add(cb.like(root.get("parameterName"), pattern));
            }
            if (supportStatus != null) {
                predicates.add(cb.equal(root.get("supportStatus"), supportStatus.name()));
            }
            if (valueType != null) {
                predicates.add(cb.equal(root.get("valueType"), valueType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
