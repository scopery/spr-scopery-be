package com.company.scopery.modules.servicesupport.statushistory.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.statushistory.application.response.SupportStatusHistoryResponse;
import com.company.scopery.modules.servicesupport.statushistory.application.service.SupportStatusHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Status History")
public class SupportStatusHistoryController {
    private final SupportStatusHistoryQueryService query;

    public SupportStatusHistoryController(SupportStatusHistoryQueryService query) {
        this.query = query;
    }

    @GetMapping(SupportApiPaths.CASE_STATUS_HISTORY)
    @Operation(summary = "List status history for a case")
    public ApiResponse<List<SupportStatusHistoryResponse>> list(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId) {
        return ApiResponse.success(query.listByCase(workspaceId, caseId));
    }
}
