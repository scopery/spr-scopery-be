package com.company.scopery.modules.resourcecapacity.dayrule.infrastructure.mapper;

import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.infrastructure.persistence.CalendarDayRuleJpaEntity;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class CalendarDayRulePersistenceMapper {

    public CalendarDayRule toDomain(CalendarDayRuleJpaEntity entity) {
        return new CalendarDayRule(
                entity.getId(),
                entity.getWorkingCalendarId(),
                DayOfWeek.valueOf(entity.getDayOfWeek()),
                entity.isWorkingDay(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getWorkingHours(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CalendarDayRuleJpaEntity toJpaEntity(CalendarDayRule domain) {
        CalendarDayRuleJpaEntity entity = new CalendarDayRuleJpaEntity();
        entity.setId(domain.id());
        entity.setWorkingCalendarId(domain.workingCalendarId());
        entity.setDayOfWeek(domain.dayOfWeek().name());
        entity.setWorkingDay(domain.isWorkingDay());
        entity.setStartTime(domain.startTime());
        entity.setEndTime(domain.endTime());
        entity.setWorkingHours(domain.workingHours());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
