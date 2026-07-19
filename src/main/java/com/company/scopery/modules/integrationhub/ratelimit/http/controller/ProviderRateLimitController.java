package com.company.scopery.modules.integrationhub.ratelimit.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.ratelimit.application.response.ProviderRateLimitStateResponse;
import com.company.scopery.modules.integrationhub.ratelimit.application.service.ProviderRateLimitQueryService;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Rate Limits")
public class ProviderRateLimitController {
    private final ProviderRateLimitQueryService query;
    public ProviderRateLimitController(ProviderRateLimitQueryService query) { this.query = query; }
    @GetMapping(IntegrationApiPaths.RATE_LIMITS) @Operation(summary = "List provider rate limit states")
    public ApiResponse<List<ProviderRateLimitStateResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
}
