package com.company.scopery.modules.resourcecapacity.workingcalendar.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataWorkingCalendarJpaRepository
        extends JpaRepository<WorkingCalendarJpaEntity, UUID>, JpaSpecificationExecutor<WorkingCalendarJpaEntity> {

    Optional<WorkingCalendarJpaEntity> findByWorkspaceIdAndCode(UUID workspaceId, String code);

    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);

    Optional<WorkingCalendarJpaEntity> findByWorkspaceIdAndIsDefaultTrueAndStatus(UUID workspaceId, String status);

    List<WorkingCalendarJpaEntity> findAllByWorkspaceIdAndIsDefaultTrueAndStatus(UUID workspaceId, String status);

    @Query("SELECT COUNT(p) > 0 FROM com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.persistence.UserCapacityProfileJpaEntity p "
            + "WHERE p.workingCalendarId = :calendarId")
    boolean existsProfileByCalendarId(@Param("calendarId") UUID calendarId);
}
