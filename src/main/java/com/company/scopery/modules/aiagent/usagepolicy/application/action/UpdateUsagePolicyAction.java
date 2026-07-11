package com.company.scopery.modules.aiagent.usagepolicy.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.UpdateUsagePolicyCommand;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.UsagePolicyDetailResponse;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateUsagePolicyAction {

    private static final int DEFAULT_PRIORITY = 100;

    private final UsagePolicyRepository usagePolicyRepository;
    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateUsagePolicyAction(UsagePolicyRepository usagePolicyRepository,
                                   EventConfigRepository eventConfigRepository,
                                   AgentRepository agentRepository,
                                   ModelDeploymentRepository modelDeploymentRepository,
                                   AiAgentActivityLogger activityLogger) {
        this.usagePolicyRepository = usagePolicyRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.agentRepository = agentRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UsagePolicyDetailResponse execute(UpdateUsagePolicyCommand command) {
        UsagePolicy policy = findOrThrow(command.id());

        validateLimits(command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(),
                command.maxCostPerPeriod(), command.maxConcurrentRequests(),
                command.dailyBudget(), command.period());

        UsagePolicyPeriod period = AiAgentEnumParser.parseOptional(
                UsagePolicyPeriod.class, command.period(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_PERIOD.code(), "period");
        UsagePolicyAction action = AiAgentEnumParser.parseRequired(
                UsagePolicyAction.class, command.action(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_ACTION.code(), "action");

        int priority = command.priority() != null ? command.priority() : DEFAULT_PRIORITY;

        policy.update(command.name(), command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(),
                command.maxCostPerPeriod(), command.maxConcurrentRequests(), command.dailyBudget(),
                period, action, priority, command.description());

        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.UPDATE_USAGE_POLICY,
                "Usage policy updated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    private UsagePolicy findOrThrow(UUID id) {
        return usagePolicyRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.usagePolicyNotFound(id));
    }

    private void validateLimits(Integer maxRequestsPerPeriod, Long maxTokensPerPeriod,
                                 java.math.BigDecimal maxCostPerPeriod, Integer maxConcurrentRequests,
                                 java.math.BigDecimal dailyBudget, String periodRaw) {
        boolean hasAnyLimit = maxRequestsPerPeriod != null || maxTokensPerPeriod != null
                || maxCostPerPeriod != null || maxConcurrentRequests != null || dailyBudget != null;
        if (!hasAnyLimit) {
            throw AiAgentExceptions.usagePolicyNoLimitDefined();
        }
        boolean hasPeriodBasedLimit = maxRequestsPerPeriod != null
                || maxTokensPerPeriod != null || maxCostPerPeriod != null;
        if (hasPeriodBasedLimit && (periodRaw == null || periodRaw.isBlank())) {
            throw AiAgentExceptions.usagePolicyPeriodRequired();
        }
    }

    private UsagePolicyDetailResponse buildDetailResponse(UsagePolicy policy) {
        String targetName = resolveTargetName(policy.targetType(), policy.targetId());
        return UsagePolicyDetailResponse.from(policy, targetName);
    }

    private String resolveTargetName(UsagePolicyTargetType targetType, UUID targetId) {
        return switch (targetType) {
            case GLOBAL -> "Global";
            case EVENT_CONFIG -> eventConfigRepository.findById(targetId)
                    .map(EventConfig::code).map(c -> c.value()).orElse(null);
            case AGENT -> agentRepository.findById(targetId)
                    .map(Agent::name).orElse(null);
            case MODEL_DEPLOYMENT -> modelDeploymentRepository.findById(targetId)
                    .map(ModelDeployment::name).orElse(null);
        };
    }
}
