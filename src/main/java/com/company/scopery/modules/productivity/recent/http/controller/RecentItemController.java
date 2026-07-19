package com.company.scopery.modules.productivity.recent.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.recent.application.action.RecordRecentItemAction;
import com.company.scopery.modules.productivity.recent.application.command.RecordRecentItemCommand;
import com.company.scopery.modules.productivity.recent.application.response.RecentItemResponse;
import com.company.scopery.modules.productivity.recent.application.service.RecentItemQueryService;
import com.company.scopery.modules.productivity.recent.http.request.RecordRecentRequest;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.RECENT) @Tag(name = "Productivity - Recent")
public class RecentItemController {
    private final RecordRecentItemAction record; private final RecentItemQueryService query;
    public RecentItemController(RecordRecentItemAction record, RecentItemQueryService query) { this.record=record; this.query=query; }
    @PostMapping @Operation(summary="Record recent item")
    public ApiResponse<RecentItemResponse> record(@PathVariable UUID workspaceId, @Valid @RequestBody RecordRecentRequest r) {
        return ApiResponse.success(record.execute(new RecordRecentItemCommand(workspaceId, r.targetType(), r.targetId(), r.titleSnapshot())));
    }
    @GetMapping @Operation(summary="List recent items")
    public ApiResponse<List<RecentItemResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
