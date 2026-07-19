package com.company.scopery.modules.traceability.screenfield.infrastructure.persistence;
import com.company.scopery.modules.traceability.screenfield.domain.model.*;
import com.company.scopery.modules.traceability.screenfield.infrastructure.mapper.RegistryScreenFieldPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryScreenFieldRepository implements RegistryScreenFieldRepository {
    private final SpringDataRegistryScreenFieldJpaRepository springData;
    private final RegistryScreenFieldPersistenceMapper mapper;
    public JpaRegistryScreenFieldRepository(SpringDataRegistryScreenFieldJpaRepository springData, RegistryScreenFieldPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryScreenField save(RegistryScreenField e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryScreenField> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryScreenField> findByScreenId(UUID screenId) {
        return springData.findByScreenIdOrderByDisplayOrderAsc(screenId).stream().map(mapper::toDomain).toList();
    }
}
