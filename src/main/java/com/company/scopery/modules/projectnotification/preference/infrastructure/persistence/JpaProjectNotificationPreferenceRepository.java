package com.company.scopery.modules.projectnotification.preference.infrastructure.persistence;

import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreference;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreferenceRepository;
import com.company.scopery.modules.projectnotification.preference.infrastructure.mapper.ProjectNotificationPreferencePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectNotificationPreferenceRepository implements ProjectNotificationPreferenceRepository {
    private final SpringDataProjectNotificationPreferenceJpaRepository spring;
    private final ProjectNotificationPreferencePersistenceMapper mapper;

    public JpaProjectNotificationPreferenceRepository(
            SpringDataProjectNotificationPreferenceJpaRepository spring,
            ProjectNotificationPreferencePersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public ProjectNotificationPreference save(ProjectNotificationPreference preference) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(preference)));
    }

    @Override
    public List<ProjectNotificationPreference> findByProjectIdAndUserId(UUID projectId, UUID userId) {
        return spring.findByProjectIdAndUserId(projectId, userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProjectNotificationPreference> findByTaskIdAndUserId(UUID taskId, UUID userId) {
        return spring.findByTaskIdAndUserId(taskId, userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProjectNotificationPreference> findMatching(
            UUID projectId, UUID taskId, UUID userId, String eventCode, PreferenceChannel channel) {
        return spring.findFirstByProjectIdAndTaskIdAndUserIdAndEventCodeAndChannel(
                projectId, taskId, userId, eventCode, channel.name()).map(mapper::toDomain);
    }
}
