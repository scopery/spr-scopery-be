package com.company.scopery.modules.quality.verificationresult.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface VerificationCaseResultRepository {
    VerificationCaseResult save(VerificationCaseResult r);
    Optional<VerificationCaseResult> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<VerificationCaseResult> findByTestRunIdAndVerificationCaseId(UUID testRunId, UUID verificationCaseId);
    List<VerificationCaseResult> findByTestRunIdAndProjectId(UUID testRunId, UUID projectId);
}
