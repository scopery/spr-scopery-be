package com.company.scopery.modules.trust.privacy.infrastructure.mapper;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequest;
import com.company.scopery.modules.trust.privacy.infrastructure.persistence.PrivacyRequestJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class PrivacyRequestPersistenceMapper {
    public PrivacyRequestJpaEntity toJpaEntity(PrivacyRequest d) {
        PrivacyRequestJpaEntity e = new PrivacyRequestJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setRequestCode(d.requestCode());
        e.setRequestType(d.requestType()); e.setStatus(d.status()); e.setSubjectReference(d.subjectReference());
        e.setAssignedOwnerUserId(d.assignedOwnerUserId()); e.setResolutionSummary(d.resolutionSummary());
        e.setRejectionReason(d.rejectionReason()); e.setCompletedAt(d.completedAt()); e.setVersion(d.version());
        e.setCreatedAt(d.createdAt()); return e;
    }
    public PrivacyRequest toDomain(PrivacyRequestJpaEntity e) {
        return new PrivacyRequest(e.getId(), e.getWorkspaceId(), e.getRequestCode(), e.getRequestType(), e.getStatus(),
                e.getSubjectReference(), e.getAssignedOwnerUserId(), e.getResolutionSummary(), e.getRejectionReason(),
                e.getCompletedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
