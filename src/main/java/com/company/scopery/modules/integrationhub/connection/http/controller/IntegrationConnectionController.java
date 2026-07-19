package com.company.scopery.modules.integrationhub.connection.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.connection.application.action.*;
import com.company.scopery.modules.integrationhub.connection.application.response.ConnectionHealthCheckResponse;
import com.company.scopery.modules.integrationhub.connection.application.response.IntegrationConnectionResponse;
import com.company.scopery.modules.integrationhub.connection.application.response.TestConnectionResult;
import com.company.scopery.modules.integrationhub.connection.application.service.ConnectionTestService;
import com.company.scopery.modules.integrationhub.connection.application.service.IntegrationConnectionQueryService;
import com.company.scopery.modules.integrationhub.connection.http.request.CreateIntegrationConnectionRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import com.company.scopery.modules.integrationhub.sync.application.action.RunProviderSyncAction;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Connections")
public class IntegrationConnectionController {
    private final IntegrationConnectionQueryService connectionQuery;
    private final CreateIntegrationConnectionAction createConnection;
    private final EnableIntegrationConnectionAction enableConnection;
    private final DisableIntegrationConnectionAction disableConnection;
    private final ArchiveIntegrationConnectionAction archiveConnection;
    private final RunConnectionHealthCheckAction healthCheck;
    private final ConnectionTestService connectionTest;
    private final RunProviderSyncAction runProviderSync;
    public IntegrationConnectionController(IntegrationConnectionQueryService connectionQuery,
            CreateIntegrationConnectionAction createConnection, EnableIntegrationConnectionAction enableConnection,
            DisableIntegrationConnectionAction disableConnection, ArchiveIntegrationConnectionAction archiveConnection,
            RunConnectionHealthCheckAction healthCheck, ConnectionTestService connectionTest,
            RunProviderSyncAction runProviderSync) {
        this.connectionQuery = connectionQuery; this.createConnection = createConnection;
        this.enableConnection = enableConnection; this.disableConnection = disableConnection;
        this.archiveConnection = archiveConnection; this.healthCheck = healthCheck;
        this.connectionTest = connectionTest; this.runProviderSync = runProviderSync;
    }
    @PostMapping(IntegrationApiPaths.CONNECTIONS) @Operation(summary = "Create integration connection")
    public ApiResponse<IntegrationConnectionResponse> create(@PathVariable UUID workspaceId,
            @Valid @RequestBody CreateIntegrationConnectionRequest r) {
        return ApiResponse.success(createConnection.execute(workspaceId, r.providerCode(), r.name(), r.credentialReferenceId()));
    }
    @GetMapping(IntegrationApiPaths.CONNECTIONS) @Operation(summary = "List connections")
    public ApiResponse<List<IntegrationConnectionResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(connectionQuery.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.CONNECTION_BY_ID) @Operation(summary = "Get connection by id")
    public ApiResponse<IntegrationConnectionResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(connectionQuery.getById(workspaceId, connectionId));
    }
    @PostMapping(IntegrationApiPaths.CONNECTION_ENABLE) @Operation(summary = "Enable connection")
    public ApiResponse<IntegrationConnectionResponse> enable(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(enableConnection.execute(workspaceId, connectionId));
    }
    @PostMapping(IntegrationApiPaths.CONNECTION_DISABLE) @Operation(summary = "Disable connection")
    public ApiResponse<IntegrationConnectionResponse> disable(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(disableConnection.execute(workspaceId, connectionId));
    }
    @PatchMapping(IntegrationApiPaths.CONNECTION_ARCHIVE) @Operation(summary = "Archive connection")
    public ApiResponse<IntegrationConnectionResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(archiveConnection.execute(workspaceId, connectionId));
    }
    @PostMapping(IntegrationApiPaths.CONNECTION_HEALTH) @Operation(summary = "Run connection health check")
    public ApiResponse<ConnectionHealthCheckResponse> healthCheck(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(healthCheck.execute(workspaceId, connectionId));
    }
    @GetMapping(IntegrationApiPaths.CONNECTION_HEALTH_LIST) @Operation(summary = "List connection health checks")
    public ApiResponse<List<ConnectionHealthCheckResponse>> healthChecks(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(connectionQuery.listHealthChecks(workspaceId, connectionId));
    }
    @PostMapping(IntegrationApiPaths.CONNECTION_TEST) @Operation(summary = "Test connection (config only)")
    public ApiResponse<TestConnectionResult> testConnection(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(connectionTest.testConnection(workspaceId, connectionId));
    }
    @PostMapping(IntegrationApiPaths.CONNECTION_SYNC) @Operation(summary = "Provider sync pull stub")
    public ApiResponse<Map<String, Object>> syncPull(@PathVariable UUID workspaceId, @PathVariable UUID connectionId) {
        return ApiResponse.success(runProviderSync.execute(workspaceId, connectionId));
    }
}
