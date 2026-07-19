package com.company.scopery.modules.collaboration.minutes.infrastructure.mapper;
import com.company.scopery.modules.collaboration.minutes.domain.enums.MinutesStatus;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutes;
import com.company.scopery.modules.collaboration.minutes.infrastructure.persistence.MeetingMinutesJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingMinutesPersistenceMapper {
    public MeetingMinutes toDomain(MeetingMinutesJpaEntity e) {
        return new MeetingMinutes(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingId(), MinutesStatus.valueOf(e.getStatus()),
                e.getSummary(), e.getDecisionsSummary(), e.getActionsSummary(), e.getClientVisibleSummary(), e.getDocumentId(), e.getDocumentVersionId(),
                e.getSubmittedAt(), e.getSubmittedBy(), e.getApprovedAt(), e.getApprovedBy(), e.getRejectedAt(), e.getRejectedBy(),
                e.getRejectionReason(), e.getTraceId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingMinutesJpaEntity toJpaEntity(MeetingMinutes d) {
        MeetingMinutesJpaEntity e = new MeetingMinutesJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setMeetingId(d.meetingId());
        e.setStatus(d.status().name()); e.setSummary(d.summary()); e.setDecisionsSummary(d.decisionsSummary());
        e.setActionsSummary(d.actionsSummary()); e.setClientVisibleSummary(d.clientVisibleSummary());
        e.setDocumentId(d.documentId()); e.setDocumentVersionId(d.documentVersionId());
        e.setSubmittedAt(d.submittedAt()); e.setSubmittedBy(d.submittedBy()); e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setRejectedAt(d.rejectedAt()); e.setRejectedBy(d.rejectedBy()); e.setRejectionReason(d.rejectionReason());
        e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
