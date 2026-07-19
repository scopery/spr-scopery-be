package com.company.scopery.modules.configuration.layout.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.layout.application.action.*;
import com.company.scopery.modules.configuration.layout.application.command.*;
import com.company.scopery.modules.configuration.layout.application.response.LayoutDefinitionResponse;
import com.company.scopery.modules.configuration.layout.application.service.LayoutQueryService;
import com.company.scopery.modules.configuration.layout.http.request.CreateLayoutRequest;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.LAYOUTS) @Tag(name = "Configuration - Layouts")
public class LayoutController {
    private final CreateLayoutAction create; private final PublishLayoutAction publish; private final LayoutQueryService query;
    public LayoutController(CreateLayoutAction create, PublishLayoutAction publish, LayoutQueryService query) { this.create=create; this.publish=publish; this.query=query; }
    @PostMapping @Operation(summary="Create layout")
    public ApiResponse<LayoutDefinitionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateLayoutRequest r) {
        return ApiResponse.success(create.execute(new CreateLayoutCommand(workspaceId, r.objectTypeCode(), r.layoutType(), r.name(), r.layoutJson())));
    }
    @GetMapping @Operation(summary="List layouts")
    public ApiResponse<List<LayoutDefinitionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @PostMapping("/{layoutId}/publish") @Operation(summary="Publish layout")
    public ApiResponse<LayoutDefinitionResponse> publish(@PathVariable UUID workspaceId, @PathVariable UUID layoutId) {
        return ApiResponse.success(publish.execute(new PublishLayoutCommand(workspaceId, layoutId)));
    }
}
