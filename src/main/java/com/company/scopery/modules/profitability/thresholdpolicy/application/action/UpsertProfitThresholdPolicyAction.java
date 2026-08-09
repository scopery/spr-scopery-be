package com.company.scopery.modules.profitability.thresholdpolicy.application.action;

import com.company.scopery.modules.profitability.thresholdpolicy.application.command.UpsertProfitThresholdPolicyCommand;
import com.company.scopery.modules.profitability.thresholdpolicy.application.response.ProfitThresholdPolicyResponse;
import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicy;
import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UpsertProfitThresholdPolicyAction {

    private final ProfitThresholdPolicyRepository policies;

    public UpsertProfitThresholdPolicyAction(ProfitThresholdPolicyRepository policies) {
        this.policies = policies;
    }

    @Transactional
    public ProfitThresholdPolicyResponse execute(UpsertProfitThresholdPolicyCommand command) {
        Optional<ProfitThresholdPolicy> existing = policies.findByProjectId(command.projectId());

        ProfitThresholdPolicy policy;
        if (existing.isPresent()) {
            ProfitThresholdPolicy current = existing.get();
            policy = new ProfitThresholdPolicy(
                    current.id(),
                    current.projectId(),
                    command.healthyMarginPercent(),
                    command.watchMarginPercent(),
                    command.atRiskMarginPercent(),
                    command.lossRiskMarginPercent(),
                    current.version(),
                    current.createdAt(),
                    current.updatedAt()
            );
        } else {
            policy = new ProfitThresholdPolicy(
                    java.util.UUID.randomUUID(),
                    command.projectId(),
                    command.healthyMarginPercent(),
                    command.watchMarginPercent(),
                    command.atRiskMarginPercent(),
                    command.lossRiskMarginPercent(),
                    null,
                    null,
                    null
            );
        }

        ProfitThresholdPolicy saved = policies.save(policy);
        return ProfitThresholdPolicyResponse.from(saved);
    }
}
