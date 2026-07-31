package com.company.scopery.modules.quality.verificationcase.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface VerificationCaseRepository {
    VerificationCase save(VerificationCase e);
    Optional<VerificationCase> findByIdAndProjectId(UUID id, UUID projectId);
    List<VerificationCaseListRow> searchList(UUID projectId, UUID requirementId, String status,
            UUID assigneeId, String orderBy, int limit, long offset);
    long countSearch(UUID projectId, UUID requirementId, String status, UUID assigneeId);
}
