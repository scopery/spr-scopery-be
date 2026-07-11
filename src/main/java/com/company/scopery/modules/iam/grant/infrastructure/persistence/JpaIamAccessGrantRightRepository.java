package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.infrastructure.mapper.IamAccessGrantRightPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class JpaIamAccessGrantRightRepository implements IamAccessGrantRightRepository {

    private final SpringDataIamAccessGrantRightJpaRepository springDataRepository;
    private final IamAccessGrantRightPersistenceMapper mapper;

    public JpaIamAccessGrantRightRepository(SpringDataIamAccessGrantRightJpaRepository springDataRepository,
                                             IamAccessGrantRightPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamAccessGrantRight save(IamAccessGrantRight grantRight) {
        IamAccessGrantRightJpaEntity entity = mapper.toJpaEntity(grantRight);
        IamAccessGrantRightJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByGrantIdAndRightId(UUID grantId, UUID rightId) {
        return springDataRepository.existsByGrantIdAndRightId(grantId, rightId);
    }

    @Override
    public void deleteByGrantIdAndRightId(UUID grantId, UUID rightId) {
        springDataRepository.deleteByGrantIdAndRightId(grantId, rightId);
    }

    @Override
    public List<IamAccessGrantRight> findByGrantId(UUID grantId) {
        return springDataRepository.findByGrantId(grantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Set<UUID> findGrantIdsHavingRight(List<UUID> grantIds, UUID rightId) {
        if (grantIds.isEmpty()) return Set.of();
        return springDataRepository.findGrantIdsHavingRight(grantIds, rightId);
    }
}
