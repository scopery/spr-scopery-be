package com.company.scopery.modules.configuration.statusset.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import com.company.scopery.modules.configuration.statusset.application.action.*;
import com.company.scopery.modules.configuration.statusset.application.command.*;
import com.company.scopery.modules.configuration.statusset.application.response.*;
import com.company.scopery.modules.configuration.statusset.application.service.StatusSetQueryService;
import com.company.scopery.modules.configuration.statusset.http.request.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.STATUS_SETS) @Tag(name = "Configuration - Status Sets")
public class StatusSetController {
    private final CreateStatusSetAction create; private final CreateStatusValueAction createValue; private final StatusSetQueryService query;
    public StatusSetController(CreateStatusSetAction create, CreateStatusValueAction createValue, StatusSetQueryService query) {
        this.create=create; this.createValue=createValue; this.query=query;
    }
    @PostMapping @Operation(summary="Create status set")
    public ApiResponse<StatusSetResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateStatusSetRequest r) {
        return ApiResponse.success(create.execute(new CreateStatusSetCommand(workspaceId, r.objectTypeCode(), r.setCode(), r.name())));
    }
    @GetMapping @Operation(summary="List status sets")
    public ApiResponse<List<StatusSetResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @PostMapping("/{setId}/values") @Operation(summary="Add status value")
    public ApiResponse<StatusValueResponse> addValue(@PathVariable UUID workspaceId, @PathVariable UUID setId, @Valid @RequestBody CreateStatusValueRequest r) {
        return ApiResponse.success(createValue.execute(new CreateStatusValueCommand(workspaceId, setId, r.valueCode(), r.label(), r.domainCategory(), r.sortOrder())));
    }
    @GetMapping("/{setId}/values") @Operation(summary="List status values")
    public ApiResponse<List<StatusValueResponse>> listValues(@PathVariable UUID workspaceId, @PathVariable UUID setId) {
        return ApiResponse.success(query.listValues(workspaceId, setId));
    }
}
