package com.company.scopery.modules.quality.verificationresult.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataVerificationCaseResultJpaRepository extends JpaRepository<VerificationCaseResultJpaEntity, UUID> {
    Optional<VerificationCaseResultJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<VerificationCaseResultJpaEntity> findByTestRunIdAndVerificationCaseId(UUID testRunId, UUID verificationCaseId);
    List<VerificationCaseResultJpaEntity> findByTestRunIdAndProjectId(UUID testRunId, UUID projectId);
}
