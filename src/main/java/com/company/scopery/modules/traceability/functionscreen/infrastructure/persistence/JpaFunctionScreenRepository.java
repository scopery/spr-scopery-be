package com.company.scopery.modules.traceability.functionscreen.infrastructure.persistence;

import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.functionscreen.infrastructure.mapper.FunctionScreenPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFunctionScreenRepository implements FunctionScreenRepository {

    private final SpringDataFunctionScreenJpaRepository springData;
    private final FunctionScreenPersistenceMapper mapper;

    public JpaFunctionScreenRepository(SpringDataFunctionScreenJpaRepository springData,
                                        FunctionScreenPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public FunctionScreen save(FunctionScreen link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public boolean existsByFunctionIdAndScreenId(UUID functionId, UUID screenId) {
        return springData.existsByIdFunctionIdAndIdScreenId(functionId, screenId);
    }

    @Override
    public Optional<FunctionScreen> findByFunctionIdAndScreenId(UUID functionId, UUID screenId) {
        return springData.findByIdFunctionIdAndIdScreenId(functionId, screenId).map(mapper::toDomain);
    }

    @Override
    public List<FunctionScreen> findByFunctionId(UUID functionId) {
        return springData.findByIdFunctionId(functionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionScreen> findByFunctionIdIn(Collection<UUID> functionIds) {
        return springData.findByIdFunctionIdIn(functionIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionScreen> findByScreenId(UUID screenId) {
        return springData.findByIdScreenId(screenId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByFunctionIdAndScreenId(UUID functionId, UUID screenId) {
        springData.deleteByIdFunctionIdAndIdScreenId(functionId, screenId);
    }
}
