package com.company.scopery.modules.iam.ownerpolicy.domain.model;

import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;

import java.util.List;
import java.util.Optional;

public interface IamOwnerPolicyRepository {
    IamOwnerPolicy save(IamOwnerPolicy policy);
    Optional<IamOwnerPolicy> findActiveByResourceType(IamResourceType resourceType);
    List<IamOwnerPolicy> findAll();
}
