package com.company.scopery.modules.productivity.favorite.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.favorite.application.action.*;
import com.company.scopery.modules.productivity.favorite.application.command.*;
import com.company.scopery.modules.productivity.favorite.application.response.FavoriteItemResponse;
import com.company.scopery.modules.productivity.favorite.application.service.FavoriteQueryService;
import com.company.scopery.modules.productivity.favorite.http.request.CreateFavoriteRequest;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.FAVORITES) @Tag(name = "Productivity - Favorites")
public class FavoriteController {
    private final CreateFavoriteAction create; private final RemoveFavoriteAction remove; private final FavoriteQueryService query;
    public FavoriteController(CreateFavoriteAction create, RemoveFavoriteAction remove, FavoriteQueryService query) { this.create=create; this.remove=remove; this.query=query; }
    @PostMapping @Operation(summary="Add favorite")
    public ApiResponse<FavoriteItemResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateFavoriteRequest r) {
        return ApiResponse.success(create.execute(new CreateFavoriteCommand(workspaceId, r.targetType(), r.targetId(), r.labelOverride())));
    }
    @GetMapping @Operation(summary="List favorites")
    public ApiResponse<List<FavoriteItemResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @DeleteMapping("/{favoriteId}") @Operation(summary="Remove favorite")
    public ApiResponse<Void> remove(@PathVariable UUID workspaceId, @PathVariable UUID favoriteId) { remove.execute(new RemoveFavoriteCommand(workspaceId, favoriteId)); return ApiResponse.success(null); }
}
