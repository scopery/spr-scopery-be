package com.company.scopery.modules.productivity.search.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.search.application.response.*;
import com.company.scopery.modules.productivity.search.application.service.GlobalSearchQueryService;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.SEARCH) @Tag(name = "Productivity - Search")
public class GlobalSearchController {
    private final GlobalSearchQueryService query;
    public GlobalSearchController(GlobalSearchQueryService query) { this.query = query; }
    @GetMapping @Operation(summary="Global search")
    public ApiResponse<SearchPageResponse> search(@RequestParam UUID workspaceId, @RequestParam(required=false) String q,
                                                  @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        return ApiResponse.success(query.search(workspaceId, q, page, size));
    }
    @GetMapping("/scopes") @Operation(summary="List search scopes")
    public ApiResponse<List<String>> scopes(@RequestParam UUID workspaceId) { return ApiResponse.success(query.scopes(workspaceId)); }
}
