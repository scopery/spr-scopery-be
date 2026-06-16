package com.company.scopery.modules.iam.grant.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IamAccessGrantRightRepository {

    IamAccessGrantRight save(IamAccessGrantRight grantRight);

    boolean existsByGrantIdAndRightId(UUID grantId, UUID rightId);

    void deleteByGrantIdAndRightId(UUID grantId, UUID rightId);

    List<IamAccessGrantRight> findByGrantId(UUID grantId);

    Set<UUID> findGrantIdsHavingRight(List<UUID> grantIds, UUID rightId);

    Set<UUID> findGrantIdsHavingAnyRight(List<UUID> grantIds);
}
