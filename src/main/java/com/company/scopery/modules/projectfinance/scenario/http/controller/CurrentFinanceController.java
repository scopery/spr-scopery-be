package com.company.scopery.modules.projectfinance.scenario.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectfinance.phasefinance.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.application.service.FinanceScenarioQueryService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceApiPaths;
import com.company.scopery.modules.projectfinance.summary.application.response.FinanceSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectFinanceApiPaths.CURRENT_FINANCE)
@Tag(name = "Project Finance - Current")
public class CurrentFinanceController {

    private final FinanceScenarioQueryService query;

    public CurrentFinanceController(FinanceScenarioQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "Get current finance scenario")
    public ApiResponse<FinanceScenarioResponse> get(@PathVariable UUID projectId) {
        return ApiResponse.success(query.getCurrent(projectId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get current finance summary")
    public ApiResponse<FinanceSummaryResponse> summary(@PathVariable UUID projectId) {
        return ApiResponse.success(query.getCurrentSummary(projectId));
    }

    @GetMapping("/phases")
    @Operation(summary = "Get current phase finance rows")
    public ApiResponse<List<PhaseFinanceResponse>> phases(@PathVariable UUID projectId) {
        return ApiResponse.success(query.getCurrentPhases(projectId));
    }
}
