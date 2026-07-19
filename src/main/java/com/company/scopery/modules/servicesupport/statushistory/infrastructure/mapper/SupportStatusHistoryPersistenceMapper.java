package com.company.scopery.modules.servicesupport.statushistory.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.statushistory.domain.model.SupportStatusHistory;
import com.company.scopery.modules.servicesupport.statushistory.infrastructure.persistence.SupportStatusHistoryJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportStatusHistoryPersistenceMapper {
    public SupportStatusHistoryJpaEntity toJpa(SupportStatusHistory d) {
        var e = new SupportStatusHistoryJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId());
        e.setFromStatus(d.fromStatus()); e.setToStatus(d.toStatus()); e.setReason(d.reason());
        e.setChangedAt(d.changedAt()); e.setChangedBy(d.changedBy()); e.setVisibility(d.visibility());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportStatusHistory toDomain(SupportStatusHistoryJpaEntity e) {
        return new SupportStatusHistory(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getFromStatus(),
                e.getToStatus(), e.getReason(), e.getChangedAt(), e.getChangedBy(), e.getVisibility(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
