package com.company.scopery.modules.traceability.screen.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screen.application.response.ScreenFullSpecResponse;
import com.company.scopery.modules.traceability.screen.application.service.RegistryScreenQueryService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_FULL_SPEC)
@Tag(name = "Traceability - Screen Full Spec")
public class ScreenFullSpecController {

    private final RegistryScreenQueryService query;

    public ScreenFullSpecController(RegistryScreenQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "Get full spec for a screen — includes modes, sections, fields (with mode configs and validations), screen actions, process items, and event items")
    public ApiResponse<ScreenFullSpecResponse> getFullSpec(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId) {
        return ApiResponse.success(query.getFullSpec(workspaceId, screenId));
    }
}
