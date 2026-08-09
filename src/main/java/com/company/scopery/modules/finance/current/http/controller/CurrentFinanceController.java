package com.company.scopery.modules.finance.current.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.finance.scenario.application.response.FinanceSummaryResponse;
import com.company.scopery.modules.finance.scenario.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.finance.scenario.application.service.FinanceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Finance - Current")
public class CurrentFinanceController {

    private final FinanceQueryService queryService;

    public CurrentFinanceController(FinanceQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/current")
    @Operation(summary = "Get the current finance scenario for a project")
    public ResponseEntity<ApiResponse<FinanceScenarioResponse>> getCurrent(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getCurrent(projectId)));
    }

    @GetMapping("/current/summary")
    @Operation(summary = "Get the current finance summary for a project")
    public ResponseEntity<ApiResponse<FinanceSummaryResponse>> getCurrentSummary(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getCurrentSummary(projectId)));
    }

    @GetMapping("/current/phases")
    @Operation(summary = "Get phase breakdown for the current finance scenario")
    public ResponseEntity<ApiResponse<List<PhaseFinanceResponse>>> getCurrentPhases(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getCurrentPhases(projectId)));
    }
}
