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

import java.util.UUID;

@Component
public class MarkCurrentFinanceScenarioAction {

    private final FinanceScenarioRepository scenarios;
    private final FinanceActivityLogger activityLogger;

    public MarkCurrentFinanceScenarioAction(FinanceScenarioRepository scenarios,
                                            FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UUID projectId, UUID scenarioId) {
        FinanceScenario scenario = scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));

        scenarios.clearCurrentFlagForProject(projectId);

        FinanceScenario marked = scenario.markCurrent();
        FinanceScenario saved = scenarios.save(marked);

        activityLogger.log(FinanceEntityTypes.FINANCE_SCENARIO, saved.id(),
                FinanceActivityActions.MARK_CURRENT,
                "Finance scenario marked as current: " + saved.code());

        return FinanceScenarioResponse.from(saved);
    }
}
