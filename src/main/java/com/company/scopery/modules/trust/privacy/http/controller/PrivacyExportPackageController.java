package com.company.scopery.modules.trust.privacy.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.privacy.application.action.CreatePrivacyExportPackageAction;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyExportPackageResponse;
import com.company.scopery.modules.trust.privacy.application.service.PrivacyExportPackageQueryService;
import com.company.scopery.modules.trust.privacy.http.request.CreatePrivacyExportPackageRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class PrivacyExportPackageController {
    private final CreatePrivacyExportPackageAction createAction;
    private final PrivacyExportPackageQueryService queryService;
    public PrivacyExportPackageController(CreatePrivacyExportPackageAction createAction, PrivacyExportPackageQueryService queryService) {
        this.createAction = createAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.PRIVACY_EXPORT_PACKAGES) @Operation(summary = "Create privacy export package")
    public ApiResponse<PrivacyExportPackageResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreatePrivacyExportPackageRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.privacyRequestId(), r.packageManifestJson()));
    }
    @GetMapping(TrustApiPaths.PRIVACY_EXPORT_PACKAGES) @Operation(summary = "List privacy export packages")
    public ApiResponse<List<PrivacyExportPackageResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
}
