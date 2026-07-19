package com.company.scopery.modules.servicesupport.problem.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecord;
import com.company.scopery.modules.servicesupport.problem.infrastructure.persistence.SupportProblemRecordJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportProblemRecordPersistenceMapper {
    public SupportProblemRecordJpaEntity toJpa(SupportProblemRecord d) {
        var e = new SupportProblemRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setServiceProfileId(d.serviceProfileId()); e.setProblemNumber(d.problemNumber());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setStatus(d.status());
        e.setRootCauseSummary(d.rootCauseSummary()); e.setWorkaround(d.workaround());
        e.setOwnerUserId(d.ownerUserId()); e.setResolvedAt(d.resolvedAt()); e.setResolvedBy(d.resolvedBy());
        e.setClosedAt(d.closedAt()); e.setClosedBy(d.closedBy()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportProblemRecord toDomain(SupportProblemRecordJpaEntity e) {
        return new SupportProblemRecord(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getServiceProfileId(),
                e.getProblemNumber(), e.getTitle(), e.getDescription(), e.getStatus(),
                e.getRootCauseSummary(), e.getWorkaround(), e.getOwnerUserId(),
                e.getResolvedAt(), e.getResolvedBy(), e.getClosedAt(), e.getClosedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
