package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.execution.domain.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.UsageAggregate;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyCode;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyRepository;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsagePolicyEvaluatorTest {

    @Mock private UsagePolicyRepository usagePolicyRepository;
    @Mock private ExecutionLogRepository executionLogRepository;
    @Mock private UsageWindowCalculator windowCalculator;

    private UsagePolicyEvaluator evaluator;

    private final UUID eventConfigId    = UUID.randomUUID();
    private final UUID agentId          = UUID.randomUUID();
    private final UUID deploymentId     = UUID.randomUUID();
    private final Instant now           = Instant.parse("2026-01-15T10:30:00Z");
    private final UsageWindow window    = new UsageWindow(
            Instant.parse("2026-01-15T00:00:00Z"), Instant.parse("2026-01-16T00:00:00Z"));

    private UsagePolicyEvaluationContext ctx;

    @BeforeEach
    void setUp() {
        evaluator = new UsagePolicyEvaluator(usagePolicyRepository, executionLogRepository, windowCalculator);
        ctx = new UsagePolicyEvaluationContext(
                eventConfigId, UUID.randomUUID(), agentId, deploymentId,
                UUID.randomUUID(), UUID.randomUUID(), "req-test", "API", now);

        when(windowCalculator.calculateWindow(any(), any())).thenReturn(window);
        when(executionLogRepository.aggregateGlobal(any(), any()))
                .thenReturn(new UsageAggregate(0L, 0L, BigDecimal.ZERO));
        when(executionLogRepository.aggregateByEventConfig(any(), any(), any()))
                .thenReturn(new UsageAggregate(0L, 0L, BigDecimal.ZERO));
        when(executionLogRepository.aggregateByAgent(any(), any(), any()))
                .thenReturn(new UsageAggregate(0L, 0L, BigDecimal.ZERO));
        when(executionLogRepository.aggregateByModelDeployment(any(), any(), any()))
                .thenReturn(new UsageAggregate(0L, 0L, BigDecimal.ZERO));
    }

    @Test
    void evaluate_noPolicies_returnsAllowed() {
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of());

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isAllowed()).isTrue();
        assertThat(result.violations()).isEmpty();
        assertThat(result.warnings()).isEmpty();
    }

    @Test
    void evaluate_blockPolicyRequestLimitExceeded_returnsBlocked() {
        UsagePolicy policy = buildGlobalPolicy(UsagePolicyAction.BLOCK, 100, null, null);
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of(policy));
        when(executionLogRepository.aggregateGlobal(any(), any()))
                .thenReturn(new UsageAggregate(100L, 0L, BigDecimal.ZERO));

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isBlocked()).isTrue();
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0).metricName()).isEqualTo("REQUEST_COUNT");
        assertThat(result.violations().get(0).action()).isEqualTo("BLOCK");
    }

    @Test
    void evaluate_warnPolicyTokenLimitExceeded_returnsWarn() {
        UsagePolicy policy = buildGlobalPolicy(UsagePolicyAction.WARN, null, 50000L, null);
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of(policy));
        when(executionLogRepository.aggregateGlobal(any(), any()))
                .thenReturn(new UsageAggregate(0L, 50000L, BigDecimal.ZERO));

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isWarn()).isTrue();
        assertThat(result.warnings()).hasSize(1);
        assertThat(result.warnings().get(0).metricName()).isEqualTo("TOTAL_TOKENS");
        assertThat(result.warnings().get(0).action()).isEqualTo("WARN");
    }

    @Test
    void evaluate_blockPolicyCostLimitExceeded_returnsBlocked() {
        UsagePolicy policy = buildGlobalPolicy(UsagePolicyAction.BLOCK, null, null, new BigDecimal("10.00"));
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of(policy));
        when(executionLogRepository.aggregateGlobal(any(), any()))
                .thenReturn(new UsageAggregate(0L, 0L, new BigDecimal("10.00")));

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isBlocked()).isTrue();
        assertThat(result.violations().get(0).metricName()).isEqualTo("ESTIMATED_COST");
    }

    @Test
    void evaluate_limitNotYetReached_returnsAllowed() {
        UsagePolicy policy = buildGlobalPolicy(UsagePolicyAction.BLOCK, 100, null, null);
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of(policy));
        when(executionLogRepository.aggregateGlobal(any(), any()))
                .thenReturn(new UsageAggregate(99L, 0L, BigDecimal.ZERO));

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isAllowed()).isTrue();
    }

    @Test
    void evaluate_blockAndWarnPoliciesBothTriggered_blockedTakesPriority() {
        UsagePolicy blockPolicy = buildGlobalPolicy(UsagePolicyAction.BLOCK, 100, null, null);
        UsagePolicy warnPolicy  = buildAgentPolicy(UsagePolicyAction.WARN, null, 1000L, null);
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of(blockPolicy, warnPolicy));
        when(executionLogRepository.aggregateGlobal(any(), any()))
                .thenReturn(new UsageAggregate(100L, 0L, BigDecimal.ZERO));
        when(executionLogRepository.aggregateByAgent(any(), any(), any()))
                .thenReturn(new UsageAggregate(0L, 1000L, BigDecimal.ZERO));

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isBlocked()).isTrue();
        assertThat(result.violations()).hasSize(1);
        assertThat(result.warnings()).hasSize(1);
    }

    @Test
    void evaluate_policyWithNullPeriod_skipped() {
        UsagePolicy policy = buildGlobalPolicyNullPeriod();
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenReturn(List.of(policy));

        UsagePolicyEvaluationResult result = evaluator.evaluate(ctx);

        assertThat(result.isAllowed()).isTrue();
    }

    @Test
    void evaluate_repositoryThrows_throwsEvaluationFailed() {
        when(usagePolicyRepository.findApplicableActivePolicies(any(), any(), any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> evaluator.evaluate(ctx))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(AiAgentErrorCatalog.USAGE_POLICY_EVALUATION_FAILED.code()));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private UsagePolicy buildGlobalPolicy(UsagePolicyAction action,
                                          Integer maxRequests, Long maxTokens, BigDecimal maxCost) {
        return UsagePolicy.reconstitute(
                UUID.randomUUID(), UsagePolicyCode.of("GLOBAL_POLICY"), "Global Policy",
                UsagePolicyTargetType.GLOBAL, null,
                maxRequests, maxTokens, maxCost,
                null, null, UsagePolicyPeriod.DAY, action, 1, null,
                UsagePolicyStatus.ACTIVE, now, now);
    }

    private UsagePolicy buildAgentPolicy(UsagePolicyAction action,
                                         Integer maxRequests, Long maxTokens, BigDecimal maxCost) {
        return UsagePolicy.reconstitute(
                UUID.randomUUID(), UsagePolicyCode.of("AGENT_POLICY"), "Agent Policy",
                UsagePolicyTargetType.AGENT, agentId,
                maxRequests, maxTokens, maxCost,
                null, null, UsagePolicyPeriod.DAY, action, 1, null,
                UsagePolicyStatus.ACTIVE, now, now);
    }

    private UsagePolicy buildGlobalPolicyNullPeriod() {
        return UsagePolicy.reconstitute(
                UUID.randomUUID(), UsagePolicyCode.of("NO_PERIOD_POLICY"), "No Period Policy",
                UsagePolicyTargetType.GLOBAL, null,
                100, null, null,
                null, null, null, UsagePolicyAction.BLOCK, 1, null,
                UsagePolicyStatus.ACTIVE, now, now);
    }
}
