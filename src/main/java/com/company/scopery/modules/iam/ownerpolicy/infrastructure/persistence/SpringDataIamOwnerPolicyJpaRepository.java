package com.company.scopery.modules.iam.ownerpolicy.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataIamOwnerPolicyJpaRepository extends JpaRepository<IamOwnerPolicyJpaEntity, UUID> {
    Optional<IamOwnerPolicyJpaEntity> findByResourceTypeAndStatus(String resourceType, String status);
}
