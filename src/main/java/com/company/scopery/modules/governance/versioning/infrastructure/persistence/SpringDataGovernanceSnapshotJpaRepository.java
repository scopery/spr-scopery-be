package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface SpringDataGovernanceSnapshotJpaRepository extends JpaRepository<GovernanceSnapshotJpaEntity, UUID> {}
