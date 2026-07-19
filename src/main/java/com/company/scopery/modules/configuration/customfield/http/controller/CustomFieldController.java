package com.company.scopery.modules.configuration.customfield.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.customfield.application.action.CreateCustomFieldAction;
import com.company.scopery.modules.configuration.customfield.application.command.CreateCustomFieldCommand;
import com.company.scopery.modules.configuration.customfield.application.response.CustomFieldDefinitionResponse;
import com.company.scopery.modules.configuration.customfield.application.service.CustomFieldQueryService;
import com.company.scopery.modules.configuration.customfield.http.request.CreateCustomFieldRequest;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.CUSTOM_FIELDS) @Tag(name = "Configuration - Custom Fields")
public class CustomFieldController {
    private final CreateCustomFieldAction create; private final CustomFieldQueryService query;
    public CustomFieldController(CreateCustomFieldAction create, CustomFieldQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create custom field")
    public ApiResponse<CustomFieldDefinitionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateCustomFieldRequest r) {
        return ApiResponse.success(create.execute(new CreateCustomFieldCommand(workspaceId, r.objectTypeCode(), r.fieldKey(), r.label(), r.fieldType(), r.required(), r.sensitive(), r.clientVisible())));
    }
    @GetMapping @Operation(summary="List custom fields")
    public ApiResponse<List<CustomFieldDefinitionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/{fieldId}") @Operation(summary="Get custom field")
    public ApiResponse<CustomFieldDefinitionResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID fieldId) { return ApiResponse.success(query.get(workspaceId, fieldId)); }
}
