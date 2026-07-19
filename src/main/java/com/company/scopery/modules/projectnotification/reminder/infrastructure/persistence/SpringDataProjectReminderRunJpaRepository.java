package com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectReminderRunJpaRepository
        extends JpaRepository<ProjectReminderRunJpaEntity, UUID> {
    boolean existsByStatus(String status);
    List<ProjectReminderRunJpaEntity> findAllByOrderByStartedAtDesc(Pageable pageable);
}
