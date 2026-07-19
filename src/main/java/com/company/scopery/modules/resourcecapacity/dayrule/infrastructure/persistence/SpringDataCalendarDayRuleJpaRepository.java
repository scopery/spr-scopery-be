package com.company.scopery.modules.resourcecapacity.dayrule.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataCalendarDayRuleJpaRepository extends JpaRepository<CalendarDayRuleJpaEntity, UUID> {

    List<CalendarDayRuleJpaEntity> findByWorkingCalendarId(UUID workingCalendarId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM CalendarDayRuleJpaEntity e WHERE e.workingCalendarId = :workingCalendarId")
    int deleteByWorkingCalendarId(@Param("workingCalendarId") UUID workingCalendarId);
}
