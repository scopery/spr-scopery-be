package com.company.scopery.modules.traceability.screeneventitem.infrastructure.persistence;

import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItem;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.screeneventitem.infrastructure.mapper.RegistryScreenEventItemPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryScreenEventItemRepository implements RegistryScreenEventItemRepository {

    private final SpringDataRegistryScreenEventItemJpaRepository springData;
    private final RegistryScreenEventItemPersistenceMapper mapper;

    public JpaRegistryScreenEventItemRepository(SpringDataRegistryScreenEventItemJpaRepository springData,
                                                RegistryScreenEventItemPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryScreenEventItem save(RegistryScreenEventItem item) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item)));
    }

    @Override
    public Optional<RegistryScreenEventItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryScreenEventItem> findByScreenIdAndStatusOrderByDisplayOrderAsc(UUID screenId, String status) {
        return springData.findByScreenIdAndStatusOrderByDisplayOrderAsc(screenId, status)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryScreenEventItem> findByScreenIdOrderByDisplayOrderAsc(UUID screenId) {
        return springData.findByScreenIdOrderByDisplayOrderAsc(screenId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
