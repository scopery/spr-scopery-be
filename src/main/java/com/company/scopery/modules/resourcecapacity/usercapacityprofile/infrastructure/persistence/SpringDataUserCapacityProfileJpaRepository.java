package com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserCapacityProfileJpaRepository
        extends JpaRepository<UserCapacityProfileJpaEntity, UUID>,
        JpaSpecificationExecutor<UserCapacityProfileJpaEntity> {

    List<UserCapacityProfileJpaEntity> findByWorkspaceMemberIdAndCapacityStatus(UUID workspaceMemberId, String capacityStatus);

    Optional<UserCapacityProfileJpaEntity> findFirstByUserIdAndCapacityStatus(UUID userId, String capacityStatus);

    boolean existsByWorkingCalendarId(UUID workingCalendarId);
}
