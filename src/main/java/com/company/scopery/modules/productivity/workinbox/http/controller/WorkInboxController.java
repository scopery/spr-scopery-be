package com.company.scopery.modules.productivity.workinbox.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import com.company.scopery.modules.productivity.workinbox.application.action.MarkInboxReadAction;
import com.company.scopery.modules.productivity.workinbox.application.command.MarkInboxReadCommand;
import com.company.scopery.modules.productivity.workinbox.application.response.WorkInboxItemResponse;
import com.company.scopery.modules.productivity.workinbox.application.service.WorkInboxQueryService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.WORK_INBOX) @Tag(name = "Productivity - Work Inbox")
public class WorkInboxController {
    private final WorkInboxQueryService query; private final MarkInboxReadAction markRead;
    public WorkInboxController(WorkInboxQueryService query, MarkInboxReadAction markRead) { this.query=query; this.markRead=markRead; }
    @GetMapping @Operation(summary="List work inbox")
    public ApiResponse<List<WorkInboxItemResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/counts") @Operation(summary="Inbox counts")
    public ApiResponse<Map<String, Long>> counts(@PathVariable UUID workspaceId) { return ApiResponse.success(query.counts(workspaceId)); }
    @PostMapping("/{itemId}/mark-read") @Operation(summary="Mark inbox item read")
    public ApiResponse<WorkInboxItemResponse> markRead(@PathVariable UUID workspaceId, @PathVariable UUID itemId) { return ApiResponse.success(markRead.execute(new MarkInboxReadCommand(workspaceId, itemId))); }
}
