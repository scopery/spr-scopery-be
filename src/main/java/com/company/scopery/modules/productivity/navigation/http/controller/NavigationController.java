package com.company.scopery.modules.productivity.navigation.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.productivity.navigation.application.action.UpsertNavigationPreferenceAction;
import com.company.scopery.modules.productivity.navigation.application.command.UpsertNavigationPreferenceCommand;
import com.company.scopery.modules.productivity.navigation.application.response.*;
import com.company.scopery.modules.productivity.navigation.application.service.NavigationQueryService;
import com.company.scopery.modules.productivity.navigation.http.request.UpdateNavigationPreferenceRequest;
import com.company.scopery.modules.productivity.shared.constant.ProductivityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProductivityApiPaths.NAVIGATION) @Tag(name = "Productivity - Navigation")
public class NavigationController {
    private final NavigationQueryService query; private final UpsertNavigationPreferenceAction upsert;
    public NavigationController(NavigationQueryService query, UpsertNavigationPreferenceAction upsert) { this.query=query; this.upsert=upsert; }
    @GetMapping @Operation(summary="List navigation menu")
    public ApiResponse<List<NavigationMenuResponse>> menu(@PathVariable UUID workspaceId) { return ApiResponse.success(query.menu(workspaceId)); }
    @GetMapping("/preferences") @Operation(summary="Get my navigation preferences")
    public ApiResponse<UserNavigationPreferenceResponse> prefs(@PathVariable UUID workspaceId) { return ApiResponse.success(query.myPreference(workspaceId)); }
    @PutMapping("/preferences") @Operation(summary="Update my navigation preferences")
    public ApiResponse<UserNavigationPreferenceResponse> updatePrefs(@PathVariable UUID workspaceId, @RequestBody UpdateNavigationPreferenceRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertNavigationPreferenceCommand(workspaceId, r.preferenceJson(), r.defaultLandingRoute())));
    }
}
