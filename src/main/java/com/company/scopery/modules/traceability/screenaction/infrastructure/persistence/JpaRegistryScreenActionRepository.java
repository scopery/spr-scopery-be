package com.company.scopery.modules.traceability.screenaction.infrastructure.persistence;
import com.company.scopery.modules.traceability.screenaction.domain.model.*;
import com.company.scopery.modules.traceability.screenaction.infrastructure.mapper.RegistryScreenActionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryScreenActionRepository implements RegistryScreenActionRepository {
    private final SpringDataRegistryScreenActionJpaRepository springData;
    private final RegistryScreenActionPersistenceMapper mapper;
    public JpaRegistryScreenActionRepository(SpringDataRegistryScreenActionJpaRepository springData, RegistryScreenActionPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryScreenAction save(RegistryScreenAction e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryScreenAction> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryScreenAction> findByScreenId(UUID screenId) {
        return springData.findByScreenIdOrderByDisplayOrderAsc(screenId).stream().map(mapper::toDomain).toList();
    }
}
