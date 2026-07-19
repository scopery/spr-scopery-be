package com.company.scopery.modules.projectfinance.calculation;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostCategory;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceErrorCatalog;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinanceCalculationServiceTest {

    private FinanceCalculationService service;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID estimationRunId = UUID.randomUUID();
    private final UUID phase1Id = UUID.randomUUID();
    private final UUID phase2Id = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new FinanceCalculationService();
    }

    @Test
    void importsLaborAndCalculatesGrossMarginAndPbt() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"),
                RevenueSplitMethod.COST_PROPORTION,
                ContingencyMethod.PERCENT_OF_DIRECT_COST, new BigDecimal("10"), null,
                OverheadMethod.PERCENT_OF_LABOR_COST, new BigDecimal("15"), null);

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), List.of(), true);
        ProjectFinanceSummary summary = result.summary();

        assertThat(summary.totalLaborCost()).isEqualByComparingTo("300");
        assertThat(summary.totalContingency()).isEqualByComparingTo("30");
        assertThat(summary.totalDirectCost()).isEqualByComparingTo("330");
        assertThat(summary.totalOverhead()).isEqualByComparingTo("45");
        assertThat(summary.budgetOfCosts()).isEqualByComparingTo("375");
        assertThat(summary.grossMargin()).isEqualByComparingTo("670");
        assertThat(summary.profitBeforeTax()).isEqualByComparingTo("625");
        assertThat(summary.grossMarginPercent()).isEqualByComparingTo("67.0000");
        assertThat(summary.pbtPercent()).isEqualByComparingTo("62.5000");
        assertThat(result.phaseRows()).hasSize(2);
    }

    @Test
    void addsCustomAndVendorCosts() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.COST_PROPORTION,
                null, null, null, null, null, null);
        List<ProjectCustomCost> customs = List.of(ProjectCustomCost.create(
                scenario.id(), projectId, phase1Id, CustomCostCategory.TRAVEL, "Flight", null,
                new BigDecimal("50"), "USD", null));
        List<ProjectVendorCost> vendors = List.of(ProjectVendorCost.create(
                scenario.id(), projectId, phase2Id, "Vendor", "Subcontract",
                new BigDecimal("70"), "USD"));

        var result = service.calculate(scenario, phases(), rollups(), customs, vendors, List.of(), true);

        assertThat(result.summary().totalCustomCost()).isEqualByComparingTo("50");
        assertThat(result.summary().totalVendorCost()).isEqualByComparingTo("70");
        assertThat(result.summary().totalDirectCost()).isEqualByComparingTo("420");
    }

    @Test
    void contingencyFixedAmount() {
        ProjectFinanceScenario scenario = scenario(
                BigDecimal.ZERO, RevenueSplitMethod.COST_PROPORTION,
                ContingencyMethod.FIXED_AMOUNT, null, new BigDecimal("40"),
                null, null, null);

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), List.of(), true);
        assertThat(result.summary().totalContingency()).isEqualByComparingTo("40");
    }

    @Test
    void contingencyPercentLabor() {
        ProjectFinanceScenario scenario = scenario(
                BigDecimal.ZERO, RevenueSplitMethod.COST_PROPORTION,
                ContingencyMethod.PERCENT_OF_LABOR_COST, new BigDecimal("20"), null,
                null, null, null);

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), List.of(), true);
        assertThat(result.summary().totalContingency()).isEqualByComparingTo("60");
    }

    @Test
    void overheadPercentRevenue() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.COST_PROPORTION,
                null, null, null,
                OverheadMethod.PERCENT_OF_REVENUE, new BigDecimal("10"), null);

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), List.of(), true);
        assertThat(result.summary().totalOverhead()).isEqualByComparingTo("100");
        assertThat(result.summary().grossMargin()).isEqualByComparingTo("700");
        assertThat(result.summary().profitBeforeTax()).isEqualByComparingTo("600");
    }

    @Test
    void overheadNegativePercentRejected() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.COST_PROPORTION,
                null, null, null,
                OverheadMethod.PERCENT_OF_LABOR_COST, new BigDecimal("-1"), null);

        assertThatThrownBy(() -> service.calculate(scenario, phases(), rollups(), List.of(), List.of(), List.of(), true))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_OVERHEAD_POLICY_INVALID.code()));
    }

    @Test
    void revenueSplitManualPercentMustSum100() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.MANUAL_PERCENT,
                null, null, null, null, null, null);
        List<ProjectPhaseFinance> existing = List.of(
                basePhase(phase1Id, "P1", 1, new BigDecimal("40")),
                basePhase(phase2Id, "P2", 2, new BigDecimal("50")));

        assertThatThrownBy(() -> service.calculate(scenario, phases(), rollups(), List.of(), List.of(), existing, true))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_INVALID_PERCENT_TOTAL.code()));
    }

    @Test
    void revenueSplitManualPercentSuccess() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.MANUAL_PERCENT,
                null, null, null, null, null, null);
        List<ProjectPhaseFinance> existing = List.of(
                basePhase(phase1Id, "P1", 1, new BigDecimal("40")),
                basePhase(phase2Id, "P2", 2, new BigDecimal("60")));

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), existing, true);
        assertThat(result.phaseRows().get(0).plannedRevenue()).isEqualByComparingTo("400");
        assertThat(result.phaseRows().get(1).plannedRevenue()).isEqualByComparingTo("600");
    }

    @Test
    void revenueSplitCostProportionZeroDirectWithRevenueRejected() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.COST_PROPORTION,
                null, null, null, null, null, null);
        List<PhaseEstimateRollup> emptyLabor = List.of(
                PhaseEstimateRollup.create(estimationRunId, projectId, phase1Id, 0, 0, 0,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "USD"),
                PhaseEstimateRollup.create(estimationRunId, projectId, phase2Id, 0, 0, 0,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "USD"));

        assertThatThrownBy(() -> service.calculate(scenario, phases(), emptyLabor, List.of(), List.of(), List.of(), true))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_DIRECT_COST_ZERO.code()));
    }

    @Test
    void zeroRevenueYieldsNullPercents() {
        ProjectFinanceScenario scenario = scenario(
                BigDecimal.ZERO, RevenueSplitMethod.COST_PROPORTION,
                null, null, null, null, null, null);

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), List.of(), true);
        assertThat(result.summary().grossMarginPercent()).isNull();
        assertThat(result.summary().pbtPercent()).isNull();
        assertThat(result.summary().grossMargin()).isEqualByComparingTo("-300");
    }

    @Test
    void recalculateKeepsLaborWithoutReimport() {
        ProjectFinanceScenario scenario = scenario(
                new BigDecimal("1000"), RevenueSplitMethod.COST_PROPORTION,
                null, null, null, null, null, null);
        List<ProjectPhaseFinance> existing = List.of(
                laborPhase(phase1Id, "P1", 1, new BigDecimal("10"), new BigDecimal("999")),
                laborPhase(phase2Id, "P2", 2, new BigDecimal("5"), new BigDecimal("1")));

        var result = service.calculate(scenario, phases(), rollups(), List.of(), List.of(), existing, false);
        assertThat(result.summary().totalLaborCost()).isEqualByComparingTo("1000");
    }

    private ProjectFinanceScenario scenario(
            BigDecimal plannedRevenue,
            RevenueSplitMethod revenueSplit,
            ContingencyMethod contingencyMethod,
            BigDecimal contingencyPercent,
            BigDecimal contingencyFixed,
            OverheadMethod overheadMethod,
            BigDecimal overheadPercent,
            BigDecimal overheadFixed) {
        return ProjectFinanceScenario.create(
                projectId, workspaceId, estimationRunId, null, "Scenario", null, "USD",
                plannedRevenue, revenueSplit, contingencyMethod, contingencyPercent, contingencyFixed,
                overheadMethod, overheadPercent, overheadFixed, null, null, UUID.randomUUID(), "trace");
    }

    private List<ProjectPhase> phases() {
        return List.of(
                new ProjectPhase(phase1Id, projectId, null, "P1", "Phase 1", null, 1,
                        null, null, ProjectPhaseStatus.ACTIVE, null, null, null, 0, null, null),
                new ProjectPhase(phase2Id, projectId, null, "P2", "Phase 2", null, 2,
                        null, null, ProjectPhaseStatus.ACTIVE, null, null, null, 0, null, null));
    }

    private List<PhaseEstimateRollup> rollups() {
        return List.of(
                PhaseEstimateRollup.create(estimationRunId, projectId, phase1Id, 2, 2, 0,
                        new BigDecimal("10"), new BigDecimal("200"), BigDecimal.ZERO, "USD"),
                PhaseEstimateRollup.create(estimationRunId, projectId, phase2Id, 1, 1, 0,
                        new BigDecimal("5"), new BigDecimal("100"), BigDecimal.ZERO, "USD"));
    }

    private ProjectPhaseFinance basePhase(UUID phaseId, String name, int order, BigDecimal percent) {
        return new ProjectPhaseFinance(
                UUID.randomUUID(), UUID.randomUUID(), projectId, phaseId, name, order, "USD",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, percent,
                null, null, null, null, null, null);
    }

    private ProjectPhaseFinance laborPhase(UUID phaseId, String name, int order,
                                           BigDecimal hours, BigDecimal labor) {
        return new ProjectPhaseFinance(
                UUID.randomUUID(), UUID.randomUUID(), projectId, phaseId, name, order, "USD",
                hours, labor, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null,
                null, null, null, null, null, null);
    }
}
