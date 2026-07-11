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
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.ActivateUsagePolicyCommand;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.UsagePolicyDetailResponse;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateUsagePolicyAction {

    private final UsagePolicyRepository usagePolicyRepository;
    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateUsagePolicyAction(UsagePolicyRepository usagePolicyRepository,
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
    public UsagePolicyDetailResponse execute(ActivateUsagePolicyCommand command) {
        UsagePolicy policy = findOrThrow(command.id());

        if (policy.status() == UsagePolicyStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedUsagePolicyCannotBeActivated(policy.code().value());
        }

        if (usagePolicyRepository.existsActiveByTargetTypeAndTargetId(
                policy.targetType(), policy.targetId(), policy.id())) {
            throw AiAgentExceptions.usagePolicyActiveAlreadyExists(
                    policy.targetType().name(), policy.targetId());
        }

        policy.activate();
        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.ACTIVATE_USAGE_POLICY,
                "Usage policy activated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    private UsagePolicy findOrThrow(UUID id) {
        return usagePolicyRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.usagePolicyNotFound(id));
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
