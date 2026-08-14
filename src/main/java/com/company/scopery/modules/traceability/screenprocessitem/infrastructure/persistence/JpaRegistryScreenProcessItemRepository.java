package com.company.scopery.modules.traceability.screenprocessitem.infrastructure.persistence;

import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItem;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.screenprocessitem.infrastructure.mapper.RegistryScreenProcessItemPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryScreenProcessItemRepository implements RegistryScreenProcessItemRepository {

    private final SpringDataRegistryScreenProcessItemJpaRepository springData;
    private final RegistryScreenProcessItemPersistenceMapper mapper;

    public JpaRegistryScreenProcessItemRepository(SpringDataRegistryScreenProcessItemJpaRepository springData,
                                                  RegistryScreenProcessItemPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryScreenProcessItem save(RegistryScreenProcessItem item) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item)));
    }

    @Override
    public Optional<RegistryScreenProcessItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryScreenProcessItem> findByScreenIdAndStatusOrderByDisplayOrderAsc(UUID screenId, String status) {
        return springData.findByScreenIdAndStatusOrderByDisplayOrderAsc(screenId, status)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryScreenProcessItem> findByScreenIdOrderByDisplayOrderAsc(UUID screenId) {
        return springData.findByScreenIdOrderByDisplayOrderAsc(screenId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
