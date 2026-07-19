package com.company.scopery.common.outbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionalOutboxProcessorTest {

    @Mock private TransactionalOutboxJpaRepository repository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PlatformTransactionManager transactionManager;

    private TransactionalOutboxProcessor processor;

    @BeforeEach
    void setUp() {
        when(transactionManager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        processor = new TransactionalOutboxProcessor(
                repository, eventPublisher, transactionManager, true, 50, 60, 30);
    }

    @Test
    void processOne_successfulPublish_marksPublished() {
        UUID id = UUID.randomUUID();
        TransactionalOutboxJpaEntity entity = pendingEntity(id, 0, 10);
        when(repository.claim(eq(id), any(), any(), any())).thenReturn(1);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        processor.processOne(id);

        assertThat(entity.getStatus()).isEqualTo(OutboxStatus.PUBLISHED.name());
        assertThat(entity.getPublishedAt()).isNotNull();
        verify(eventPublisher).publishEvent(any(PlatformOutboxPublishedEvent.class));
    }

    @Test
    void processOne_alreadyClaimed_skips() {
        UUID id = UUID.randomUUID();
        when(repository.claim(eq(id), any(), any(), any())).thenReturn(0);

        processor.processOne(id);

        verify(repository, never()).findById(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void processOne_publishFailure_retriesThenDeadLetters() {
        UUID id = UUID.randomUUID();
        TransactionalOutboxJpaEntity entity = pendingEntity(id, 0, 2);
        when(repository.claim(eq(id), any(), any(), any())).thenReturn(1);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("boom")).when(eventPublisher).publishEvent(any());

        processor.processOne(id);
        assertThat(entity.getStatus()).isEqualTo(OutboxStatus.PENDING.name());
        assertThat(entity.getRetryCount()).isEqualTo(1);
        assertThat(entity.getNextRetryAt()).isNotNull();

        processor.processOne(id);
        assertThat(entity.getStatus()).isEqualTo(OutboxStatus.DEAD_LETTER.name());
        assertThat(entity.getRetryCount()).isEqualTo(2);
    }

    private TransactionalOutboxJpaEntity pendingEntity(UUID id, int retryCount, int maxAttempts) {
        TransactionalOutboxJpaEntity entity = new TransactionalOutboxJpaEntity();
        entity.setId(id);
        entity.setAggregateType("WORKSPACE");
        entity.setAggregateId(UUID.randomUUID());
        entity.setEventType("WORKSPACE_CREATED");
        entity.setEventVersion(1);
        entity.setSourceSystem("SCOPERY_WORKSPACE");
        entity.setPayload("{\"eventCode\":\"WORKSPACE_CREATED\"}");
        entity.setTraceId("trace-1");
        entity.setStatus(OutboxStatus.PENDING.name());
        entity.setOccurredAt(Instant.now());
        entity.setAvailableAt(Instant.now());
        entity.setRetryCount(retryCount);
        entity.setMaxAttempts(maxAttempts);
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
