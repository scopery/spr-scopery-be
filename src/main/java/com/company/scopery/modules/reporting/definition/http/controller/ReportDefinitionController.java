package com.company.scopery.modules.reporting.definition.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.definition.application.response.ReportDefinitionResponse;
import com.company.scopery.modules.reporting.definition.application.service.ReportDefinitionQueryService;
import com.company.scopery.modules.reporting.shared.constant.ReportingApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ReportingApiPaths.DEFINITIONS)
@Tag(name = "Reporting - Definitions")
public class ReportDefinitionController {
    private final ReportDefinitionQueryService query;

    public ReportDefinitionController(ReportDefinitionQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "List report definitions")
    public ApiResponse<List<ReportDefinitionResponse>> list() {
        return ApiResponse.success(query.list());
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get report definition by code")
    public ApiResponse<ReportDefinitionResponse> get(@PathVariable String code) {
        return ApiResponse.success(query.getByCode(code));
    }
}
