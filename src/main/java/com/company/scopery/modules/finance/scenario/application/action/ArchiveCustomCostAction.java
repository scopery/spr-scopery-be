package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.response.CustomCostResponse;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCost;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCostRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveCustomCostAction {

    private final FinanceScenarioRepository scenarios;
    private final CustomCostRepository customCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public ArchiveCustomCostAction(FinanceScenarioRepository scenarios,
                                   CustomCostRepository customCosts,
                                   RecalculateFinanceSummaryAction recalculate,
                                   FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.customCosts = customCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CustomCostResponse execute(UUID projectId, UUID scenarioId, UUID costId) {
        scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));

        CustomCost existing = customCosts.findById(costId)
                .orElseThrow(() -> FinanceExceptions.costNotFound(costId));

        CustomCost archived = existing.archive();
        CustomCost saved = customCosts.save(archived);
        recalculate.execute(scenarioId, projectId);

        activityLogger.log(FinanceEntityTypes.CUSTOM_COST, saved.id(),
                FinanceActivityActions.ARCHIVE_CUSTOM_COST,
                "Custom cost archived: " + saved.name());

        return CustomCostResponse.from(saved);
    }
}
