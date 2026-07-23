package com.company.scopery.modules.traceability.screencomponent.infrastructure.persistence;

import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.screencomponent.infrastructure.mapper.ScreenComponentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaScreenComponentRepository implements ScreenComponentRepository {

    private final SpringDataScreenComponentJpaRepository springData;
    private final ScreenComponentPersistenceMapper mapper;

    public JpaScreenComponentRepository(SpringDataScreenComponentJpaRepository springData,
                                        ScreenComponentPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ScreenComponent save(ScreenComponent link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public boolean existsByScreenIdAndComponentId(UUID screenId, UUID componentId) {
        return springData.existsByIdScreenIdAndIdComponentId(screenId, componentId);
    }

    @Override
    public Optional<ScreenComponent> findByScreenIdAndComponentId(UUID screenId, UUID componentId) {
        return springData.findByIdScreenIdAndIdComponentId(screenId, componentId).map(mapper::toDomain);
    }

    @Override
    public List<ScreenComponent> findByScreenId(UUID screenId) {
        return springData.findByIdScreenIdOrderByDisplayOrderAsc(screenId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ScreenComponent> findByScreenIdIn(Collection<UUID> screenIds) {
        return springData.findByIdScreenIdIn(screenIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ScreenComponent> findByComponentId(UUID componentId) {
        return springData.findByIdComponentId(componentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByScreenIdAndComponentId(UUID screenId, UUID componentId) {
        springData.deleteByIdScreenIdAndIdComponentId(screenId, componentId);
    }
}
