package com.company.scopery.common.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLogJpaEntity, UUID> {
}
