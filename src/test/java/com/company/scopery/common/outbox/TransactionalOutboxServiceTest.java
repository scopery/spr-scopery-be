package com.company.scopery.common.outbox;

import com.company.scopery.common.privacy.SensitiveDataRedactor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionalOutboxServiceTest {

    @Mock private TransactionalOutboxJpaRepository repository;
    @Mock private OutboxEventCodeValidator eventCodeValidator;

    private TransactionalOutboxService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TransactionalOutboxService(
                repository, objectMapper, new SensitiveDataRedactor(objectMapper), eventCodeValidator, 10);
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventCodeValidator.isKnown(any())).thenReturn(true);
    }

    @Test
    void enqueue_wrapsEnvelope_redactsSecrets_andStoresTraceId() throws Exception {
        MDC.put("traceId", "trace-123");
        UUID aggregateId = UUID.randomUUID();

        service.enqueue("WORKSPACE", aggregateId, "WORKSPACE_CREATED",
                Map.of("password", "secret", "name", "Dev"));

        ArgumentCaptor<TransactionalOutboxJpaEntity> captor = ArgumentCaptor.forClass(TransactionalOutboxJpaEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        TransactionalOutboxJpaEntity entity = captor.getValue();

        assertThat(entity.getStatus()).isEqualTo(OutboxStatus.PENDING.name());
        assertThat(entity.getTraceId()).isEqualTo("trace-123");
        assertThat(entity.getSourceSystem()).isEqualTo("SCOPERY_WORKSPACE");
        assertThat(entity.getPayload()).doesNotContain("secret");

        JsonNode payload = objectMapper.readTree(entity.getPayload());
        assertThat(payload.get("eventCode").asText()).isEqualTo("WORKSPACE_CREATED");
        assertThat(payload.get("traceId").asText()).isEqualTo("trace-123");
        assertThat(payload.get("data").get("password").asText()).isEqualTo(SensitiveDataRedactor.REDACTED);
        assertThat(payload.get("data").get("name").asText()).isEqualTo("Dev");
        MDC.clear();
    }
}
