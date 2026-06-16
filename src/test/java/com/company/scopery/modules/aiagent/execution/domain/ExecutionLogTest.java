package com.company.scopery.modules.aiagent.execution.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ExecutionLogTest {

    private static final ExecutionRequestId REQUEST_ID = ExecutionRequestId.of("req-test-001");

    private ExecutionLog buildPending() {
        return ExecutionLog.create(REQUEST_ID, null, null,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                ExecutionTriggerSource.EVENT, null);
    }

    @Test
    void create_newLog_isPendingByDefault() {
        ExecutionLog log = buildPending();

        assertThat(log.id()).isNotNull();
        assertThat(log.status()).isEqualTo(ExecutionStatus.PENDING);
        assertThat(log.requestId().value()).isEqualTo("req-test-001");
        assertThat(log.startedAt()).isNull();
        assertThat(log.completedAt()).isNull();
        assertThat(log.latencyMs()).isNull();
    }

    @Test
    void markRunning_fromPending_setsStatusAndStartedAt() {
        ExecutionLog log = buildPending();
        log.markRunning();

        assertThat(log.status()).isEqualTo(ExecutionStatus.RUNNING);
        assertThat(log.startedAt()).isNotNull();
    }

    @Test
    void markRunning_alreadyRunning_throwsIllegalStateException() {
        ExecutionLog log = buildPending();
        log.markRunning();

        assertThatThrownBy(log::markRunning)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING");
    }

    @Test
    void markSucceeded_fromRunning_setsStatusAndCompletedAt() {
        ExecutionLog log = buildPending();
        log.markRunning();
        log.markSucceeded(100, 200, 300, new BigDecimal("0.005"), "prov-req-1", null);

        assertThat(log.status()).isEqualTo(ExecutionStatus.SUCCEEDED);
        assertThat(log.completedAt()).isNotNull();
        assertThat(log.inputTokenCount()).isEqualTo(100);
        assertThat(log.outputTokenCount()).isEqualTo(200);
        assertThat(log.totalTokenCount()).isEqualTo(300);
        assertThat(log.estimatedCost()).isEqualByComparingTo("0.005");
        assertThat(log.latencyMs()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void markFailed_fromRunning_setsStatusAndErrorFields() {
        ExecutionLog log = buildPending();
        log.markRunning();
        log.markFailed("PROVIDER_TIMEOUT", "Request timed out", null, null, null, null, null, null);

        assertThat(log.status()).isEqualTo(ExecutionStatus.FAILED);
        assertThat(log.completedAt()).isNotNull();
        assertThat(log.errorCode()).isEqualTo("PROVIDER_TIMEOUT");
        assertThat(log.errorMessage()).isEqualTo("Request timed out");
    }

    @Test
    void cancel_fromPending_setsStatusToCancelled() {
        ExecutionLog log = buildPending();
        log.cancel();

        assertThat(log.status()).isEqualTo(ExecutionStatus.CANCELLED);
        assertThat(log.completedAt()).isNotNull();
    }

    @Test
    void cancel_fromRunning_setsStatusToCancelledAndCalculatesLatency() {
        ExecutionLog log = buildPending();
        log.markRunning();
        log.cancel();

        assertThat(log.status()).isEqualTo(ExecutionStatus.CANCELLED);
        assertThat(log.latencyMs()).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    void markRunning_fromTerminalSucceeded_throwsIllegalStateException() {
        ExecutionLog log = buildPending();
        log.markRunning();
        log.markSucceeded(null, null, null, null, null, null);

        assertThatThrownBy(log::markRunning)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("terminal");
    }

    @Test
    void cancel_fromTerminal_throwsIllegalStateException() {
        ExecutionLog log = buildPending();
        log.cancel();

        assertThatThrownBy(log::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("terminal");
    }

    @Test
    void markSucceeded_fromPending_throwsIllegalStateException() {
        ExecutionLog log = buildPending();

        assertThatThrownBy(() -> log.markSucceeded(null, null, null, null, null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RUNNING");
    }

    @Test
    void executionStatus_terminalCheck() {
        assertThat(ExecutionStatus.SUCCEEDED.isTerminal()).isTrue();
        assertThat(ExecutionStatus.FAILED.isTerminal()).isTrue();
        assertThat(ExecutionStatus.CANCELLED.isTerminal()).isTrue();
        assertThat(ExecutionStatus.PENDING.isTerminal()).isFalse();
        assertThat(ExecutionStatus.RUNNING.isTerminal()).isFalse();
    }
}