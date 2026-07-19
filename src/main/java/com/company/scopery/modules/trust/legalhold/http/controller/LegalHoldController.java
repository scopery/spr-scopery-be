package com.company.scopery.modules.trust.legalhold.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.legalhold.application.action.CreateLegalHoldAction;
import com.company.scopery.modules.trust.legalhold.application.action.ReleaseLegalHoldAction;
import com.company.scopery.modules.trust.legalhold.application.response.LegalHoldResponse;
import com.company.scopery.modules.trust.legalhold.application.service.LegalHoldQueryService;
import com.company.scopery.modules.trust.legalhold.http.request.CreateLegalHoldRequest;
import com.company.scopery.modules.trust.legalhold.http.request.ReleaseLegalHoldRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class LegalHoldController {
    private final CreateLegalHoldAction createAction;
    private final ReleaseLegalHoldAction releaseAction;
    private final LegalHoldQueryService queryService;
    public LegalHoldController(CreateLegalHoldAction createAction, ReleaseLegalHoldAction releaseAction, LegalHoldQueryService queryService) {
        this.createAction = createAction; this.releaseAction = releaseAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.LEGAL_HOLDS) @Operation(summary = "Create legal hold")
    public ApiResponse<LegalHoldResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateLegalHoldRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r));
    }
    @GetMapping(TrustApiPaths.LEGAL_HOLDS) @Operation(summary = "List legal holds")
    public ApiResponse<List<LegalHoldResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @GetMapping(TrustApiPaths.LEGAL_HOLDS + "/{holdId}") @Operation(summary = "Get legal hold")
    public ApiResponse<LegalHoldResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID holdId) {
        return ApiResponse.success(queryService.getById(workspaceId, holdId));
    }
    @PostMapping(TrustApiPaths.LEGAL_HOLD_RELEASE) @Operation(summary = "Release legal hold")
    public ApiResponse<LegalHoldResponse> release(@PathVariable UUID workspaceId, @PathVariable UUID holdId,
            @Valid @RequestBody ReleaseLegalHoldRequest r) {
        return ApiResponse.success(releaseAction.execute(workspaceId, holdId, r.releaseReason()));
    }
}
