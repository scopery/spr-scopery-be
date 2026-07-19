package com.company.scopery.modules.productivity.pin.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.pin.application.action.CreatePinAction;
import com.company.scopery.modules.productivity.pin.application.command.CreatePinCommand;
import com.company.scopery.modules.productivity.pin.application.response.PinnedItemResponse;
import com.company.scopery.modules.productivity.pin.application.service.PinQueryService;
import com.company.scopery.modules.productivity.pin.http.request.CreatePinRequest;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.PINS) @Tag(name = "Productivity - Pins")
public class PinController {
    private final CreatePinAction create; private final PinQueryService query;
    public PinController(CreatePinAction create, PinQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create pin")
    public ApiResponse<PinnedItemResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreatePinRequest r) {
        return ApiResponse.success(create.execute(new CreatePinCommand(workspaceId, r.scope(), r.targetType(), r.targetId(), r.projectId(), r.sortOrder())));
    }
    @GetMapping @Operation(summary="List pins")
    public ApiResponse<List<PinnedItemResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
