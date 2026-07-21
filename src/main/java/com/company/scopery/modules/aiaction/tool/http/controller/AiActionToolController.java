package com.company.scopery.modules.aiaction.tool.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionToolResponse;
import com.company.scopery.modules.aiaction.application.service.AiActionToolQueryService;
import com.company.scopery.modules.aiaction.shared.constant.AiActionApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AiActionApiPaths.TOOLS)
@Tag(name = "AI Action - Tools")
public class AiActionToolController {

    private final AiActionToolQueryService toolQueryService;

    public AiActionToolController(AiActionToolQueryService toolQueryService) {
        this.toolQueryService = toolQueryService;
    }

    @GetMapping
    @Operation(summary = "List all active AI action tool policies")
    public ResponseEntity<ApiResponse<List<AiActionToolResponse>>> listActiveToolPolicies() {
        List<AiActionToolResponse> response = toolQueryService.listActiveToolPolicies();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{toolCode}")
    @Operation(summary = "Get an AI action tool policy by tool code")
    public ResponseEntity<ApiResponse<AiActionToolResponse>> getToolPolicy(
            @PathVariable String toolCode) {

        AiActionToolResponse response = toolQueryService.getToolPolicy(toolCode, "1");
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
