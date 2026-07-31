package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataTestRunMembershipJpaRepository extends JpaRepository<TestRunMembershipJpaEntity, UUID> {
    List<TestRunMembershipJpaEntity> findByTestRunIdOrderByDisplayOrderAsc(UUID testRunId);
    boolean existsByTestRunIdAndCaseKindAndCaseId(UUID testRunId, String caseKind, UUID caseId);
    void deleteByTestRunIdAndCaseKindAndCaseId(UUID testRunId, String caseKind, UUID caseId);
    void deleteByTestRunId(UUID testRunId);
    int countByTestRunId(UUID testRunId);
}
