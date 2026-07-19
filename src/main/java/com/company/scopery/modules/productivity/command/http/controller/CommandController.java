package com.company.scopery.modules.productivity.command.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.command.application.response.CommandDefinitionResponse;
import com.company.scopery.modules.productivity.command.application.service.CommandQueryService;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.COMMANDS) @Tag(name = "Productivity - Commands")
public class CommandController {
    private final CommandQueryService query;
    public CommandController(CommandQueryService query) { this.query = query; }
    @GetMapping @Operation(summary="List command palette commands")
    public ApiResponse<List<CommandDefinitionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/suggestions") @Operation(summary="Suggest commands")
    public ApiResponse<List<CommandDefinitionResponse>> suggestions(@PathVariable UUID workspaceId, @RequestParam(required=false) String q) {
        return ApiResponse.success(query.suggestions(workspaceId, q));
    }
}
