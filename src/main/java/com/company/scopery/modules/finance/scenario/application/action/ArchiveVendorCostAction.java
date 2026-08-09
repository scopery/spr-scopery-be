package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.response.VendorCostResponse;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCost;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCostRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveVendorCostAction {

    private final FinanceScenarioRepository scenarios;
    private final VendorCostRepository vendorCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public ArchiveVendorCostAction(FinanceScenarioRepository scenarios,
                                   VendorCostRepository vendorCosts,
                                   RecalculateFinanceSummaryAction recalculate,
                                   FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.vendorCosts = vendorCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public VendorCostResponse execute(UUID projectId, UUID scenarioId, UUID costId) {
        scenarios.findByIdAndProjectId(scenarioId, projectId)
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(scenarioId));

        VendorCost existing = vendorCosts.findById(costId)
                .orElseThrow(() -> FinanceExceptions.costNotFound(costId));

        VendorCost archived = existing.archive();
        VendorCost saved = vendorCosts.save(archived);
        recalculate.execute(scenarioId, projectId);

        activityLogger.log(FinanceEntityTypes.VENDOR_COST, saved.id(),
                FinanceActivityActions.ARCHIVE_VENDOR_COST,
                "Vendor cost archived: " + saved.vendorName());

        return VendorCostResponse.from(saved);
    }
}
