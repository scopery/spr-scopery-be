package com.company.scopery.modules.aiagent.providersecret.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.providersecret.application.action.DeactivateProviderSecretAction;
import com.company.scopery.modules.aiagent.providersecret.application.action.RotateProviderSecretAction;
import com.company.scopery.modules.aiagent.providersecret.application.action.SetProviderSecretAction;
import com.company.scopery.modules.aiagent.providersecret.application.command.DeactivateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.RotateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.SetProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.query.GetProviderSecretDetailQuery;
import com.company.scopery.modules.aiagent.providersecret.application.query.SearchProviderSecretQuery;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretDetailResponse;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.application.service.ProviderSecretQueryService;
import com.company.scopery.modules.aiagent.providersecret.http.request.RotateProviderSecretRequest;
import com.company.scopery.modules.aiagent.providersecret.http.request.SearchProviderSecretRequest;
import com.company.scopery.modules.aiagent.providersecret.http.request.SetProviderSecretRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Provider Secrets")
@RestController
@RequestMapping(AiAgentApiPaths.PROVIDER_SECRETS)
public class ProviderSecretController {

    private final SetProviderSecretAction setAction;
    private final RotateProviderSecretAction rotateAction;
    private final DeactivateProviderSecretAction deactivateAction;
    private final ProviderSecretQueryService queryService;

    public ProviderSecretController(SetProviderSecretAction setAction,
                                     RotateProviderSecretAction rotateAction,
                                     DeactivateProviderSecretAction deactivateAction,
                                     ProviderSecretQueryService queryService) {
        this.setAction = setAction;
        this.rotateAction = rotateAction;
        this.deactivateAction = deactivateAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Set provider API key (encrypts and stores; deactivates previous active secret of same type)")
    @PostMapping
    public ResponseEntity<ApiResponse<ProviderSecretResponse>> setProviderSecret(
            @Valid @RequestBody SetProviderSecretRequest request) {
        SetProviderSecretCommand command = new SetProviderSecretCommand(
                request.providerId(), request.secretType(), request.secretValue(), request.description());
        return ResponseEntity.ok(ApiResponse.success(setAction.execute(command)));
    }

    @Operation(summary = "Rotate provider secret (deactivates old, creates new encrypted record)")
    @PutMapping("/{id}/rotate")
    public ResponseEntity<ApiResponse<ProviderSecretResponse>> rotateProviderSecret(
            @PathVariable UUID id,
            @Valid @RequestBody RotateProviderSecretRequest request) {
        RotateProviderSecretCommand command = new RotateProviderSecretCommand(
                id, request.secretValue(), request.description());
        return ResponseEntity.ok(ApiResponse.success(rotateAction.execute(command)));
    }

    @Operation(summary = "Deactivate a provider secret")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ProviderSecretResponse>> deactivateProviderSecret(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                deactivateAction.execute(new DeactivateProviderSecretCommand(id))));
    }

    @Operation(summary = "Get provider secret detail (masked value only — no raw key returned)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderSecretDetailResponse>> getProviderSecretDetail(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                queryService.getProviderSecretDetail(new GetProviderSecretDetailQuery(id))));
    }

    @Operation(summary = "Search provider secrets")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProviderSecretResponse>>> searchProviderSecrets(
            @Valid SearchProviderSecretRequest request) {
        SearchProviderSecretQuery query = new SearchProviderSecretQuery(
                request.providerId(), request.secretType(), request.status(),
                request.page(), request.size());
        PageResult<ProviderSecretResponse> result = queryService.searchProviderSecrets(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }
}
