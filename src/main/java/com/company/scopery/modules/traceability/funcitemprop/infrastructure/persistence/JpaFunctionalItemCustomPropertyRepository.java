package com.company.scopery.modules.traceability.funcitemprop.infrastructure.persistence;

import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomProperty;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomPropertyRepository;
import com.company.scopery.modules.traceability.funcitemprop.infrastructure.mapper.FunctionalItemCustomPropertyPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFunctionalItemCustomPropertyRepository implements FunctionalItemCustomPropertyRepository {

    private final SpringDataFunctionalItemCustomPropertyJpaRepository springData;
    private final FunctionalItemCustomPropertyPersistenceMapper mapper;

    public JpaFunctionalItemCustomPropertyRepository(
            SpringDataFunctionalItemCustomPropertyJpaRepository springData,
            FunctionalItemCustomPropertyPersistenceMapper mapper
    ) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public FunctionalItemCustomProperty save(FunctionalItemCustomProperty prop) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(prop)));
    }

    @Override
    public Optional<FunctionalItemCustomProperty> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId) {
        return springData.findByIdAndFunctionalItemId(id, functionalItemId).map(mapper::toDomain);
    }

    @Override
    public List<FunctionalItemCustomProperty> findByFunctionalItemId(UUID functionalItemId) {
        return springData.findByFunctionalItemIdOrderByCreatedAtAsc(functionalItemId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByFunctionalItemIdAndPropKey(UUID functionalItemId, String propKey) {
        return springData.existsByFunctionalItemIdAndPropKey(functionalItemId, propKey);
    }

    @Override
    public void delete(UUID id, UUID functionalItemId) {
        springData.deleteByIdAndFunctionalItemId(id, functionalItemId);
    }
}
