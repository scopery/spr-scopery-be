package com.company.scopery.modules.iam.permission.infrastructure.persistence;

import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.infrastructure.mapper.IamPermissionActionDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaIamPermissionActionDefinitionRepository implements IamPermissionActionDefinitionRepository {

    private final SpringDataIamPermissionActionDefinitionJpaRepository springDataRepository;
    private final IamPermissionActionDefinitionPersistenceMapper mapper;

    public JpaIamPermissionActionDefinitionRepository(
            SpringDataIamPermissionActionDefinitionJpaRepository springDataRepository,
            IamPermissionActionDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamPermissionActionDefinition save(IamPermissionActionDefinition action) {
        IamPermissionActionDefinitionJpaEntity entity = mapper.toJpaEntity(action);
        IamPermissionActionDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamPermissionActionDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<IamPermissionActionDefinition> findByPermissionIdAndActionCode(UUID permissionId,
                                                                                   String actionCode) {
        return springDataRepository.findByPermissionIdAndActionCode(permissionId, actionCode)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByPermissionIdAndActionCode(UUID permissionId, String actionCode) {
        return springDataRepository.existsByPermissionIdAndActionCode(permissionId, actionCode);
    }

    @Override
    public List<IamPermissionActionDefinition> findByPermissionId(UUID permissionId) {
        return springDataRepository.findByPermissionIdOrderByActionCodeAsc(permissionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<IamPermissionActionDefinition> findByPermissionIds(List<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        return springDataRepository.findByPermissionIdInOrderByActionCodeAsc(permissionIds)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<IamPermissionActionDefinition> findByRightIds(List<UUID> rightIds) {
        if (rightIds == null || rightIds.isEmpty()) {
            return List.of();
        }
        return springDataRepository.findByRightIdIn(rightIds)
                .stream().map(mapper::toDomain).toList();
    }
}
