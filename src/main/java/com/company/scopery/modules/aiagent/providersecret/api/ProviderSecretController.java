package com.company.scopery.modules.aiagent.providersecret.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.providersecret.api.request.RotateProviderSecretRequest;
import com.company.scopery.modules.aiagent.providersecret.api.request.SearchProviderSecretRequest;
import com.company.scopery.modules.aiagent.providersecret.api.request.SetProviderSecretRequest;
import com.company.scopery.modules.aiagent.providersecret.application.ProviderSecretApplicationService;
import com.company.scopery.modules.aiagent.providersecret.application.command.DeactivateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.RotateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.SetProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.query.GetProviderSecretDetailQuery;
import com.company.scopery.modules.aiagent.providersecret.application.query.SearchProviderSecretQuery;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretDetailResponse;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
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

    private final ProviderSecretApplicationService providerSecretApplicationService;

    public ProviderSecretController(ProviderSecretApplicationService providerSecretApplicationService) {
        this.providerSecretApplicationService = providerSecretApplicationService;
    }

    @Operation(summary = "Set provider API key (encrypts and stores; deactivates previous active secret of same type)")
    @PostMapping
    public ResponseEntity<ApiResponse<ProviderSecretResponse>> setProviderSecret(
            @Valid @RequestBody SetProviderSecretRequest request) {
        SetProviderSecretCommand command = new SetProviderSecretCommand(
                request.providerId(),
                request.secretType(),
                request.secretValue(),
                request.description());
        return ResponseEntity.ok(ApiResponse.success(
                providerSecretApplicationService.setProviderSecret(command)));
    }

    @Operation(summary = "Rotate provider secret (deactivates old, creates new encrypted record)")
    @PutMapping("/{id}/rotate")
    public ResponseEntity<ApiResponse<ProviderSecretResponse>> rotateProviderSecret(
            @PathVariable UUID id,
            @Valid @RequestBody RotateProviderSecretRequest request) {
        RotateProviderSecretCommand command = new RotateProviderSecretCommand(
                id, request.secretValue(), request.description());
        return ResponseEntity.ok(ApiResponse.success(
                providerSecretApplicationService.rotateProviderSecret(command)));
    }

    @Operation(summary = "Deactivate a provider secret")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ProviderSecretResponse>> deactivateProviderSecret(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                providerSecretApplicationService.deactivateProviderSecret(
                        new DeactivateProviderSecretCommand(id))));
    }

    @Operation(summary = "Get provider secret detail (masked value only — no raw key returned)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderSecretDetailResponse>> getProviderSecretDetail(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                providerSecretApplicationService.getProviderSecretDetail(
                        new GetProviderSecretDetailQuery(id))));
    }

    @Operation(summary = "Search provider secrets")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProviderSecretResponse>>> searchProviderSecrets(
            @Valid SearchProviderSecretRequest request) {
        SearchProviderSecretQuery query = new SearchProviderSecretQuery(
                request.providerId(),
                request.secretType(),
                request.status(),
                request.page(),
                request.size());
        return ResponseEntity.ok(ApiResponse.success(
                providerSecretApplicationService.searchProviderSecrets(query)));
    }
}
