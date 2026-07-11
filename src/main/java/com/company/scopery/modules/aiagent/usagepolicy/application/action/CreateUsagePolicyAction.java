package com.company.scopery.modules.aiagent.usagepolicy.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.CreateUsagePolicyCommand;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.UsagePolicyResponse;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateUsagePolicyAction {

    private static final int DEFAULT_PRIORITY = 100;

    private final UsagePolicyRepository usagePolicyRepository;
    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateUsagePolicyAction(UsagePolicyRepository usagePolicyRepository,
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
    public UsagePolicyResponse execute(CreateUsagePolicyCommand command) {
        UsagePolicyCode code = UsagePolicyCode.of(command.code());
        if (usagePolicyRepository.existsByCode(code)) {
            throw AiAgentExceptions.usagePolicyCodeAlreadyExists(code.value());
        }

        UsagePolicyTargetType targetType = AiAgentEnumParser.parseRequired(
                UsagePolicyTargetType.class, command.targetType(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_TARGET_TYPE.code(), "targetType");

        validateTargetConsistency(targetType, command.targetId());
        validateLimits(command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(),
                command.maxCostPerPeriod(), command.maxConcurrentRequests(),
                command.dailyBudget(), command.period());

        UsagePolicyPeriod period = AiAgentEnumParser.parseOptional(
                UsagePolicyPeriod.class, command.period(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_PERIOD.code(), "period");
        UsagePolicyAction action = AiAgentEnumParser.parseRequired(
                UsagePolicyAction.class, command.action(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_ACTION.code(), "action");

        validateTargetExists(targetType, command.targetId());

        if (usagePolicyRepository.existsActiveByTargetTypeAndTargetId(targetType, command.targetId(), null)) {
            throw AiAgentExceptions.usagePolicyActiveAlreadyExists(targetType.name(), command.targetId());
        }

        int priority = command.priority() != null ? command.priority() : DEFAULT_PRIORITY;

        UsagePolicy policy = UsagePolicy.create(code, command.name(), targetType, command.targetId(),
                command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(), command.maxCostPerPeriod(),
                command.maxConcurrentRequests(), command.dailyBudget(), period, action, priority,
                command.description());

        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.CREATE_USAGE_POLICY,
                "Usage policy created: " + saved.code().value()
                        + " targetType=" + saved.targetType().name());

        return UsagePolicyResponse.from(saved);
    }

    private void validateTargetConsistency(UsagePolicyTargetType targetType, UUID targetId) {
        if (targetType == UsagePolicyTargetType.GLOBAL) {
            if (targetId != null) {
                throw AiAgentExceptions.usagePolicyTargetIdMustBeNull();
            }
        } else {
            if (targetId == null) {
                throw AiAgentExceptions.usagePolicyTargetIdRequired(targetType.name());
            }
        }
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

    private void validateTargetExists(UsagePolicyTargetType targetType, UUID targetId) {
        if (targetType == UsagePolicyTargetType.GLOBAL) return;
        switch (targetType) {
            case EVENT_CONFIG -> {
                if (eventConfigRepository.findById(targetId).isEmpty()) {
                    throw AiAgentExceptions.usagePolicyEventConfigNotFound(targetId);
                }
            }
            case AGENT -> {
                if (agentRepository.findById(targetId).isEmpty()) {
                    throw AiAgentExceptions.usagePolicyAgentNotFound(targetId);
                }
            }
            case MODEL_DEPLOYMENT -> {
                if (modelDeploymentRepository.findById(targetId).isEmpty()) {
                    throw AiAgentExceptions.usagePolicyModelDeploymentNotFound(targetId);
                }
            }
            default -> {}
        }
    }
}
