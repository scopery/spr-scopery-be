package com.company.scopery.modules.projectfinance.scenario.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectfinance.customcost.application.action.ArchiveCustomCostAction;
import com.company.scopery.modules.projectfinance.customcost.application.action.CreateCustomCostAction;
import com.company.scopery.modules.projectfinance.customcost.application.action.UpdateCustomCostAction;
import com.company.scopery.modules.projectfinance.customcost.application.command.CreateCustomCostCommand;
import com.company.scopery.modules.projectfinance.customcost.application.command.UpdateCustomCostCommand;
import com.company.scopery.modules.projectfinance.customcost.application.response.CustomCostResponse;
import com.company.scopery.modules.projectfinance.customcost.http.request.CreateCustomCostRequest;
import com.company.scopery.modules.projectfinance.customcost.http.request.UpdateCustomCostRequest;
import com.company.scopery.modules.projectfinance.phasefinance.application.action.UpdatePhaseRevenueAction;
import com.company.scopery.modules.projectfinance.phasefinance.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.projectfinance.phasefinance.http.request.UpdatePhaseRevenueRequest;
import com.company.scopery.modules.projectfinance.scenario.application.action.ApproveFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.action.ArchiveFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.action.CreateFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.action.DuplicateFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.action.MarkCurrentFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.action.RecalculateFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.action.UpdateFinanceScenarioAction;
import com.company.scopery.modules.projectfinance.scenario.application.command.CreateFinanceScenarioCommand;
import com.company.scopery.modules.projectfinance.scenario.application.command.UpdateFinanceScenarioCommand;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioCompareResponse;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.application.service.FinanceScenarioQueryService;
import com.company.scopery.modules.projectfinance.scenario.http.request.CreateFinanceScenarioRequest;
import com.company.scopery.modules.projectfinance.scenario.http.request.UpdateFinanceScenarioRequest;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceApiPaths;
import com.company.scopery.modules.projectfinance.summary.application.response.FinanceSummaryResponse;
import com.company.scopery.modules.projectfinance.vendorcost.application.action.ArchiveVendorCostAction;
import com.company.scopery.modules.projectfinance.vendorcost.application.action.CreateVendorCostAction;
import com.company.scopery.modules.projectfinance.vendorcost.application.action.UpdateVendorCostAction;
import com.company.scopery.modules.projectfinance.vendorcost.application.command.CreateVendorCostCommand;
import com.company.scopery.modules.projectfinance.vendorcost.application.command.UpdateVendorCostCommand;
import com.company.scopery.modules.projectfinance.vendorcost.application.response.VendorCostResponse;
import com.company.scopery.modules.projectfinance.vendorcost.http.request.CreateVendorCostRequest;
import com.company.scopery.modules.projectfinance.vendorcost.http.request.UpdateVendorCostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectFinanceApiPaths.FINANCE_SCENARIOS)
@Tag(name = "Project Finance - Scenarios")
public class FinanceScenarioController {

    private final CreateFinanceScenarioAction create;
    private final UpdateFinanceScenarioAction update;
    private final RecalculateFinanceScenarioAction recalculate;
    private final ApproveFinanceScenarioAction approve;
    private final MarkCurrentFinanceScenarioAction markCurrent;
    private final ArchiveFinanceScenarioAction archive;
    private final DuplicateFinanceScenarioAction duplicate;
    private final UpdatePhaseRevenueAction updatePhaseRevenue;
    private final CreateCustomCostAction createCustomCost;
    private final UpdateCustomCostAction updateCustomCost;
    private final ArchiveCustomCostAction archiveCustomCost;
    private final CreateVendorCostAction createVendorCost;
    private final UpdateVendorCostAction updateVendorCost;
    private final ArchiveVendorCostAction archiveVendorCost;
    private final FinanceScenarioQueryService query;

    public FinanceScenarioController(CreateFinanceScenarioAction create,
                                     UpdateFinanceScenarioAction update,
                                     RecalculateFinanceScenarioAction recalculate,
                                     ApproveFinanceScenarioAction approve,
                                     MarkCurrentFinanceScenarioAction markCurrent,
                                     ArchiveFinanceScenarioAction archive,
                                     DuplicateFinanceScenarioAction duplicate,
                                     UpdatePhaseRevenueAction updatePhaseRevenue,
                                     CreateCustomCostAction createCustomCost,
                                     UpdateCustomCostAction updateCustomCost,
                                     ArchiveCustomCostAction archiveCustomCost,
                                     CreateVendorCostAction createVendorCost,
                                     UpdateVendorCostAction updateVendorCost,
                                     ArchiveVendorCostAction archiveVendorCost,
                                     FinanceScenarioQueryService query) {
        this.create = create;
        this.update = update;
        this.recalculate = recalculate;
        this.approve = approve;
        this.markCurrent = markCurrent;
        this.archive = archive;
        this.duplicate = duplicate;
        this.updatePhaseRevenue = updatePhaseRevenue;
        this.createCustomCost = createCustomCost;
        this.updateCustomCost = updateCustomCost;
        this.archiveCustomCost = archiveCustomCost;
        this.createVendorCost = createVendorCost;
        this.updateVendorCost = updateVendorCost;
        this.archiveVendorCost = archiveVendorCost;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create finance scenario from estimation run")
    public ApiResponse<FinanceScenarioResponse> create(@PathVariable UUID projectId,
                                                       @Valid @RequestBody CreateFinanceScenarioRequest request) {
        CreateFinanceScenarioRequest.PolicyRequest contingency = request.contingency();
        CreateFinanceScenarioRequest.PolicyRequest overhead = request.overhead();
        return ApiResponse.success(create.execute(new CreateFinanceScenarioCommand(
                projectId, request.name(), request.description(), request.code(), request.estimationRunId(),
                request.currencyCode(), request.plannedRevenue(), request.revenueSplitMethod(),
                contingency == null ? null : contingency.method(),
                contingency == null ? null : contingency.percent(),
                contingency == null ? null : contingency.fixedAmount(),
                overhead == null ? null : overhead.method(),
                overhead == null ? null : overhead.percent(),
                overhead == null ? null : overhead.fixedAmount(),
                request.targetMarginPercent(), request.assumptionsJson(),
                Boolean.TRUE.equals(request.markAsCurrent()))));
    }

    @GetMapping
    @Operation(summary = "List finance scenarios")
    public ApiResponse<List<FinanceScenarioResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{scenarioId}")
    @Operation(summary = "Get finance scenario")
    public ApiResponse<FinanceScenarioResponse> get(@PathVariable UUID projectId,
                                                    @PathVariable UUID scenarioId) {
        return ApiResponse.success(query.get(projectId, scenarioId));
    }

    @PutMapping("/{scenarioId}")
    @Operation(summary = "Update draft finance scenario")
    public ApiResponse<FinanceScenarioResponse> update(@PathVariable UUID projectId,
                                                       @PathVariable UUID scenarioId,
                                                       @Valid @RequestBody UpdateFinanceScenarioRequest request) {
        CreateFinanceScenarioRequest.PolicyRequest contingency = request.contingency();
        CreateFinanceScenarioRequest.PolicyRequest overhead = request.overhead();
        return ApiResponse.success(update.execute(new UpdateFinanceScenarioCommand(
                projectId, scenarioId, request.name(), request.description(), request.plannedRevenue(),
                request.revenueSplitMethod(),
                contingency == null ? null : contingency.method(),
                contingency == null ? null : contingency.percent(),
                contingency == null ? null : contingency.fixedAmount(),
                overhead == null ? null : overhead.method(),
                overhead == null ? null : overhead.percent(),
                overhead == null ? null : overhead.fixedAmount(),
                request.targetMarginPercent(), request.assumptionsJson())));
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare two finance scenarios side-by-side")
    public ApiResponse<FinanceScenarioCompareResponse> compare(
            @PathVariable UUID projectId,
            @org.springframework.web.bind.annotation.RequestParam UUID leftScenarioId,
            @org.springframework.web.bind.annotation.RequestParam UUID rightScenarioId) {
        return ApiResponse.success(query.compare(projectId, leftScenarioId, rightScenarioId));
    }

    @PostMapping("/{scenarioId}/recalculate")
    @Operation(summary = "Recalculate draft finance scenario")
    public ApiResponse<FinanceScenarioResponse> recalculate(@PathVariable UUID projectId,
                                                            @PathVariable UUID scenarioId) {
        return ApiResponse.success(recalculate.execute(projectId, scenarioId));
    }

    @PostMapping("/{scenarioId}/approve")
    @Operation(summary = "Approve finance scenario")
    public ApiResponse<FinanceScenarioResponse> approve(@PathVariable UUID projectId,
                                                        @PathVariable UUID scenarioId) {
        return ApiResponse.success(approve.execute(projectId, scenarioId));
    }

    @PostMapping("/{scenarioId}/mark-current")
    @Operation(summary = "Mark finance scenario as current")
    public ApiResponse<FinanceScenarioResponse> markCurrent(@PathVariable UUID projectId,
                                                            @PathVariable UUID scenarioId) {
        return ApiResponse.success(markCurrent.execute(projectId, scenarioId));
    }

    @PatchMapping("/{scenarioId}/archive")
    @Operation(summary = "Archive finance scenario")
    public ApiResponse<FinanceScenarioResponse> archive(@PathVariable UUID projectId,
                                                        @PathVariable UUID scenarioId) {
        return ApiResponse.success(archive.execute(projectId, scenarioId));
    }

    @PostMapping("/{scenarioId}/duplicate")
    @Operation(summary = "Duplicate finance scenario as DRAFT")
    public ApiResponse<FinanceScenarioResponse> duplicate(@PathVariable UUID projectId,
                                                          @PathVariable UUID scenarioId) {
        return ApiResponse.success(duplicate.execute(projectId, scenarioId));
    }

    @GetMapping("/{scenarioId}/phases")
    @Operation(summary = "List phase finance rows")
    public ApiResponse<List<PhaseFinanceResponse>> phases(@PathVariable UUID projectId,
                                                          @PathVariable UUID scenarioId) {
        return ApiResponse.success(query.listPhases(projectId, scenarioId));
    }

    @GetMapping("/{scenarioId}/phases/{phaseFinanceId}")
    @Operation(summary = "Get phase finance row")
    public ApiResponse<PhaseFinanceResponse> phase(@PathVariable UUID projectId,
                                                   @PathVariable UUID scenarioId,
                                                   @PathVariable UUID phaseFinanceId) {
        return ApiResponse.success(query.getPhase(projectId, scenarioId, phaseFinanceId));
    }

    @PutMapping("/{scenarioId}/phases/{phaseFinanceId}/revenue")
    @Operation(summary = "Update phase planned revenue / percent")
    public ApiResponse<PhaseFinanceResponse> updateRevenue(@PathVariable UUID projectId,
                                                           @PathVariable UUID scenarioId,
                                                           @PathVariable UUID phaseFinanceId,
                                                           @Valid @RequestBody UpdatePhaseRevenueRequest request) {
        return ApiResponse.success(updatePhaseRevenue.execute(
                projectId, scenarioId, phaseFinanceId, request.plannedRevenue(), request.revenuePercent()));
    }

    @GetMapping("/{scenarioId}/summary")
    @Operation(summary = "Get finance summary")
    public ApiResponse<FinanceSummaryResponse> summary(@PathVariable UUID projectId,
                                                       @PathVariable UUID scenarioId) {
        return ApiResponse.success(query.getSummary(projectId, scenarioId));
    }

    @PostMapping("/{scenarioId}/custom-costs")
    @Operation(summary = "Create custom cost")
    public ApiResponse<CustomCostResponse> createCustomCost(@PathVariable UUID projectId,
                                                            @PathVariable UUID scenarioId,
                                                            @Valid @RequestBody CreateCustomCostRequest request) {
        return ApiResponse.success(createCustomCost.execute(new CreateCustomCostCommand(
                projectId, scenarioId, request.projectPhaseId(), request.category(), request.name(),
                request.description(), request.amount(), request.currencyCode(), request.costDate())));
    }

    @GetMapping("/{scenarioId}/custom-costs")
    @Operation(summary = "List custom costs")
    public ApiResponse<List<CustomCostResponse>> listCustomCosts(@PathVariable UUID projectId,
                                                                 @PathVariable UUID scenarioId) {
        return ApiResponse.success(query.listCustomCosts(projectId, scenarioId));
    }

    @GetMapping("/{scenarioId}/custom-costs/{costId}")
    @Operation(summary = "Get custom cost")
    public ApiResponse<CustomCostResponse> getCustomCost(@PathVariable UUID projectId,
                                                         @PathVariable UUID scenarioId,
                                                         @PathVariable UUID costId) {
        return ApiResponse.success(query.getCustomCost(projectId, scenarioId, costId));
    }

    @PutMapping("/{scenarioId}/custom-costs/{costId}")
    @Operation(summary = "Update custom cost")
    public ApiResponse<CustomCostResponse> updateCustomCost(@PathVariable UUID projectId,
                                                            @PathVariable UUID scenarioId,
                                                            @PathVariable UUID costId,
                                                            @Valid @RequestBody UpdateCustomCostRequest request) {
        return ApiResponse.success(updateCustomCost.execute(new UpdateCustomCostCommand(
                projectId, scenarioId, costId, request.projectPhaseId(), request.category(), request.name(),
                request.description(), request.amount(), request.currencyCode(), request.costDate())));
    }

    @PatchMapping("/{scenarioId}/custom-costs/{costId}/archive")
    @Operation(summary = "Archive custom cost")
    public ApiResponse<CustomCostResponse> archiveCustomCost(@PathVariable UUID projectId,
                                                             @PathVariable UUID scenarioId,
                                                             @PathVariable UUID costId) {
        return ApiResponse.success(archiveCustomCost.execute(projectId, scenarioId, costId));
    }

    @PostMapping("/{scenarioId}/vendor-costs")
    @Operation(summary = "Create vendor cost")
    public ApiResponse<VendorCostResponse> createVendorCost(@PathVariable UUID projectId,
                                                            @PathVariable UUID scenarioId,
                                                            @Valid @RequestBody CreateVendorCostRequest request) {
        return ApiResponse.success(createVendorCost.execute(new CreateVendorCostCommand(
                projectId, scenarioId, request.projectPhaseId(), request.vendorName(),
                request.description(), request.amount(), request.currencyCode())));
    }

    @GetMapping("/{scenarioId}/vendor-costs")
    @Operation(summary = "List vendor costs")
    public ApiResponse<List<VendorCostResponse>> listVendorCosts(@PathVariable UUID projectId,
                                                                 @PathVariable UUID scenarioId) {
        return ApiResponse.success(query.listVendorCosts(projectId, scenarioId));
    }

    @GetMapping("/{scenarioId}/vendor-costs/{costId}")
    @Operation(summary = "Get vendor cost")
    public ApiResponse<VendorCostResponse> getVendorCost(@PathVariable UUID projectId,
                                                         @PathVariable UUID scenarioId,
                                                         @PathVariable UUID costId) {
        return ApiResponse.success(query.getVendorCost(projectId, scenarioId, costId));
    }

    @PutMapping("/{scenarioId}/vendor-costs/{costId}")
    @Operation(summary = "Update vendor cost")
    public ApiResponse<VendorCostResponse> updateVendorCost(@PathVariable UUID projectId,
                                                            @PathVariable UUID scenarioId,
                                                            @PathVariable UUID costId,
                                                            @Valid @RequestBody UpdateVendorCostRequest request) {
        return ApiResponse.success(updateVendorCost.execute(new UpdateVendorCostCommand(
                projectId, scenarioId, costId, request.projectPhaseId(), request.vendorName(),
                request.description(), request.amount(), request.currencyCode())));
    }

    @PatchMapping("/{scenarioId}/vendor-costs/{costId}/archive")
    @Operation(summary = "Archive vendor cost")
    public ApiResponse<VendorCostResponse> archiveVendorCost(@PathVariable UUID projectId,
                                                             @PathVariable UUID scenarioId,
                                                             @PathVariable UUID costId) {
        return ApiResponse.success(archiveVendorCost.execute(projectId, scenarioId, costId));
    }
}
