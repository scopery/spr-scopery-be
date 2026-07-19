package com.company.scopery.modules.integrationhub.provider.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.provider.application.response.ConnectorCapabilityResponse;
import com.company.scopery.modules.integrationhub.provider.application.response.IntegrationProviderResponse;
import com.company.scopery.modules.integrationhub.provider.application.service.IntegrationProviderQueryService;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @Tag(name = "Integration Hub - Providers")
public class IntegrationProviderController {
    private final IntegrationProviderQueryService providerQuery;
    public IntegrationProviderController(IntegrationProviderQueryService providerQuery) {
        this.providerQuery = providerQuery;
    }
    @GetMapping(IntegrationApiPaths.PROVIDERS) @Operation(summary = "List integration providers")
    public ApiResponse<List<IntegrationProviderResponse>> list() {
        return ApiResponse.success(providerQuery.listAll());
    }
    @GetMapping(IntegrationApiPaths.PROVIDER_BY_CODE) @Operation(summary = "Get integration provider by code")
    public ApiResponse<IntegrationProviderResponse> getByCode(@PathVariable String providerCode) {
        return ApiResponse.success(providerQuery.getByCode(providerCode));
    }
    @GetMapping(IntegrationApiPaths.PROVIDER_CAPABILITIES) @Operation(summary = "List provider capabilities")
    public ApiResponse<List<ConnectorCapabilityResponse>> capabilities(@PathVariable String providerCode) {
        return ApiResponse.success(providerQuery.listCapabilities(providerCode));
    }
}
