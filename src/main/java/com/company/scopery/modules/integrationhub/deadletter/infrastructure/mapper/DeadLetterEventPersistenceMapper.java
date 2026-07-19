package com.company.scopery.modules.integrationhub.deadletter.infrastructure.mapper;

import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEvent;
import com.company.scopery.modules.integrationhub.deadletter.infrastructure.persistence.DeadLetterEventJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterEventPersistenceMapper {

    public DeadLetterEventJpaEntity toJpa(DeadLetterEvent d) {
        DeadLetterEventJpaEntity e = new DeadLetterEventJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setSourceType(d.sourceType());
        e.setSourceId(d.sourceId());
        e.setEventType(d.eventType());
        e.setPayloadReference(d.payloadReference());
        e.setFailureCode(d.failureCode());
        e.setFailureMessage(d.failureMessage());
        e.setAttemptCount(d.attemptCount());
        e.setStatus(d.status());
        e.setLastAttemptAt(d.lastAttemptAt());
        e.setResolvedAt(d.resolvedAt());
        e.setResolvedBy(d.resolvedBy());
        e.setVersion(d.version());
        e.setCreatedAt(d.createdAt());
        return e;
    }

    public DeadLetterEvent toDomain(DeadLetterEventJpaEntity e) {
        return new DeadLetterEvent(
                e.getId(), e.getWorkspaceId(), e.getSourceType(), e.getSourceId(), e.getEventType(),
                e.getPayloadReference(), e.getFailureCode(), e.getFailureMessage(),
                e.getAttemptCount() == null ? 0 : e.getAttemptCount(), e.getStatus(),
                e.getLastAttemptAt(), e.getResolvedAt(), e.getResolvedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
