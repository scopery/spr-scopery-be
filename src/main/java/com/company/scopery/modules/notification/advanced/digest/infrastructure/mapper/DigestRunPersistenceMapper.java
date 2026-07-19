package com.company.scopery.modules.notification.advanced.digest.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.digest.domain.model.DigestRun;
import com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence.DigestRunJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DigestRunPersistenceMapper {
    public DigestRun toDomain(DigestRunJpaEntity e) {
        return new DigestRun(e.getId(), e.getWorkspaceId(), e.getDigestRuleId(), e.getRecipientUserId(),
                e.getStatus(), e.getNotificationCount(), e.getSentAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DigestRunJpaEntity toJpa(DigestRun d) {
        var e = new DigestRunJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setDigestRuleId(d.digestRuleId());
        e.setRecipientUserId(d.recipientUserId()); e.setStatus(d.status());
        e.setNotificationCount(d.notificationCount()); e.setSentAt(d.sentAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
