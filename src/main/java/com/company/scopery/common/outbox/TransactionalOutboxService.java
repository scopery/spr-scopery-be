package com.company.scopery.common.outbox;

import com.company.scopery.common.privacy.SensitiveDataRedactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionalOutboxService {

    private static final Logger log = LoggerFactory.getLogger(TransactionalOutboxService.class);

    private final TransactionalOutboxJpaRepository repository;
    private final ObjectMapper objectMapper;
    private final SensitiveDataRedactor redactor;
    private final Optional<OutboxEventCodeValidator> eventCodeValidator;
    private final int defaultMaxAttempts;

    public TransactionalOutboxService(TransactionalOutboxJpaRepository repository,
                                      ObjectMapper objectMapper,
                                      SensitiveDataRedactor redactor,
                                      @Autowired(required = false) OutboxEventCodeValidator eventCodeValidator,
                                      @Value("${scopery.platform.outbox.max-attempts:10}") int defaultMaxAttempts) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.redactor = redactor;
        this.eventCodeValidator = Optional.ofNullable(eventCodeValidator);
        this.defaultMaxAttempts = defaultMaxAttempts;
    }

    public void enqueue(String aggregateType, UUID aggregateId, String eventType, Object payload) {
        enqueue(aggregateType, aggregateId, eventType, inferSourceSystem(eventType), 1, payload);
    }

    public void enqueue(String aggregateType,
                        UUID aggregateId,
                        String eventType,
                        String sourceSystem,
                        int eventVersion,
                        Object payload) {
        eventCodeValidator.ifPresent(validator -> {
            if (!validator.isKnown(eventType)) {
                log.warn("Outbox enqueue for unregistered event code={} — emission allowed with monitoring", eventType);
            }
        });

        try {
            Instant now = Instant.now();
            String traceId = MDC.get("traceId");
            Object safeData = redactor.redact(payload);

            Map<String, Object> envelope = new LinkedHashMap<>();
            envelope.put("eventCode", eventType);
            envelope.put("eventVersion", eventVersion);
            envelope.put("sourceSystem", sourceSystem);
            envelope.put("occurredAt", now.toString());
            envelope.put("traceId", traceId == null ? "" : traceId);
            envelope.put("aggregateType", aggregateType);
            envelope.put("aggregateId", aggregateId);
            envelope.put("data", safeData);

            TransactionalOutboxJpaEntity entity = new TransactionalOutboxJpaEntity();
            entity.setId(UUID.randomUUID());
            entity.setAggregateType(aggregateType);
            entity.setAggregateId(aggregateId);
            entity.setEventType(eventType);
            entity.setEventVersion(eventVersion);
            entity.setSourceSystem(sourceSystem);
            entity.setPayload(objectMapper.writeValueAsString(envelope));
            entity.setTraceId(traceId);
            entity.setStatus(OutboxStatus.PENDING.name());
            entity.setOccurredAt(now);
            entity.setAvailableAt(now);
            entity.setRetryCount(0);
            entity.setMaxAttempts(defaultMaxAttempts);
            entity.setUpdatedAt(now);
            repository.saveAndFlush(entity);
        } catch (Exception exception) {
            throw new IllegalStateException("Transactional outbox write failed", exception);
        }
    }

    private String inferSourceSystem(String eventType) {
        if (eventType == null) {
            return "SCOPERY";
        }
        if (eventType.startsWith("IAM_")) {
            return "SCOPERY_IAM";
        }
        if (eventType.startsWith("ORG_") || eventType.startsWith("WORKSPACE_") || eventType.startsWith("ORGANIZATION_")) {
            return "SCOPERY_WORKSPACE";
        }
        if (eventType.startsWith("PLATFORM_")) {
            return "SCOPERY_PLATFORM";
        }
        if (eventType.startsWith("AI_")) {
            return "SCOPERY_AI_AGENT";
        }
        if (eventType.startsWith("DOCUMENT_TYPE_") || eventType.startsWith("DOCUMENT_")) {
            return "SCOPERY_KNOWLEDGE";
        }
        return "SCOPERY";
    }
}
