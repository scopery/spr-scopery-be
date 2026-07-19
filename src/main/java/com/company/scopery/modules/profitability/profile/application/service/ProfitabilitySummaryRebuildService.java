package com.company.scopery.modules.profitability.profile.application.service;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.profitability.adjustment.domain.model.ProfitAdjustment;
import com.company.scopery.modules.profitability.adjustment.domain.model.ProfitAdjustmentRepository;
import com.company.scopery.modules.profitability.profile.application.response.ProfitabilitySummaryResponse;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfile;
import com.company.scopery.modules.profitability.shared.domain.ProfitabilityCalculator;
import com.company.scopery.modules.profitability.summary.domain.model.*;
import com.company.scopery.modules.profitability.threshold.domain.model.ProfitThresholdPolicy;
import com.company.scopery.modules.profitability.threshold.domain.model.ProfitThresholdPolicyRepository;
import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecord;
import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecordRepository;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class ProfitabilitySummaryRebuildService {
    /**
     * Pragmatic placeholder hourly cost when no rate card is linked to support effort.
     * Not payroll; documents a conservative support cost contribution.
     */
    public static final BigDecimal DEFAULT_SUPPORT_HOURLY_COST = new BigDecimal("75.00");

    private final ProjectFinanceScenarioRepository financeScenarios;
    private final ProjectFinanceSummaryRepository financeSummaries;
    private final QuoteRepository quotes;
    private final QuoteVersionRepository quoteVersions;
    private final QuoteSummaryRepository quoteSummaries;
    private final ProfitAdjustmentRepository adjustments;
    private final ProfitThresholdPolicyRepository thresholds;
    private final ProjectProfitabilitySummaryRepository summaries;
    private final ProfitSnapshotRepository snapshots;
    private final SupportCaseRepository supportCases;
    private final SupportEffortRecordRepository supportEfforts;

    public ProfitabilitySummaryRebuildService(ProjectFinanceScenarioRepository financeScenarios,
                                              ProjectFinanceSummaryRepository financeSummaries,
                                              QuoteRepository quotes,
                                              QuoteVersionRepository quoteVersions,
                                              QuoteSummaryRepository quoteSummaries,
                                              ProfitAdjustmentRepository adjustments,
                                              ProfitThresholdPolicyRepository thresholds,
                                              ProjectProfitabilitySummaryRepository summaries,
                                              ProfitSnapshotRepository snapshots,
                                              SupportCaseRepository supportCases,
                                              SupportEffortRecordRepository supportEfforts) {
        this.financeScenarios = financeScenarios;
        this.financeSummaries = financeSummaries;
        this.quotes = quotes;
        this.quoteVersions = quoteVersions;
        this.quoteSummaries = quoteSummaries;
        this.adjustments = adjustments;
        this.thresholds = thresholds;
        this.summaries = summaries;
        this.snapshots = snapshots;
        this.supportCases = supportCases;
        this.supportEfforts = supportEfforts;
    }

    @Transactional
    public ProfitabilitySummaryResponse rebuild(ProjectProfitabilityProfile profile) {
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;

        for (var quote : quotes.findByProjectId(profile.projectId())) {
            for (var version : quoteVersions.findCurrentFlagged(quote.id())) {
                revenue = revenue.add(quoteSummaries.findByQuoteVersionId(version.id())
                        .map(s -> s.totalQuotedAmount() == null ? BigDecimal.ZERO : s.totalQuotedAmount())
                        .orElse(BigDecimal.ZERO));
            }
        }

        var scenario = financeScenarios.findCurrentByProjectId(profile.projectId());
        if (scenario.isPresent()) {
            var summary = financeSummaries.findByScenarioId(scenario.get().id());
            if (summary.isPresent()) {
                var fs = summary.get();
                cost = nz(fs.totalLaborCost()).add(nz(fs.totalCustomCost())).add(nz(fs.totalVendorCost())).add(nz(fs.totalOverhead()));
                if (revenue.signum() == 0) {
                    revenue = nz(fs.plannedRevenue());
                }
            }
        }

        for (ProfitAdjustment adj : adjustments.findByProjectId(profile.projectId())) {
            if (!"APPLIED".equals(adj.status())) continue;
            if ("REVENUE".equalsIgnoreCase(adj.adjustmentType())) revenue = revenue.add(nz(adj.amount()));
            else if ("COST".equalsIgnoreCase(adj.adjustmentType())) cost = cost.add(nz(adj.amount()));
        }

        cost = cost.add(supportEffortCost(profile.projectId()));

        var threshold = thresholds.findByProjectId(profile.projectId())
                .orElse(ProfitThresholdPolicy.defaults(profile.workspaceId(), profile.projectId()));
        final BigDecimal finalRevenue = revenue;
        final BigDecimal finalCost = cost;
        final BigDecimal profit = ProfitabilityCalculator.profit(finalRevenue, finalCost);
        final BigDecimal margin = ProfitabilityCalculator.marginPercent(finalRevenue, finalCost);
        final var status = ProfitabilityCalculator.status(margin, threshold.healthyMarginPercent(), threshold.watchMarginPercent(),
                threshold.atRiskMarginPercent(), threshold.lossRiskMarginPercent());
        final String statusName = status.name();

        var persisted = summaries.findByProjectIdAndCurrency(profile.projectId(), profile.currency())
                .map(s -> s.replace(finalRevenue, finalCost, profit, margin, statusName))
                .orElseGet(() -> ProjectProfitabilitySummary.create(profile.workspaceId(), profile.projectId(), profile.currency(),
                        finalRevenue, finalCost, profit, margin, statusName));
        persisted = summaries.save(persisted);
        snapshots.save(ProfitSnapshot.fromSummary(profile.id(), persisted));

        return new ProfitabilitySummaryResponse(profile.currency(), finalRevenue, finalCost, profit, margin, statusName);
    }

    private BigDecimal supportEffortCost(UUID projectId) {
        BigDecimal hours = BigDecimal.ZERO;
        for (SupportCase supportCase : supportCases.findByProjectId(projectId)) {
            for (SupportEffortRecord effort : supportEfforts.findBySupportCaseId(supportCase.id())) {
                if ("CANCELLED".equals(effort.status())) continue;
                hours = hours.add(nz(effort.effortHours()));
            }
        }
        if (hours.signum() == 0) return BigDecimal.ZERO;
        return hours.multiply(DEFAULT_SUPPORT_HOURLY_COST).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
