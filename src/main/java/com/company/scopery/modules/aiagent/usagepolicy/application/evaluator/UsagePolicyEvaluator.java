package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.model.UsageAggregate;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
                if (!environmentMatches(policy, context.environment())) {
                    continue;
                }

                checkEventAllowBlockLists(policy, context.eventDefinitionId(), violations, warnings);

                if (policy.maxTokensPerRequest() != null && context.estimatedTokensPerRequest() != null
                        && context.estimatedTokensPerRequest() > policy.maxTokensPerRequest()) {
                    addViolation(policy, "MAX_TOKENS_PER_REQUEST",
                            String.valueOf(policy.maxTokensPerRequest()),
                            String.valueOf(context.estimatedTokensPerRequest()),
                            "Estimated tokens per request exceeded limit",
                            violations, warnings);
                }

                if (policy.period() == null
                        && policy.maxRequestsPerPeriod() == null
                        && policy.maxTokensPerPeriod() == null
                        && policy.maxCostPerPeriod() == null
                        && policy.maxRequestsPerDay() == null
                        && policy.maxTokensPerDay() == null
                        && policy.maxEstimatedCostPerDay() == null) {
                    continue;
                }

                if (policy.period() != null) {
                    UsageWindow window = windowCalculator.calculateWindow(policy.period(), context.currentTime());
                    UsageAggregate aggregate = resolveAggregate(policy.targetType(), policy.targetId(), context, window);

                    checkRequestLimit(policy, aggregate.requestCount(), violations, warnings);
                    checkTokenLimit(policy, aggregate.totalTokenCount(), violations, warnings);
                    checkCostLimit(policy, aggregate.estimatedCost(), violations, warnings);
                }

                if (policy.maxRequestsPerDay() != null || policy.maxTokensPerDay() != null
                        || policy.maxEstimatedCostPerDay() != null) {
                    UsageWindow dayWindow = windowCalculator.calculateWindow(
                            com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod.DAY,
                            context.currentTime());
                    UsageAggregate dayAgg = resolveAggregate(policy.targetType(), policy.targetId(), context, dayWindow);
                    if (policy.maxRequestsPerDay() != null && dayAgg.requestCount() >= policy.maxRequestsPerDay()) {
                        addViolation(policy, "MAX_REQUESTS_PER_DAY",
                                String.valueOf(policy.maxRequestsPerDay()),
                                String.valueOf(dayAgg.requestCount()),
                                "Daily request count reached limit", violations, warnings);
                    }
                    if (policy.maxTokensPerDay() != null && dayAgg.totalTokenCount() >= policy.maxTokensPerDay()) {
                        addViolation(policy, "MAX_TOKENS_PER_DAY",
                                String.valueOf(policy.maxTokensPerDay()),
                                String.valueOf(dayAgg.totalTokenCount()),
                                "Daily token count reached limit", violations, warnings);
                    }
                    if (policy.maxEstimatedCostPerDay() != null
                            && dayAgg.estimatedCost().compareTo(policy.maxEstimatedCostPerDay()) >= 0) {
                        addViolation(policy, "MAX_ESTIMATED_COST_PER_DAY",
                                policy.maxEstimatedCostPerDay().toPlainString(),
                                dayAgg.estimatedCost().toPlainString(),
                                "Daily estimated cost reached limit", violations, warnings);
                    }
                }
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
            throw AiAgentExceptions.usagePolicyEvaluationFailed(sanitize(e.getMessage()));
        }
    }

    private boolean environmentMatches(UsagePolicy policy, String environment) {
        if (policy.environment() == null || policy.environment().isBlank()) {
            return true;
        }
        if (environment == null || environment.isBlank()) {
            return true;
        }
        return policy.environment().equalsIgnoreCase(environment);
    }

    private void checkEventAllowBlockLists(UsagePolicy policy, UUID eventDefinitionId,
                                            List<UsagePolicyViolation> violations,
                                            List<UsagePolicyViolation> warnings) {
        if (eventDefinitionId == null) return;
        String eventId = eventDefinitionId.toString();

        Set<String> blocked = parseIdList(policy.blockedEventDefinitionIds());
        if (blocked.contains(eventId)) {
            addViolation(policy, "BLOCKED_EVENT_DEFINITION", eventId, eventId,
                    "Event definition is blocked by usage policy", violations, warnings);
            return;
        }

        Set<String> allowed = parseIdList(policy.allowedEventDefinitionIds());
        if (!allowed.isEmpty() && !allowed.contains(eventId)) {
            addViolation(policy, "ALLOWED_EVENT_DEFINITION", "allowlist", eventId,
                    "Event definition is not in usage policy allowlist", violations, warnings);
        }
    }

    private Set<String> parseIdList(String raw) {
        if (raw == null || raw.isBlank()) {
            return Set.of();
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return Arrays.stream(trimmed.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.replace("\"", ""))
                .collect(Collectors.toSet());
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
                policy.targetType().name(), policy.period() != null ? policy.period().name() : "N/A",
                metricName, limitValue, currentValue,
                policy.action().name(), message + " for policy " + policy.code().value());
        if (policy.action() == UsagePolicyAction.BLOCK) {
            violations.add(violation);
        } else {
            warnings.add(violation);
        }
    }

    private static String sanitize(String message) {
        if (message == null) return "unknown";
        String lower = message.toLowerCase();
        if (lower.contains("api") && lower.contains("key")) return "provider error";
        if (lower.contains("authorization") || lower.contains("bearer ")) return "auth error";
        if (message.length() > 200) return message.substring(0, 200);
        return message;
    }
}
