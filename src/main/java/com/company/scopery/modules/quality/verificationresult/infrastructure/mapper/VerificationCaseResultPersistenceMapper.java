package com.company.scopery.modules.quality.verificationresult.infrastructure.mapper;
import com.company.scopery.modules.quality.verificationresult.domain.enums.VerificationResultStatus;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResult;
import com.company.scopery.modules.quality.verificationresult.infrastructure.persistence.VerificationCaseResultJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class VerificationCaseResultPersistenceMapper {
    public VerificationCaseResult toDomain(VerificationCaseResultJpaEntity e) {
        return new VerificationCaseResult(e.getId(), e.getProjectId(), e.getTestRunId(), e.getVerificationCaseId(),
                VerificationResultStatus.valueOf(e.getResultStatus()),
                e.getActualValue(), e.getActualValueUnit(), e.getActualResultJson(), e.getEvidenceReference(),
                e.getExecutedAt(), e.getExecutedById(), e.getDefectId(), e.getComment(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public VerificationCaseResultJpaEntity toJpaEntity(VerificationCaseResult d) {
        VerificationCaseResultJpaEntity e = new VerificationCaseResultJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestRunId(d.testRunId()); e.setVerificationCaseId(d.verificationCaseId());
        e.setResultStatus(d.resultStatus().name()); e.setActualValue(d.actualValue()); e.setActualValueUnit(d.actualValueUnit());
        e.setActualResultJson(d.actualResultJson()); e.setEvidenceReference(d.evidenceReference());
        e.setExecutedAt(d.executedAt()); e.setExecutedById(d.executedById()); e.setDefectId(d.defectId());
        e.setComment(d.comment());
        e.setVersion(d.version() >= 0 ? d.version() : null);
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
