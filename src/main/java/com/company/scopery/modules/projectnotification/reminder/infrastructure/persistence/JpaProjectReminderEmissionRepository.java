package com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence;

import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderEmission;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderEmissionRepository;
import com.company.scopery.modules.projectnotification.reminder.infrastructure.mapper.ProjectReminderPersistenceMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProjectReminderEmissionRepository implements ProjectReminderEmissionRepository {
    private final SpringDataProjectReminderEmissionJpaRepository spring;
    private final ProjectReminderPersistenceMapper mapper;

    public JpaProjectReminderEmissionRepository(SpringDataProjectReminderEmissionJpaRepository spring,
                                                ProjectReminderPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public ProjectReminderEmission save(ProjectReminderEmission emission) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(emission)));
    }

    @Override
    public boolean existsByDedupKey(String dedupKey) {
        return spring.existsByDedupKey(dedupKey);
    }
}
