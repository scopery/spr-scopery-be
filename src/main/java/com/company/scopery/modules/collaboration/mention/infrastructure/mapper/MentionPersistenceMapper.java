package com.company.scopery.modules.collaboration.mention.infrastructure.mapper;
import com.company.scopery.modules.collaboration.mention.domain.enums.*;
import com.company.scopery.modules.collaboration.mention.domain.model.Mention;
import com.company.scopery.modules.collaboration.mention.infrastructure.persistence.MentionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MentionPersistenceMapper {
    public Mention toDomain(MentionJpaEntity e) {
        return new Mention(e.getId(), e.getWorkspaceId(), e.getProjectId(), MentionSourceType.valueOf(e.getSourceType()), e.getSourceId(),
                MentionTargetType.valueOf(e.getTargetType()), e.getTargetId(), e.isNotificationSent(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MentionJpaEntity toJpaEntity(Mention d) {
        MentionJpaEntity e = new MentionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setSourceType(d.sourceType().name());
        e.setSourceId(d.sourceId()); e.setTargetType(d.targetType().name()); e.setTargetId(d.targetId()); e.setNotificationSent(d.notificationSent()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
