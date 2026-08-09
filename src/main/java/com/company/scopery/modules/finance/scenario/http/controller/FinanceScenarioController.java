package com.company.scopery.modules.finance.scenario.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.finance.scenario.application.action.ApproveFinanceScenarioAction;
import com.company.scopery.modules.finance.scenario.application.action.ArchiveCustomCostAction;
import com.company.scopery.modules.finance.scenario.application.action.ArchiveFinanceScenarioAction;
import com.company.scopery.modules.finance.scenario.application.action.ArchiveVendorCostAction;
import com.company.scopery.modules.finance.scenario.application.action.CreateCustomCostAction;
import com.company.scopery.modules.finance.scenario.application.action.CreateFinanceScenarioAction;
import com.company.scopery.modules.finance.scenario.application.action.CreateVendorCostAction;
import com.company.scopery.modules.finance.scenario.application.action.DuplicateFinanceScenarioAction;
import com.company.scopery.modules.finance.scenario.application.action.MarkCurrentFinanceScenarioAction;
import com.company.scopery.modules.finance.scenario.application.action.RecalculateFinanceSummaryAction;
import com.company.scopery.modules.finance.scenario.application.action.UpdateCustomCostAction;
import com.company.scopery.modules.finance.scenario.application.action.UpdateFinanceScenarioAction;
import com.company.scopery.modules.finance.scenario.application.action.UpdatePhaseRevenueAction;
import com.company.scopery.modules.finance.scenario.application.action.UpdateVendorCostAction;
import com.company.scopery.modules.finance.scenario.application.command.CreateCustomCostCommand;
import com.company.scopery.modules.finance.scenario.application.command.CreateFinanceScenarioCommand;
import com.company.scopery.modules.finance.scenario.application.command.CreateVendorCostCommand;
import com.company.scopery.modules.finance.scenario.application.command.UpdateCustomCostCommand;
import com.company.scopery.modules.finance.scenario.application.command.UpdateFinanceScenarioCommand;
import com.company.scopery.modules.finance.scenario.application.command.UpdatePhaseRevenueCommand;
import com.company.scopery.modules.finance.scenario.application.command.UpdateVendorCostCommand;
import com.company.scopery.modules.finance.scenario.application.response.CustomCostResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceCompareResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceSummaryResponse;
import com.company.scopery.modules.finance.scenario.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.finance.scenario.application.response.VendorCostResponse;
import com.company.scopery.modules.finance.scenario.application.service.FinanceQueryService;
import com.company.scopery.modules.finance.scenario.http.request.CreateCustomCostRequest;
import com.company.scopery.modules.finance.scenario.http.request.CreateFinanceScenarioRequest;
import com.company.scopery.modules.finance.scenario.http.request.CreateVendorCostRequest;
import com.company.scopery.modules.finance.scenario.http.request.DuplicateFinanceScenarioRequest;
import com.company.scopery.modules.finance.scenario.http.request.UpdateCustomCostRequest;
import com.company.scopery.modules.finance.scenario.http.request.UpdateFinanceScenarioRequest;
import com.company.scopery.modules.finance.scenario.http.request.UpdatePhaseRevenueRequest;
import com.company.scopery.modules.finance.scenario.http.request.UpdateVendorCostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Finance - Scenarios")
public class FinanceScenarioController {

    private final CreateFinanceScenarioAction createScenario;
    private final UpdateFinanceScenarioAction updateScenario;
    private final ApproveFinanceScenarioAction approveScenario;
    private final ArchiveFinanceScenarioAction archiveScenario;
    private final MarkCurrentFinanceScenarioAction markCurrent;
    private final RecalculateFinanceSummaryAction recalculate;
    private final DuplicateFinanceScenarioAction duplicateScenario;
    private final CreateCustomCostAction createCustomCost;
    private final UpdateCustomCostAction updateCustomCost;
    private final ArchiveCustomCostAction archiveCustomCost;
    private final CreateVendorCostAction createVendorCost;
    private final UpdateVendorCostAction updateVendorCost;
    private final ArchiveVendorCostAction archiveVendorCost;
    private final UpdatePhaseRevenueAction updatePhaseRevenue;
    private final FinanceQueryService queryService;

    public FinanceScenarioController(
            CreateFinanceScenarioAction createScenario,
            UpdateFinanceScenarioAction updateScenario,
            ApproveFinanceScenarioAction approveScenario,
            ArchiveFinanceScenarioAction archiveScenario,
            MarkCurrentFinanceScenarioAction markCurrent,
            RecalculateFinanceSummaryAction recalculate,
            DuplicateFinanceScenarioAction duplicateScenario,
            CreateCustomCostAction createCustomCost,
            UpdateCustomCostAction updateCustomCost,
            ArchiveCustomCostAction archiveCustomCost,
            CreateVendorCostAction createVendorCost,
            UpdateVendorCostAction updateVendorCost,
            ArchiveVendorCostAction archiveVendorCost,
            UpdatePhaseRevenueAction updatePhaseRevenue,
            FinanceQueryService queryService) {
        this.createScenario = createScenario;
        this.updateScenario = updateScenario;
        this.approveScenario = approveScenario;
        this.archiveScenario = archiveScenario;
        this.markCurrent = markCurrent;
        this.recalculate = recalculate;
        this.duplicateScenario = duplicateScenario;
        this.createCustomCost = createCustomCost;
        this.updateCustomCost = updateCustomCost;
        this.archiveCustomCost = archiveCustomCost;
        this.createVendorCost = createVendorCost;
        this.updateVendorCost = updateVendorCost;
        this.archiveVendorCost = archiveVendorCost;
        this.updatePhaseRevenue = updatePhaseRevenue;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a finance scenario")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> createScenario(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateFinanceScenarioRequest request) {
        CreateFinanceScenarioCommand command = new CreateFinanceScenarioCommand(
                projectId, null, request.estimationRunId(),
                request.code(), request.name(), request.description(),
                request.currencyCode(), request.plannedRevenue(),
                request.revenueSplitMethod(),
                request.contingencyMethod(), request.contingencyPercent(), request.contingencyFixedAmount(),
                request.overheadMethod(), request.overheadPercent(), request.overheadFixedAmount(),
                request.targetMarginPercent(), request.assumptionsJson(),
                Boolean.TRUE.equals(request.markAsCurrent()));
        return ResponseEntity.ok(ApiResponse.success(createScenario.execute(command)));
    }

    @GetMapping
    @Operation(summary = "List all finance scenarios for a project")
    public ResponseEntity<ApiResponse<List<FinanceScenarioResponse>>> listScenarios(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.listScenarios(projectId)));
    }

    @GetMapping("/{scenarioId}")
    @Operation(summary = "Get a finance scenario by ID")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> getScenario(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getScenario(projectId, scenarioId)));
    }

    @PutMapping("/{scenarioId}")
    @Operation(summary = "Update a finance scenario")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> updateScenario(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @RequestBody UpdateFinanceScenarioRequest request) {
        UpdateFinanceScenarioCommand command = new UpdateFinanceScenarioCommand(
                projectId, scenarioId,
                request.name(), request.description(),
                request.plannedRevenue(), request.revenueSplitMethod(),
                request.contingencyMethod(), request.contingencyPercent(), request.contingencyFixedAmount(),
                request.overheadMethod(), request.overheadPercent(), request.overheadFixedAmount(),
                request.targetMarginPercent(), request.assumptionsJson());
        return ResponseEntity.ok(ApiResponse.success(updateScenario.execute(command)));
    }

    @PostMapping("/{scenarioId}/approve")
    @Operation(summary = "Approve a finance scenario")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> approveScenario(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(approveScenario.execute(projectId, scenarioId)));
    }

    @PatchMapping("/{scenarioId}/archive")
    @Operation(summary = "Archive a finance scenario")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> archiveScenario(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(archiveScenario.execute(projectId, scenarioId)));
    }

    @PostMapping("/{scenarioId}/mark-current")
    @Operation(summary = "Mark a finance scenario as current")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> markCurrent(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(markCurrent.execute(projectId, scenarioId)));
    }

    @PostMapping("/{scenarioId}/recalculate")
    @Operation(summary = "Recalculate finance summary for a scenario")
    public ResponseEntity<ApiResponse<Void>> recalculate(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        recalculate.execute(scenarioId, projectId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{scenarioId}/duplicate")
    @Operation(summary = "Duplicate a finance scenario")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> duplicate(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @Valid @RequestBody DuplicateFinanceScenarioRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                duplicateScenario.execute(projectId, scenarioId, request.newCode(), request.newName())));
    }

    @GetMapping("/{scenarioId}/summary")
    @Operation(summary = "Get finance summary for a scenario")
    public ResponseEntity<ApiResponse<FinanceSummaryResponse>> getSummary(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getSummary(projectId, scenarioId)));
    }

    @GetMapping("/{scenarioId}/phases")
    @Operation(summary = "Get phase finance breakdown for a scenario")
    public ResponseEntity<ApiResponse<List<PhaseFinanceResponse>>> getPhases(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getPhases(projectId, scenarioId)));
    }

    @PutMapping("/{scenarioId}/phases/{phaseId}/revenue")
    @Operation(summary = "Update phase revenue (manual split only)")
    public ResponseEntity<ApiResponse<PhaseFinanceResponse>> updatePhaseRevenue(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @PathVariable UUID phaseId,
            @Valid @RequestBody UpdatePhaseRevenueRequest request) {
        UpdatePhaseRevenueCommand command = new UpdatePhaseRevenueCommand(
                projectId, scenarioId, phaseId,
                request.plannedRevenue(), request.revenuePercent());
        return ResponseEntity.ok(ApiResponse.success(updatePhaseRevenue.execute(command)));
    }

    @GetMapping("/{scenarioId}/custom-costs")
    @Operation(summary = "Get custom costs for a scenario")
    public ResponseEntity<ApiResponse<List<CustomCostResponse>>> getCustomCosts(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getCustomCosts(projectId, scenarioId)));
    }

    @PostMapping("/{scenarioId}/custom-costs")
    @Operation(summary = "Create a custom cost for a scenario")
    public ResponseEntity<ApiResponse<CustomCostResponse>> createCustomCost(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @Valid @RequestBody CreateCustomCostRequest request) {
        CreateCustomCostCommand command = new CreateCustomCostCommand(
                projectId, scenarioId, request.projectPhaseId(),
                request.category(), request.name(), request.description(),
                request.amount(), request.currencyCode(), request.costDate());
        return ResponseEntity.ok(ApiResponse.success(createCustomCost.execute(command)));
    }

    @PutMapping("/{scenarioId}/custom-costs/{costId}")
    @Operation(summary = "Update a custom cost")
    public ResponseEntity<ApiResponse<CustomCostResponse>> updateCustomCost(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @PathVariable UUID costId,
            @RequestBody UpdateCustomCostRequest request) {
        UpdateCustomCostCommand command = new UpdateCustomCostCommand(
                projectId, scenarioId, costId,
                request.category(), request.name(), request.description(),
                request.amount(), request.currencyCode(), request.costDate());
        return ResponseEntity.ok(ApiResponse.success(updateCustomCost.execute(command)));
    }

    @PatchMapping("/{scenarioId}/custom-costs/{costId}/archive")
    @Operation(summary = "Archive a custom cost")
    public ResponseEntity<ApiResponse<CustomCostResponse>> archiveCustomCost(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @PathVariable UUID costId) {
        return ResponseEntity.ok(ApiResponse.success(archiveCustomCost.execute(projectId, scenarioId, costId)));
    }

    @GetMapping("/{scenarioId}/vendor-costs")
    @Operation(summary = "Get vendor costs for a scenario")
    public ResponseEntity<ApiResponse<List<VendorCostResponse>>> getVendorCosts(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getVendorCosts(projectId, scenarioId)));
    }

    @PostMapping("/{scenarioId}/vendor-costs")
    @Operation(summary = "Create a vendor cost for a scenario")
    public ResponseEntity<ApiResponse<VendorCostResponse>> createVendorCost(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @Valid @RequestBody CreateVendorCostRequest request) {
        CreateVendorCostCommand command = new CreateVendorCostCommand(
                projectId, scenarioId, request.projectPhaseId(),
                request.vendorName(), request.description(),
                request.amount(), request.currencyCode());
        return ResponseEntity.ok(ApiResponse.success(createVendorCost.execute(command)));
    }

    @PutMapping("/{scenarioId}/vendor-costs/{costId}")
    @Operation(summary = "Update a vendor cost")
    public ResponseEntity<ApiResponse<VendorCostResponse>> updateVendorCost(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @PathVariable UUID costId,
            @RequestBody UpdateVendorCostRequest request) {
        UpdateVendorCostCommand command = new UpdateVendorCostCommand(
                projectId, scenarioId, costId,
                request.vendorName(), request.description(),
                request.amount(), request.currencyCode());
        return ResponseEntity.ok(ApiResponse.success(updateVendorCost.execute(command)));
    }

    @PatchMapping("/{scenarioId}/vendor-costs/{costId}/archive")
    @Operation(summary = "Archive a vendor cost")
    public ResponseEntity<ApiResponse<VendorCostResponse>> archiveVendorCost(
            @PathVariable UUID projectId,
            @PathVariable UUID scenarioId,
            @PathVariable UUID costId) {
        return ResponseEntity.ok(ApiResponse.success(archiveVendorCost.execute(projectId, scenarioId, costId)));
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare two finance scenarios")
    public ResponseEntity<ApiResponse<FinanceCompareResponse>> compareScenarios(
            @PathVariable UUID projectId,
            @RequestParam UUID leftScenarioId,
            @RequestParam UUID rightScenarioId) {
        return ResponseEntity.ok(ApiResponse.success(
                queryService.compareScenarios(projectId, leftScenarioId, rightScenarioId)));
    }
}
