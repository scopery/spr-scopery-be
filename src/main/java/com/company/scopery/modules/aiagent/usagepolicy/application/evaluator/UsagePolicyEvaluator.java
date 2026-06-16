package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import com.company.scopery.modules.aiagent.execution.domain.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.UsageAggregate;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyRepository;
import com.company.scopery.modules.aiagent.usagepolicy.domain.UsagePolicyTargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UsagePolicyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(UsagePolicyEvaluator.class);

    private final UsagePolicyRepository usagePolicyRepository;
    private final ExecutionLogRepository executionLogRepository;
    private final UsageWindowCalculator windowCalculator;

    public UsagePolicyEvaluator(UsagePolicyRepository usagePolicyRepository,
                                 ExecutionLogRepository executionLogRepository,
                                 UsageWindowCalculator windowCalculator) {
        this.usagePolicyRepository = usagePolicyRepository;
        this.executionLogRepository = executionLogRepository;
        this.windowCalculator = windowCalculator;
    }

    public UsagePolicyEvaluationResult evaluate(UsagePolicyEvaluationContext context) {
        try {
            List<UsagePolicy> policies = usagePolicyRepository.findApplicableActivePolicies(
                    context.eventConfigId(), context.agentId(), context.modelDeploymentId());

            List<UsagePolicyViolation> violations = new ArrayList<>();
            List<UsagePolicyViolation> warnings   = new ArrayList<>();

            for (UsagePolicy policy : policies) {
                if (policy.period() == null) continue;

                UsageWindow window = windowCalculator.calculateWindow(policy.period(), context.currentTime());
                UsageAggregate aggregate = resolveAggregate(policy.targetType(), policy.targetId(), context, window);

                checkRequestLimit(policy, aggregate.requestCount(), violations, warnings);
                checkTokenLimit(policy, aggregate.totalTokenCount(), violations, warnings);
                checkCostLimit(policy, aggregate.estimatedCost(), violations, warnings);
            }

            UsagePolicyDecision decision;
            if (!violations.isEmpty()) {
                decision = UsagePolicyDecision.BLOCKED;
            } else if (!warnings.isEmpty()) {
                decision = UsagePolicyDecision.WARN;
            } else {
                decision = UsagePolicyDecision.ALLOWED;
            }

            return new UsagePolicyEvaluationResult(decision, violations, warnings);

        } catch (Exception e) {
            log.warn("Usage policy evaluation failed for requestId={}: {}", context.requestId(), e.getMessage());
            throw AiAgentExceptions.usagePolicyEvaluationFailed(e.getMessage());
        }
    }

    private UsageAggregate resolveAggregate(UsagePolicyTargetType targetType, UUID targetId,
                                             UsagePolicyEvaluationContext context, UsageWindow window) {
        return switch (targetType) {
            case GLOBAL -> executionLogRepository.aggregateGlobal(window.start(), window.end());
            case EVENT_CONFIG -> executionLogRepository.aggregateByEventConfig(
                    targetId != null ? targetId : context.eventConfigId(), window.start(), window.end());
            case AGENT -> executionLogRepository.aggregateByAgent(
                    targetId != null ? targetId : context.agentId(), window.start(), window.end());
            case MODEL_DEPLOYMENT -> executionLogRepository.aggregateByModelDeployment(
                    targetId != null ? targetId : context.modelDeploymentId(), window.start(), window.end());
        };
    }

    private void checkRequestLimit(UsagePolicy policy, long currentCount,
                                    List<UsagePolicyViolation> violations, List<UsagePolicyViolation> warnings) {
        if (policy.maxRequestsPerPeriod() == null) return;
        long limit = policy.maxRequestsPerPeriod();
        if (currentCount >= limit) {
            addViolation(policy, "REQUEST_COUNT", String.valueOf(limit), String.valueOf(currentCount),
                    "Request count " + currentCount + " reached limit " + limit, violations, warnings);
        }
    }

    private void checkTokenLimit(UsagePolicy policy, long currentTokens,
                                   List<UsagePolicyViolation> violations, List<UsagePolicyViolation> warnings) {
        if (policy.maxTokensPerPeriod() == null) return;
        long limit = policy.maxTokensPerPeriod();
        if (currentTokens >= limit) {
            addViolation(policy, "TOTAL_TOKENS", String.valueOf(limit), String.valueOf(currentTokens),
                    "Total token count " + currentTokens + " reached limit " + limit, violations, warnings);
        }
    }

    private void checkCostLimit(UsagePolicy policy, BigDecimal currentCost,
                                  List<UsagePolicyViolation> violations, List<UsagePolicyViolation> warnings) {
        if (policy.maxCostPerPeriod() == null) return;
        BigDecimal limit = policy.maxCostPerPeriod();
        if (currentCost.compareTo(limit) >= 0) {
            addViolation(policy, "ESTIMATED_COST", limit.toPlainString(), currentCost.toPlainString(),
                    "Estimated cost " + currentCost.toPlainString() + " reached limit " + limit.toPlainString(),
                    violations, warnings);
        }
    }

    private void addViolation(UsagePolicy policy, String metricName, String limitValue, String currentValue,
                                String message, List<UsagePolicyViolation> violations, List<UsagePolicyViolation> warnings) {
        UsagePolicyViolation violation = new UsagePolicyViolation(
                policy.id(), policy.code().value(),
                policy.targetType().name(), policy.period().name(),
                metricName, limitValue, currentValue,
                policy.action().name(), message + " for policy " + policy.code().value());
        if (policy.action() == UsagePolicyAction.BLOCK) {
            violations.add(violation);
        } else {
            warnings.add(violation);
        }
    }
}
