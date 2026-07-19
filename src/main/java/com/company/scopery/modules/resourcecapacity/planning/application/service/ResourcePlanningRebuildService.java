package com.company.scopery.modules.resourcecapacity.planning.application.service;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimateRepository;
import com.company.scopery.modules.resourcecapacity.planning.application.response.*;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlag;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlagRepository;
import com.company.scopery.modules.resourcecapacity.shared.domain.ResourceUtilizationCalculator;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicy;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ResourcePlanningRebuildService {
    private final EffortEstimateRepository estimates;
    private final ProjectResourceAllocationRepository allocations;
    private final UtilizationThresholdPolicyRepository thresholds;
    private final ResourceRiskFlagRepository risks;

    public ResourcePlanningRebuildService(EffortEstimateRepository estimates,
                                          ProjectResourceAllocationRepository allocations,
                                          UtilizationThresholdPolicyRepository thresholds,
                                          ResourceRiskFlagRepository risks) {
        this.estimates = estimates;
        this.allocations = allocations;
        this.thresholds = thresholds;
        this.risks = risks;
    }

    @Transactional
    public EffortForecastResponse rebuildForecast(UUID workspaceId, UUID projectId) {
        BigDecimal forecast = estimates.findActiveByProjectId(projectId).stream()
                .map(e -> e.effortHours() == null ? BigDecimal.ZERO : e.effortHours())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actual = BigDecimal.ZERO;
        BigDecimal remaining = forecast.subtract(actual);
        if (forecast.compareTo(BigDecimal.ZERO) == 0) {
            risks.save(ResourceRiskFlag.open(workspaceId, projectId, null, "MISSING_ESTIMATE", "DELIVERY_RISK",
                    "No active effort estimates for project"));
        }
        return new EffortForecastResponse(projectId, forecast, actual, remaining, "ACTIVE");
    }

    public UtilizationSummaryResponse computeUtilization(UUID resourceProfileId, BigDecimal availableHours, BigDecimal assignedHours,
                                                         UtilizationThresholdPolicy policy) {
        BigDecimal planned = ResourceUtilizationCalculator.utilizationPercent(assignedHours, availableHours);
        BigDecimal overload = ResourceUtilizationCalculator.overloadHours(assignedHours, availableHours);
        BigDecimal under = ResourceUtilizationCalculator.underAllocationHours(assignedHours, availableHours);
        String status = ResourceUtilizationCalculator.utilizationStatus(planned,
                policy.underAllocatedPercent(), policy.healthyMinPercent(), policy.healthyMaxPercent(),
                policy.watchMaxPercent(), policy.overloadedPercent(), policy.criticalOverloadPercent());
        return new UtilizationSummaryResponse(resourceProfileId, availableHours, assignedHours, planned, overload, under, status);
    }

    @Transactional
    public ProjectCapacitySummaryResponse rebuildProjectCapacity(UUID workspaceId, UUID projectId) {
        BigDecimal required = estimates.findActiveByProjectId(projectId).stream()
                .map(e -> e.effortHours() == null ? BigDecimal.ZERO : e.effortHours())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocated = allocations.findActiveByProjectId(projectId).stream()
                .map(a -> a.allocationPercent() == null ? BigDecimal.ZERO : a.allocationPercent())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gap = required.subtract(allocated);
        String risk = gap.compareTo(BigDecimal.ZERO) > 0 ? "CAPACITY_GAP" : "HEALTHY";
        if (gap.compareTo(BigDecimal.ZERO) > 0) {
            risks.save(ResourceRiskFlag.open(workspaceId, projectId, null, "CAPACITY_GAP", "DELIVERY_RISK",
                    "Capacity gap of " + gap));
        }
        ResourceCostInputResponse cost = rebuildCostInput(workspaceId, projectId, false);
        return new ProjectCapacitySummaryResponse(projectId, required, allocated, gap, risk, cost.totalCostAmount());
    }

    @Transactional
    public ResourceCostInputResponse rebuildCostInput(UUID workspaceId, UUID projectId, boolean includeSensitive) {
        List<ResourceCostInputResponse.Line> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        boolean rateMissing = false;
        for (var est : estimates.findActiveByProjectId(projectId)) {
            // Phase 37: missing rate creates risk, does not crash. Rates come from role/rate-card linkage when available.
            BigDecimal rate = BigDecimal.ZERO;
            rateMissing = true;
            risks.save(ResourceRiskFlag.open(workspaceId, projectId, est.resourceProfileId(), "MISSING_RATE", "COST_RISK",
                    "Missing rate for estimate " + est.id()));
            BigDecimal cost = ResourceUtilizationCalculator.costAmount(est.effortHours(), rate);
            total = total.add(cost);
            lines.add(new ResourceCostInputResponse.Line(est.resourceRoleId(), est.effortHours(),
                    includeSensitive ? rate : null, includeSensitive ? cost : null, !includeSensitive));
        }
        return new ResourceCostInputResponse(projectId, includeSensitive ? total : null, "USD", rateMissing, lines);
    }

    @Transactional(readOnly = true)
    public UtilizationThresholdPolicy resolveThreshold(UUID workspaceId, UUID projectId) {
        return thresholds.findByProjectId(projectId)
                .or(() -> thresholds.findByWorkspaceIdAndProjectIdIsNull(workspaceId))
                .orElseGet(() -> UtilizationThresholdPolicy.defaults(workspaceId, projectId));
    }
}
