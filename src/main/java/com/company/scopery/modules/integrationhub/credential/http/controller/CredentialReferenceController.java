package com.company.scopery.modules.integrationhub.credential.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.credential.application.action.CreateCredentialReferenceAction;
import com.company.scopery.modules.integrationhub.credential.application.action.RevokeCredentialAction;
import com.company.scopery.modules.integrationhub.credential.application.action.RotateCredentialAction;
import com.company.scopery.modules.integrationhub.credential.application.response.CredentialReferenceResponse;
import com.company.scopery.modules.integrationhub.credential.application.service.CredentialReferenceQueryService;
import com.company.scopery.modules.integrationhub.credential.http.request.CreateCredentialReferenceRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Credentials")
public class CredentialReferenceController {
    private final CredentialReferenceQueryService credentialQuery;
    private final CreateCredentialReferenceAction createCredential;
    private final RotateCredentialAction rotateCredential;
    private final RevokeCredentialAction revokeCredential;
    public CredentialReferenceController(CredentialReferenceQueryService credentialQuery,
            CreateCredentialReferenceAction createCredential, RotateCredentialAction rotateCredential,
            RevokeCredentialAction revokeCredential) {
        this.credentialQuery = credentialQuery; this.createCredential = createCredential;
        this.rotateCredential = rotateCredential; this.revokeCredential = revokeCredential;
    }
    @PostMapping(IntegrationApiPaths.CREDENTIALS) @Operation(summary = "Create credential reference (no raw secret returned)")
    public ApiResponse<CredentialReferenceResponse> create(@PathVariable UUID workspaceId,
            @Valid @RequestBody CreateCredentialReferenceRequest r) {
        return ApiResponse.success(createCredential.execute(workspaceId, r.providerCode(), r.credentialType(), r.secretReference()));
    }
    @GetMapping(IntegrationApiPaths.CREDENTIALS) @Operation(summary = "List credential references")
    public ApiResponse<List<CredentialReferenceResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(credentialQuery.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.CREDENTIAL_BY_ID) @Operation(summary = "Get credential reference by id")
    public ApiResponse<CredentialReferenceResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID credentialId) {
        return ApiResponse.success(credentialQuery.getById(workspaceId, credentialId));
    }
    @PostMapping(IntegrationApiPaths.CREDENTIAL_ROTATE) @Operation(summary = "Rotate credential")
    public ApiResponse<CredentialReferenceResponse> rotate(@PathVariable UUID workspaceId, @PathVariable UUID credentialId) {
        return ApiResponse.success(rotateCredential.execute(workspaceId, credentialId, "secret-ref://" + UUID.randomUUID()));
    }
    @PostMapping(IntegrationApiPaths.CREDENTIAL_REVOKE) @Operation(summary = "Revoke credential")
    public ApiResponse<CredentialReferenceResponse> revoke(@PathVariable UUID workspaceId, @PathVariable UUID credentialId) {
        return ApiResponse.success(revokeCredential.execute(workspaceId, credentialId));
    }
}
