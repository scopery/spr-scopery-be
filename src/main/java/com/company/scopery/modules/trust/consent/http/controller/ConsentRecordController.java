package com.company.scopery.modules.trust.consent.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.consent.application.action.CreateConsentRecordAction;
import com.company.scopery.modules.trust.consent.application.action.WithdrawConsentRecordAction;
import com.company.scopery.modules.trust.consent.application.response.ConsentRecordResponse;
import com.company.scopery.modules.trust.consent.application.service.ConsentQueryService;
import com.company.scopery.modules.trust.consent.http.request.CreateConsentRecordRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class ConsentRecordController {
    private final CreateConsentRecordAction createAction;
    private final WithdrawConsentRecordAction withdrawAction;
    private final ConsentQueryService queryService;
    public ConsentRecordController(CreateConsentRecordAction createAction, WithdrawConsentRecordAction withdrawAction, ConsentQueryService queryService) {
        this.createAction = createAction; this.withdrawAction = withdrawAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.CONSENT_RECORDS) @Operation(summary = "Create consent record")
    public ApiResponse<ConsentRecordResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateConsentRecordRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.consentType()));
    }
    @GetMapping(TrustApiPaths.CONSENT_RECORDS) @Operation(summary = "List consent records")
    public ApiResponse<List<ConsentRecordResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @PostMapping(TrustApiPaths.CONSENT_RECORDS + "/{consentId}/withdraw") @Operation(summary = "Withdraw consent record")
    public ApiResponse<ConsentRecordResponse> withdraw(@PathVariable UUID workspaceId, @PathVariable UUID consentId) {
        return ApiResponse.success(withdrawAction.execute(workspaceId, consentId));
    }
}
