package com.company.scopery.modules.resourcecapacity.calendarexception.infrastructure.mapper;

import com.company.scopery.modules.resourcecapacity.calendarexception.domain.enums.CalendarExceptionType;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.infrastructure.persistence.CalendarExceptionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CalendarExceptionPersistenceMapper {

    public CalendarException toDomain(CalendarExceptionJpaEntity entity) {
        return new CalendarException(
                entity.getId(),
                entity.getWorkingCalendarId(),
                entity.getExceptionDate(),
                CalendarExceptionType.valueOf(entity.getExceptionType()),
                entity.getName(),
                entity.getDescription(),
                entity.isWorkingDay(),
                entity.getWorkingHours(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CalendarExceptionJpaEntity toJpaEntity(CalendarException domain) {
        CalendarExceptionJpaEntity entity = new CalendarExceptionJpaEntity();
        entity.setId(domain.id());
        entity.setWorkingCalendarId(domain.workingCalendarId());
        entity.setExceptionDate(domain.exceptionDate());
        entity.setExceptionType(domain.exceptionType().name());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setWorkingDay(domain.isWorkingDay());
        entity.setWorkingHours(domain.workingHours());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
