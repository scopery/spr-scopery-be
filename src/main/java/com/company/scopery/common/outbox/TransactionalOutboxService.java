package com.company.scopery.common.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransactionalOutboxService {
    private final TransactionalOutboxJpaRepository repository;
    private final ObjectMapper objectMapper;
    public TransactionalOutboxService(TransactionalOutboxJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository; this.objectMapper = objectMapper;
    }
    public void enqueue(String aggregateType, UUID aggregateId, String eventType, Object payload) {
        try {
            TransactionalOutboxJpaEntity entity = new TransactionalOutboxJpaEntity();
            entity.setId(UUID.randomUUID()); entity.setAggregateType(aggregateType);
            entity.setAggregateId(aggregateId); entity.setEventType(eventType);
            entity.setPayload(objectMapper.writeValueAsString(payload)); entity.setTraceId(MDC.get("traceId"));
            entity.setStatus("PENDING"); entity.setOccurredAt(Instant.now()); entity.setRetryCount(0);
            repository.saveAndFlush(entity);
        } catch (Exception exception) {
            throw new IllegalStateException("Transactional outbox write failed", exception);
        }
    }
}
