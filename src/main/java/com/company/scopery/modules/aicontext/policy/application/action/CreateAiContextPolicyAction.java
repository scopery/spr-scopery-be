package com.company.scopery.modules.aicontext.policy.application.action;

import com.company.scopery.modules.aicontext.policy.application.command.CreateAiContextPolicyCommand;
import com.company.scopery.modules.aicontext.policy.application.response.AiContextPolicyResponse;
import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicy;
import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicyRepository;
import com.company.scopery.modules.aicontext.shared.activity.AiContextActivityLogger;
import com.company.scopery.modules.aicontext.shared.constant.AiContextActivityActions;
import com.company.scopery.modules.aicontext.shared.constant.AiContextEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateAiContextPolicyAction {

    private final AiContextResolutionPolicyRepository policyRepo;
    private final AiContextActivityLogger activityLogger;

    public CreateAiContextPolicyAction(AiContextResolutionPolicyRepository policyRepo,
                                        AiContextActivityLogger activityLogger) {
        this.policyRepo = policyRepo;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiContextPolicyResponse execute(CreateAiContextPolicyCommand c) {
        var saved = policyRepo.save(AiContextResolutionPolicy.create(
                c.workspaceId(), c.policyCode(), c.label(), c.maxTokens(), c.includeRelated()));

        activityLogger.logSuccess(AiContextEntityTypes.RESOLUTION_POLICY, saved.id(),
                AiContextActivityActions.POLICY_CREATED, "AI context policy created: " + saved.policyCode());

        return AiContextPolicyResponse.from(saved);
    }
}
