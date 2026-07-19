package com.company.scopery.common.audit;

import com.company.scopery.common.privacy.SensitiveDataRedactor;
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
class ImmutableAuditEventServiceTest {

    @Mock private ImmutableAuditEventJpaRepository repository;

    private ImmutableAuditEventService service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        service = new ImmutableAuditEventService(repository, objectMapper, new SensitiveDataRedactor(objectMapper));
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void record_sensitiveAction_storesSecuritySeverityTraceAndRedactedPayload() {
        MDC.put("traceId", "audit-trace");
        UUID actorId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        service.record(AuditEventType.IAM_LOGIN_FAILED, actorId, "USER",
                "IAM_USER", resourceId, null, null,
                null, Map.of("password", "raw-secret", "outcome", "FAILED"), "Login failed");

        ArgumentCaptor<ImmutableAuditEventJpaEntity> captor = ArgumentCaptor.forClass(ImmutableAuditEventJpaEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        ImmutableAuditEventJpaEntity entity = captor.getValue();

        assertThat(entity.getEventType()).isEqualTo("IAM_LOGIN_FAILED");
        assertThat(entity.getSeverity()).isEqualTo(AuditSeverity.SECURITY.name());
        assertThat(entity.getTraceId()).isEqualTo("audit-trace");
        assertThat(entity.getAfterState()).doesNotContain("raw-secret");
        assertThat(entity.getAfterState()).contains(SensitiveDataRedactor.REDACTED);
        MDC.clear();
    }
}
