package com.company.scopery.modules.trust.retention.application.response;
import com.company.scopery.modules.trust.retention.domain.model.RetentionJob;
import java.util.UUID;
public record RetentionJobResponse(UUID id, UUID retentionPolicyId, String jobMode, String status, long candidateCount, long actionedCount, long skippedLegalHoldCount) {
    public static RetentionJobResponse from(RetentionJob j){
        return new RetentionJobResponse(j.id(), j.retentionPolicyId(), j.jobMode(), j.status(), j.candidateCount(), j.actionedCount(), j.skippedLegalHoldCount());
    }
}
