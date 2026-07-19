package com.company.scopery.modules.trust.classification.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataDataClassificationPolicyJpaRepository extends JpaRepository<DataClassificationPolicyJpaEntity, UUID> {
    Optional<DataClassificationPolicyJpaEntity> findByWorkspaceId(UUID workspaceId);
}
