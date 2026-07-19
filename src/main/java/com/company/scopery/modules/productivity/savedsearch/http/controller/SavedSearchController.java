package com.company.scopery.modules.productivity.savedsearch.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.savedsearch.application.action.CreateSavedSearchAction;
import com.company.scopery.modules.productivity.savedsearch.application.command.CreateSavedSearchCommand;
import com.company.scopery.modules.productivity.savedsearch.application.response.SavedSearchResponse;
import com.company.scopery.modules.productivity.savedsearch.application.service.SavedSearchQueryService;
import com.company.scopery.modules.productivity.savedsearch.http.request.CreateSavedSearchRequest;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.SAVED_SEARCHES) @Tag(name = "Productivity - Saved Searches")
public class SavedSearchController {
    private final CreateSavedSearchAction create; private final SavedSearchQueryService query;
    public SavedSearchController(CreateSavedSearchAction create, SavedSearchQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create saved search")
    public ApiResponse<SavedSearchResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateSavedSearchRequest r) {
        return ApiResponse.success(create.execute(new CreateSavedSearchCommand(workspaceId, r.name(), r.scope(), r.queryText(), r.filtersJson(), r.projectId())));
    }
    @GetMapping @Operation(summary="List saved searches")
    public ApiResponse<List<SavedSearchResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
