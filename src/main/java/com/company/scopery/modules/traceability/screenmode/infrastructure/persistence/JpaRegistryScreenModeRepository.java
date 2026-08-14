package com.company.scopery.modules.traceability.screenmode.infrastructure.persistence;

import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.screenmode.infrastructure.mapper.RegistryScreenModePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryScreenModeRepository implements RegistryScreenModeRepository {

    private final SpringDataRegistryScreenModeJpaRepository springData;
    private final RegistryScreenModePersistenceMapper mapper;

    public JpaRegistryScreenModeRepository(SpringDataRegistryScreenModeJpaRepository springData,
                                            RegistryScreenModePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryScreenMode save(RegistryScreenMode entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public Optional<RegistryScreenMode> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryScreenMode> findByScreenId(UUID screenId) {
        return springData.findByScreenIdOrderByDisplayOrderAsc(screenId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryScreenMode> findByIdIn(Collection<UUID> ids) {
        return springData.findByIdIn(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<RegistryScreenMode> findByScreenIdAndModeCode(UUID screenId, String modeCode) {
        return springData.findByScreenIdAndModeCode(screenId, modeCode).map(mapper::toDomain);
    }

    @Override
    public void delete(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
