package com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence;

import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderRunStatus;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRun;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRunRepository;
import com.company.scopery.modules.projectnotification.reminder.infrastructure.mapper.ProjectReminderPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectReminderRunRepository implements ProjectReminderRunRepository {
    private final SpringDataProjectReminderRunJpaRepository spring;
    private final ProjectReminderPersistenceMapper mapper;

    public JpaProjectReminderRunRepository(SpringDataProjectReminderRunJpaRepository spring,
                                           ProjectReminderPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public ProjectReminderRun save(ProjectReminderRun run) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(run)));
    }

    @Override
    public Optional<ProjectReminderRun> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProjectReminderRun> findRecent(int limit) {
        return spring.findAllByOrderByStartedAtDesc(PageRequest.of(0, limit))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByStatus(ReminderRunStatus status) {
        return spring.existsByStatus(status.name());
    }
}
