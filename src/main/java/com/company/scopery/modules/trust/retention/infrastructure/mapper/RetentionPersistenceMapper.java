package com.company.scopery.modules.trust.retention.infrastructure.mapper;
import com.company.scopery.modules.trust.retention.domain.model.*;
import com.company.scopery.modules.trust.retention.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class RetentionPersistenceMapper {
    public RetentionPolicyJpaEntity toJpa(RetentionPolicy d) {
        RetentionPolicyJpaEntity e = new RetentionPolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setPolicyCode(d.policyCode()); e.setName(d.name());
        e.setObjectTypeCode(d.objectTypeCode()); e.setRetentionPeriodDays(d.retentionPeriodDays());
        e.setRetentionAction(d.retentionAction()); e.setReviewRequired(d.reviewRequired()); e.setEnabled(d.enabled());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public RetentionPolicy toDomain(RetentionPolicyJpaEntity e) {
        return new RetentionPolicy(e.getId(), e.getWorkspaceId(), e.getPolicyCode(), e.getName(), e.getObjectTypeCode(),
                e.getRetentionPeriodDays()==null?0:e.getRetentionPeriodDays(), e.getRetentionAction(),
                Boolean.TRUE.equals(e.getReviewRequired()), Boolean.TRUE.equals(e.getEnabled()),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public RetentionJobJpaEntity toJpa(RetentionJob d) {
        RetentionJobJpaEntity e = new RetentionJobJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setRetentionPolicyId(d.retentionPolicyId());
        e.setJobMode(d.jobMode()); e.setStatus(d.status()); e.setCandidateCount(d.candidateCount());
        e.setActionedCount(d.actionedCount()); e.setSkippedLegalHoldCount(d.skippedLegalHoldCount());
        e.setCompletedAt(d.completedAt()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public RetentionJob toDomain(RetentionJobJpaEntity e) {
        return new RetentionJob(e.getId(), e.getWorkspaceId(), e.getRetentionPolicyId(), e.getJobMode(), e.getStatus(),
                e.getCandidateCount()==null?0:e.getCandidateCount(), e.getActionedCount()==null?0:e.getActionedCount(),
                e.getSkippedLegalHoldCount()==null?0:e.getSkippedLegalHoldCount(), e.getCompletedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
}
