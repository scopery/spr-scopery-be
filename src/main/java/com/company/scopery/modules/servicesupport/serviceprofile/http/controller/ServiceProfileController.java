package com.company.scopery.modules.servicesupport.serviceprofile.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.serviceprofile.application.action.CreateServiceProfileAction;
import com.company.scopery.modules.servicesupport.serviceprofile.application.command.CreateServiceProfileCommand;
import com.company.scopery.modules.servicesupport.serviceprofile.application.response.ServiceProfileResponse;
import com.company.scopery.modules.servicesupport.serviceprofile.application.service.ServiceProfileQueryService;
import com.company.scopery.modules.servicesupport.serviceprofile.http.request.CreateServiceProfileRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Service Support - Service Profiles")
public class ServiceProfileController {
    private final ServiceProfileQueryService query; private final CreateServiceProfileAction create;
    public ServiceProfileController(ServiceProfileQueryService query, CreateServiceProfileAction create){ this.query=query; this.create=create; }
    @GetMapping(SupportApiPaths.SERVICE_PROFILES) @Operation(summary = "List service profiles")
    public ApiResponse<List<ServiceProfileResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @PostMapping(SupportApiPaths.SERVICE_PROFILES) @Operation(summary = "Create service profile")
    public ApiResponse<ServiceProfileResponse> create(@PathVariable UUID workspaceId, @RequestBody @Valid CreateServiceProfileRequest req) {
        return ApiResponse.success(create.execute(workspaceId, new CreateServiceProfileCommand(
            req.scopeType() == null ? "PROJECT" : req.scopeType(), req.projectId(), req.portalIntakeEnabled() != null && req.portalIntakeEnabled())));
    }
}
