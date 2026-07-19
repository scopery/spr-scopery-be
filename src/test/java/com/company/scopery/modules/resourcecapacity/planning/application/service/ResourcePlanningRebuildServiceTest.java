package com.company.scopery.modules.resourcecapacity.planning.application.service;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.enums.EffortEstimateType;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimate;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimateRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlagRepository;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicy;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicyRepository;
import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor; import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal; import java.util.List; import java.util.Optional; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ResourcePlanningRebuildServiceTest {
    @Mock EffortEstimateRepository estimates; @Mock ProjectResourceAllocationRepository allocations;
    @Mock UtilizationThresholdPolicyRepository thresholds; @Mock ResourceRiskFlagRepository risks;
    ResourcePlanningRebuildService service;
    UUID workspaceId = UUID.randomUUID(); UUID projectId = UUID.randomUUID();
    @BeforeEach void setUp() {
        service = new ResourcePlanningRebuildService(estimates, allocations, thresholds, risks);
        lenient().when(risks.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(allocations.findActiveByProjectId(any())).thenReturn(List.of());
    }
    @Test void rebuildEffortForecast_success() {
        when(estimates.findActiveByProjectId(projectId)).thenReturn(List.of(
                EffortEstimate.create(workspaceId, projectId, "TASK", UUID.randomUUID(), EffortEstimateType.INITIAL, new BigDecimal("10"), null, null, null)));
        var r = service.rebuildForecast(workspaceId, projectId);
        assertThat(r.forecastEffortHours()).isEqualByComparingTo("10");
    }
    @Test void capacityGapCreatesRiskFlag() {
        when(estimates.findActiveByProjectId(projectId)).thenReturn(List.of(
                EffortEstimate.create(workspaceId, projectId, "TASK", UUID.randomUUID(), EffortEstimateType.INITIAL, new BigDecimal("100"), null, null, null)));
        var r = service.rebuildProjectCapacity(workspaceId, projectId);
        assertThat(r.capacityGapHours()).isEqualByComparingTo("100");
        assertThat(r.forecastCompletionRisk()).isEqualTo("CAPACITY_GAP");
        verify(risks, atLeastOnce()).save(any());
    }
    @Test void missingRateCreatesRiskNotCrash() {
        when(estimates.findActiveByProjectId(projectId)).thenReturn(List.of(
                EffortEstimate.create(workspaceId, projectId, "TASK", UUID.randomUUID(), EffortEstimateType.INITIAL, new BigDecimal("5"), UUID.randomUUID(), null, null)));
        var r = service.rebuildCostInput(workspaceId, projectId, true);
        assertThat(r.rateMissing()).isTrue();
        assertThat(r.totalCostAmount()).isEqualByComparingTo("0.0000");
        verify(risks, atLeastOnce()).save(any());
    }
    @Test void costInputSensitiveMaskedWithoutPermission() {
        when(estimates.findActiveByProjectId(projectId)).thenReturn(List.of(
                EffortEstimate.create(workspaceId, projectId, "TASK", UUID.randomUUID(), EffortEstimateType.INITIAL, new BigDecimal("5"), null, null, null)));
        var r = service.rebuildCostInput(workspaceId, projectId, false);
        assertThat(r.totalCostAmount()).isNull();
        assertThat(r.lines().get(0).masked()).isTrue();
    }
    @Test void resourceUtilizationCalculated_success() {
        var policy = UtilizationThresholdPolicy.defaults(workspaceId, projectId);
        var r = service.computeUtilization(UUID.randomUUID(), new BigDecimal("40"), new BigDecimal("40"), policy);
        assertThat(r.utilizationStatus()).isEqualTo("WATCH");
        assertThat(r.plannedUtilizationPercent()).isEqualByComparingTo("100");
    }
}
