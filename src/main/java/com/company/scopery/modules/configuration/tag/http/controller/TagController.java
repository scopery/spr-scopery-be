package com.company.scopery.modules.configuration.tag.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import com.company.scopery.modules.configuration.tag.application.action.CreateTagAction;
import com.company.scopery.modules.configuration.tag.application.command.CreateTagCommand;
import com.company.scopery.modules.configuration.tag.application.response.TagDefinitionResponse;
import com.company.scopery.modules.configuration.tag.application.service.TagQueryService;
import com.company.scopery.modules.configuration.tag.http.request.CreateTagRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.TAGS) @Tag(name = "Configuration - Tags")
public class TagController {
    private final CreateTagAction create; private final TagQueryService query;
    public TagController(CreateTagAction create, TagQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create tag")
    public ApiResponse<TagDefinitionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateTagRequest r) {
        return ApiResponse.success(create.execute(new CreateTagCommand(workspaceId, r.tagCode(), r.label(), r.color(), r.allowedObjectTypesJson())));
    }
    @GetMapping @Operation(summary="List tags")
    public ApiResponse<List<TagDefinitionResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/{tagId}") @Operation(summary="Get tag")
    public ApiResponse<TagDefinitionResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID tagId) { return ApiResponse.success(query.get(workspaceId, tagId)); }
}
