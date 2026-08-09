package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class ApproveFinanceScenarioAction {

    private final FinanceScenarioRepository scenarios;
    private final FinanceActivityLogger activityLogger;

    public ApproveFinanceScenarioAction(FinanceScenarioRepository scenarios,
                                        FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId) {
        FinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));

        if (!scenario.canApprove()) {
            throw FinanceExceptions.scenarioNotApprovable(scenarioId);
        }

        FinanceScenario approved = scenario.approve(Instant.now());
        FinanceScenario saved = scenarios.save(approved);

        activityLogger.log(FinanceEntityTypes.FINANCE_SCENARIO, saved.id(),
                FinanceActivityActions.APPROVE_SCENARIO,
                "Finance scenario approved: " + saved.code());

        return FinanceScenarioResponse.from(saved);
    }
}
