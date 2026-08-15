package com.company.scopery.modules.traceability.componentfield.infrastructure.persistence;

import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.componentfield.infrastructure.mapper.RegistryComponentFieldPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryComponentFieldRepository implements RegistryComponentFieldRepository {

    private final SpringDataRegistryComponentFieldJpaRepository springData;
    private final RegistryComponentFieldPersistenceMapper mapper;

    public JpaRegistryComponentFieldRepository(SpringDataRegistryComponentFieldJpaRepository springData,
                                                RegistryComponentFieldPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<RegistryComponentField> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByComponentIdAndFieldKey(UUID componentId, String fieldKey) {
        return springData.existsByComponentIdAndFieldKey(componentId, fieldKey);
    }

    @Override
    public List<RegistryComponentField> findByComponentIdOrderByDisplayOrderAsc(UUID componentId) {
        return springData.findByComponentIdOrderByDisplayOrderAsc(componentId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public RegistryComponentField save(RegistryComponentField field) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(field)));
    }

    @Override
    public void delete(UUID id) {
        springData.deleteById(id);
    }
}
