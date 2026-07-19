package com.company.scopery.modules.productivity.savedview.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.savedview.application.action.CreateSavedViewAction;
import com.company.scopery.modules.productivity.savedview.application.command.CreateSavedViewCommand;
import com.company.scopery.modules.productivity.savedview.application.response.SavedViewResponse;
import com.company.scopery.modules.productivity.savedview.application.service.SavedViewQueryService;
import com.company.scopery.modules.productivity.savedview.http.request.CreateSavedViewRequest;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.SAVED_VIEWS) @Tag(name = "Productivity - Saved Views")
public class SavedViewController {
    private final CreateSavedViewAction create; private final SavedViewQueryService query;
    public SavedViewController(CreateSavedViewAction create, SavedViewQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create saved view")
    public ApiResponse<SavedViewResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateSavedViewRequest r) {
        return ApiResponse.success(create.execute(new CreateSavedViewCommand(workspaceId, r.targetType(), r.name(), r.projectId(), r.viewConfigJson(), r.filtersJson())));
    }
    @GetMapping @Operation(summary="List saved views")
    public ApiResponse<List<SavedViewResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
