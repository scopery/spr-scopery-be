package com.company.scopery.modules.iam.ownerpolicy.infrastructure.persistence;

import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamOwnerPolicyStatus;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import com.company.scopery.modules.iam.ownerpolicy.infrastructure.mapper.IamOwnerPolicyPersistenceMapper;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaIamOwnerPolicyRepository implements IamOwnerPolicyRepository {
    private final SpringDataIamOwnerPolicyJpaRepository repository;
    private final IamOwnerPolicyPersistenceMapper mapper;
    public JpaIamOwnerPolicyRepository(SpringDataIamOwnerPolicyJpaRepository repository,
                                       IamOwnerPolicyPersistenceMapper mapper) {
        this.repository = repository; this.mapper = mapper;
    }
    public IamOwnerPolicy save(IamOwnerPolicy policy) {
        return mapper.toDomain(repository.saveAndFlush(mapper.toJpaEntity(policy)));
    }
    public Optional<IamOwnerPolicy> findActiveByResourceType(IamResourceType resourceType) {
        return repository.findByResourceTypeAndStatus(resourceType.name(), IamOwnerPolicyStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }
    public List<IamOwnerPolicy> findAll() { return repository.findAll().stream().map(mapper::toDomain).toList(); }
}
