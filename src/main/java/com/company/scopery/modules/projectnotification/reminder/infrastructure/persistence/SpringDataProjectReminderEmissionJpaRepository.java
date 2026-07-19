package com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataProjectReminderEmissionJpaRepository
        extends JpaRepository<ProjectReminderEmissionJpaEntity, UUID> {
    boolean existsByDedupKey(String dedupKey);
}
