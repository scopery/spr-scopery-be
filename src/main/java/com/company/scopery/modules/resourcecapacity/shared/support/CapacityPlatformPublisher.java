package com.company.scopery.modules.resourcecapacity.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.shared.listeners.CapacityEventDefinitionSeedInitializer;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CapacityPlatformPublisher {

    public static final String AGGREGATE_WORKING_CALENDAR = "WORKING_CALENDAR";
    public static final String AGGREGATE_CALENDAR_EXCEPTION = "CALENDAR_EXCEPTION";
    public static final String AGGREGATE_USER_CAPACITY_PROFILE = "USER_CAPACITY_PROFILE";
    public static final String AGGREGATE_PROJECT_RESOURCE_ALLOCATION = "PROJECT_RESOURCE_ALLOCATION";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public CapacityPlatformPublisher(TransactionalOutboxService outboxService,
                                     ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueueCalendar(WorkingCalendar calendar, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_WORKING_CALENDAR,
                calendar.id(),
                eventCode,
                CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                calendarPayload(calendar));
    }

    public void enqueueException(CalendarException exception, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_CALENDAR_EXCEPTION,
                exception.id(),
                eventCode,
                CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                exceptionPayload(exception));
    }

    public void enqueueProfile(UserCapacityProfile profile, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_USER_CAPACITY_PROFILE,
                profile.id(),
                eventCode,
                CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                profilePayload(profile));
    }

    public void enqueueAllocation(ProjectResourceAllocation allocation, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_PROJECT_RESOURCE_ALLOCATION,
                allocation.id(),
                eventCode,
                CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                allocationPayload(allocation));
    }

    public void audit(AuditEventType type, UUID actorUserId, String aggregateType, UUID aggregateId,
                      UUID organizationId, UUID workspaceId, Map<String, Object> payload, String summary) {
        auditEventService.record(type, actorUserId, "USER",
                aggregateType, aggregateId, organizationId, workspaceId, null, payload, summary);
    }

    private Map<String, Object> calendarPayload(WorkingCalendar calendar) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", calendar.id());
        map.put("workspaceId", calendar.workspaceId());
        map.put("code", calendar.code());
        map.put("name", calendar.name());
        map.put("timezone", calendar.timezone());
        map.put("isDefault", calendar.isDefault());
        map.put("status", calendar.status().name());
        return map;
    }

    private Map<String, Object> exceptionPayload(CalendarException exception) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", exception.id());
        map.put("workingCalendarId", exception.workingCalendarId());
        map.put("exceptionDate", exception.exceptionDate().toString());
        map.put("exceptionType", exception.exceptionType().name());
        map.put("isWorkingDay", exception.isWorkingDay());
        map.put("workingHours", exception.workingHours());
        return map;
    }

    private Map<String, Object> profilePayload(UserCapacityProfile profile) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", profile.id());
        map.put("workspaceId", profile.workspaceId());
        map.put("workspaceMemberId", profile.workspaceMemberId());
        map.put("userId", profile.userId());
        map.put("workingCalendarId", profile.workingCalendarId());
        map.put("defaultDailyHours", profile.defaultDailyHours());
        map.put("focusFactor", profile.focusFactor());
        map.put("capacityStatus", profile.capacityStatus().name());
        return map;
    }

    private Map<String, Object> allocationPayload(ProjectResourceAllocation allocation) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", allocation.id());
        map.put("workspaceId", allocation.workspaceId());
        map.put("projectId", allocation.projectId());
        map.put("workspaceMemberId", allocation.workspaceMemberId());
        map.put("userId", allocation.userId());
        map.put("allocationPercent", allocation.allocationPercent());
        map.put("status", allocation.status().name());
        map.put("startDate", allocation.startDate().toString());
        if (allocation.endDate() != null) {
            map.put("endDate", allocation.endDate().toString());
        }
        return map;
    }
}
