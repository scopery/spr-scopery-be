package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.infrastructure.mapper.IamAccessGrantPermissionActionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class JpaIamAccessGrantPermissionActionRepository implements IamAccessGrantPermissionActionRepository {

    private final SpringDataIamAccessGrantPermissionActionJpaRepository springDataRepository;
    private final IamAccessGrantPermissionActionPersistenceMapper mapper;

    public JpaIamAccessGrantPermissionActionRepository(
            SpringDataIamAccessGrantPermissionActionJpaRepository springDataRepository,
            IamAccessGrantPermissionActionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamAccessGrantPermissionAction save(IamAccessGrantPermissionAction grantAction) {
        IamAccessGrantPermissionActionJpaEntity entity = mapper.toJpaEntity(grantAction);
        IamAccessGrantPermissionActionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByGrantIdAndPermissionActionId(UUID grantId, UUID permissionActionId) {
        return springDataRepository.existsByGrantIdAndPermissionActionId(grantId, permissionActionId);
    }

    @Override
    public void deleteByGrantIdAndPermissionActionId(UUID grantId, UUID permissionActionId) {
        springDataRepository.deleteByGrantIdAndPermissionActionId(grantId, permissionActionId);
    }

    @Override
    public List<IamAccessGrantPermissionAction> findByGrantId(UUID grantId) {
        return springDataRepository.findByGrantId(grantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Set<UUID> findGrantIdsHavingPermissionAction(List<UUID> grantIds, UUID permissionActionId) {
        if (grantIds.isEmpty()) return Set.of();
        return springDataRepository.findGrantIdsHavingPermissionAction(grantIds, permissionActionId);
    }

    @Override
    public Set<UUID> findGrantIdsHavingAnyPermissionAction(List<UUID> grantIds, List<UUID> permissionActionIds) {
        if (grantIds.isEmpty() || permissionActionIds.isEmpty()) return Set.of();
        return springDataRepository.findGrantIdsHavingAnyPermissionAction(grantIds, permissionActionIds);
    }
}
