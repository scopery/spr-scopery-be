package com.company.scopery.common.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ImmutableAuditEventService {
    private static final Logger log = LoggerFactory.getLogger(ImmutableAuditEventService.class);
    private final ImmutableAuditEventJpaRepository repository;
    private final ObjectMapper objectMapper;
    public ImmutableAuditEventService(ImmutableAuditEventJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository; this.objectMapper = objectMapper;
    }

    public void record(AuditEventType eventType, UUID actorId, String actorType,
                       String resourceType, UUID resourceRefId, UUID organizationId,
                       UUID workspaceId, Object beforeState, Object afterState, String reason) {
        try {
            ImmutableAuditEventJpaEntity entity = new ImmutableAuditEventJpaEntity();
            entity.setId(UUID.randomUUID()); entity.setEventType(eventType.name());
            entity.setActorId(actorId); entity.setActorType(actorType == null ? "SYSTEM" : actorType);
            entity.setResourceType(resourceType); entity.setResourceRefId(resourceRefId);
            entity.setOrganizationId(organizationId); entity.setWorkspaceId(workspaceId);
            entity.setBeforeState(toJson(beforeState)); entity.setAfterState(toJson(afterState));
            entity.setReason(reason); entity.setTraceId(MDC.get("traceId")); entity.setOccurredAt(Instant.now());
            repository.saveAndFlush(entity);
        } catch (Exception exception) {
            log.warn("Immutable audit event write failed: eventType={} resourceType={} resourceId={}",
                    eventType, resourceType, resourceRefId, exception);
        }
    }

    private String toJson(Object value) throws Exception {
        return value == null ? null : objectMapper.writeValueAsString(value);
    }
}
