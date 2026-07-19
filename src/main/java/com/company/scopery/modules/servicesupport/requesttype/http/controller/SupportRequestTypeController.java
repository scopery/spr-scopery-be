package com.company.scopery.modules.servicesupport.requesttype.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.requesttype.application.action.CreateSupportRequestTypeAction;
import com.company.scopery.modules.servicesupport.requesttype.application.action.DisableSupportRequestTypeAction;
import com.company.scopery.modules.servicesupport.requesttype.application.action.EnableSupportRequestTypeAction;
import com.company.scopery.modules.servicesupport.requesttype.application.command.CreateSupportRequestTypeCommand;
import com.company.scopery.modules.servicesupport.requesttype.application.response.SupportRequestTypeResponse;
import com.company.scopery.modules.servicesupport.requesttype.application.service.SupportRequestTypeQueryService;
import com.company.scopery.modules.servicesupport.requesttype.http.request.CreateSupportRequestTypeRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Service Support - Request Types")
public class SupportRequestTypeController {
    private final SupportRequestTypeQueryService query; private final CreateSupportRequestTypeAction create;
    private final DisableSupportRequestTypeAction disable; private final EnableSupportRequestTypeAction enable;
    public SupportRequestTypeController(SupportRequestTypeQueryService query, CreateSupportRequestTypeAction create,
            DisableSupportRequestTypeAction disable, EnableSupportRequestTypeAction enable){
        this.query=query; this.create=create; this.disable=disable; this.enable=enable;
    }
    @GetMapping(SupportApiPaths.REQUEST_TYPES) @Operation(summary = "List support request types")
    public ApiResponse<List<SupportRequestTypeResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @PostMapping(SupportApiPaths.REQUEST_TYPES) @Operation(summary = "Create support request type")
    public ApiResponse<SupportRequestTypeResponse> create(@PathVariable UUID workspaceId, @RequestBody @Valid CreateSupportRequestTypeRequest req) {
        return ApiResponse.success(create.execute(workspaceId, new CreateSupportRequestTypeCommand(
            req.typeCode(), req.name(), req.defaultPriority(), req.portalVisible() != null && req.portalVisible())));
    }
    @PostMapping(SupportApiPaths.REQUEST_TYPE_DISABLE) @Operation(summary = "Disable request type")
    public ApiResponse<SupportRequestTypeResponse> disable(@PathVariable UUID workspaceId, @PathVariable UUID requestTypeId) {
        return ApiResponse.success(disable.execute(workspaceId, requestTypeId));
    }
    @PostMapping(SupportApiPaths.REQUEST_TYPE_ENABLE) @Operation(summary = "Enable request type")
    public ApiResponse<SupportRequestTypeResponse> enable(@PathVariable UUID workspaceId, @PathVariable UUID requestTypeId) {
        return ApiResponse.success(enable.execute(workspaceId, requestTypeId));
    }
}
