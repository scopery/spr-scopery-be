package com.company.scopery.common.audit;

import com.company.scopery.common.privacy.SensitiveDataRedactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ImmutableAuditEventService {

    private static final Logger log = LoggerFactory.getLogger(ImmutableAuditEventService.class);

    private static final Set<AuditEventType> SECURITY_EVENTS = EnumSet.of(
            AuditEventType.IAM_LOGIN_FAILED,
            AuditEventType.IAM_USER_SUSPENDED,
            AuditEventType.IAM_PASSWORD_CHANGED,
            AuditEventType.IAM_PASSWORD_RESET_COMPLETED,
            AuditEventType.ACCESS_GRANT_CREATED,
            AuditEventType.ACCESS_GRANT_DELEGATED,
            AuditEventType.ACCESS_GRANT_REVOKED,
            AuditEventType.IAM_AUTHORIZATION_DENIED,
            AuditEventType.IAM_DELEGATION_REJECTED,
            AuditEventType.ORGANIZATION_ARCHIVED,
            AuditEventType.WORKSPACE_ARCHIVED,
            AuditEventType.ORG_MEMBER_SUSPENDED,
            AuditEventType.ORG_MEMBER_REMOVED,
            AuditEventType.WORKSPACE_MEMBER_DEACTIVATED
    );

    private final ImmutableAuditEventJpaRepository repository;
    private final ObjectMapper objectMapper;
    private final SensitiveDataRedactor redactor;

    public ImmutableAuditEventService(ImmutableAuditEventJpaRepository repository,
                                      ObjectMapper objectMapper,
                                      SensitiveDataRedactor redactor) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.redactor = redactor;
    }

    public void record(AuditEventType eventType, UUID actorId, String actorType,
                       String resourceType, UUID resourceRefId, UUID organizationId,
                       UUID workspaceId, Object beforeState, Object afterState, String reason) {
        AuditSeverity severity = SECURITY_EVENTS.contains(eventType) ? AuditSeverity.SECURITY : AuditSeverity.INFO;
        record(eventType, severity, actorId, actorType, resourceType, resourceRefId,
                organizationId, workspaceId, beforeState, afterState, reason);
    }

    public void record(AuditEventType eventType, AuditSeverity severity, UUID actorId, String actorType,
                       String resourceType, UUID resourceRefId, UUID organizationId,
                       UUID workspaceId, Object beforeState, Object afterState, String reason) {
        try {
            ImmutableAuditEventJpaEntity entity = new ImmutableAuditEventJpaEntity();
            entity.setId(UUID.randomUUID());
            entity.setEventType(eventType.name());
            entity.setSeverity(severity == null ? AuditSeverity.INFO.name() : severity.name());
            entity.setActorId(actorId);
            entity.setActorType(actorType == null ? "SYSTEM" : actorType);
            entity.setResourceType(resourceType);
            entity.setResourceRefId(resourceRefId);
            entity.setOrganizationId(organizationId);
            entity.setWorkspaceId(workspaceId);
            entity.setBeforeState(toJson(redactor.redact(beforeState)));
            entity.setAfterState(toJson(redactor.redact(afterState)));
            entity.setReason(reason);
            entity.setTraceId(MDC.get("traceId"));
            entity.setOccurredAt(Instant.now());
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
