package com.company.scopery.modules.integrationhub.deadletter.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.deadletter.application.action.ResolveDeadLetterEventAction;
import com.company.scopery.modules.integrationhub.deadletter.application.action.RetryDeadLetterEventAction;
import com.company.scopery.modules.integrationhub.deadletter.application.response.DeadLetterEventResponse;
import com.company.scopery.modules.integrationhub.deadletter.application.service.DeadLetterQueryService;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Dead Letter Events")
public class DeadLetterEventController {
    private final DeadLetterQueryService query;
    private final RetryDeadLetterEventAction retry;
    private final ResolveDeadLetterEventAction resolve;
    public DeadLetterEventController(DeadLetterQueryService query, RetryDeadLetterEventAction retry, ResolveDeadLetterEventAction resolve) {
        this.query = query; this.retry = retry; this.resolve = resolve;
    }
    @GetMapping(IntegrationApiPaths.DEAD_LETTERS) @Operation(summary = "List dead letter events")
    public ApiResponse<List<DeadLetterEventResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @PostMapping(IntegrationApiPaths.DEAD_LETTER_RETRY) @Operation(summary = "Retry dead letter event")
    public ApiResponse<DeadLetterEventResponse> retry(@PathVariable UUID workspaceId, @PathVariable UUID deadLetterId) {
        return ApiResponse.success(retry.execute(workspaceId, deadLetterId));
    }
    @PostMapping(IntegrationApiPaths.DEAD_LETTER_RESOLVE) @Operation(summary = "Resolve dead letter event")
    public ApiResponse<DeadLetterEventResponse> resolve(@PathVariable UUID workspaceId, @PathVariable UUID deadLetterId) {
        return ApiResponse.success(resolve.execute(workspaceId, deadLetterId));
    }
}
