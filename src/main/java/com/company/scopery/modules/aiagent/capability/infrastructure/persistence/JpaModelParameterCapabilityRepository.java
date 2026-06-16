package com.company.scopery.modules.aiagent.capability.infrastructure.persistence;

import com.company.scopery.modules.aiagent.capability.domain.*;
import com.company.scopery.modules.aiagent.capability.infrastructure.mapper.ModelParameterCapabilityPersistenceMapper;
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
    public Page<ModelParameterCapability> findAll(UUID modelId, String parameterName,
                                                   ModelParameterSupportStatus supportStatus,
                                                   ModelParameterValueType valueType,
                                                   ModelParameterCapabilityStatus status,
                                                   Pageable pageable) {
        Specification<ModelParameterCapabilityJpaEntity> spec =
                buildSearchSpec(modelId, parameterName, supportStatus, valueType, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
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
