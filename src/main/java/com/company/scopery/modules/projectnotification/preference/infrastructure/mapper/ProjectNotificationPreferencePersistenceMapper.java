package com.company.scopery.modules.projectnotification.preference.infrastructure.mapper;

import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreference;
import com.company.scopery.modules.projectnotification.preference.infrastructure.persistence.ProjectNotificationPreferenceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectNotificationPreferencePersistenceMapper {
    public ProjectNotificationPreference toDomain(ProjectNotificationPreferenceJpaEntity e) {
        return new ProjectNotificationPreference(
                e.getId(), e.getProjectId(), e.getTaskId(), e.getWorkspaceId(), e.getUserId(),
                e.getEventCode(), PreferenceChannel.valueOf(e.getChannel()),
                e.isEnabled(), e.isMuted(), e.isMandatoryOverride(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectNotificationPreferenceJpaEntity toJpaEntity(ProjectNotificationPreference d) {
        ProjectNotificationPreferenceJpaEntity e = new ProjectNotificationPreferenceJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setTaskId(d.taskId());
        e.setWorkspaceId(d.workspaceId());
        e.setUserId(d.userId());
        e.setEventCode(d.eventCode());
        e.setChannel(d.channel().name());
        e.setEnabled(d.enabled());
        e.setMuted(d.muted());
        e.setMandatoryOverride(d.mandatoryOverride());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
