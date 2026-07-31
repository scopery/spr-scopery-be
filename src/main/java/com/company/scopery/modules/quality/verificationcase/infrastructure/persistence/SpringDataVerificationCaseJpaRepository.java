package com.company.scopery.modules.quality.verificationcase.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataVerificationCaseJpaRepository extends JpaRepository<VerificationCaseJpaEntity, UUID> {
    Optional<VerificationCaseJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
