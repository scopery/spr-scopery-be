package com.company.scopery.modules.traceability.commspec.infrastructure.persistence;

import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecification;
import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecificationRepository;
import com.company.scopery.modules.traceability.commspec.infrastructure.mapper.CommunicationSpecificationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCommunicationSpecificationRepository implements CommunicationSpecificationRepository {
    private final SpringDataCommunicationSpecificationJpaRepository springData;
    private final CommunicationSpecificationPersistenceMapper mapper;

    public JpaCommunicationSpecificationRepository(
            SpringDataCommunicationSpecificationJpaRepository springData,
            CommunicationSpecificationPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public CommunicationSpecification save(CommunicationSpecification spec) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(spec)));
    }

    @Override
    public Optional<CommunicationSpecification> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CommunicationSpecification> findByIdAndApplicationId(UUID id, UUID applicationId) {
        return springData.findByIdAndApplicationId(id, applicationId).map(mapper::toDomain);
    }

    @Override
    public List<CommunicationSpecification> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCodeAsc(applicationId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CommunicationSpecification> findByIdIn(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return springData.findByIdIn(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByApplicationIdAndCode(UUID applicationId, String code) {
        return springData.existsByApplicationIdAndCode(applicationId, code);
    }
}
