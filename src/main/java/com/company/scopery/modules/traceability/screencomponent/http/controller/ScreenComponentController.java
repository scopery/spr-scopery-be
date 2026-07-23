package com.company.scopery.modules.traceability.screencomponent.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screencomponent.application.action.LinkScreenComponentAction;
import com.company.scopery.modules.traceability.screencomponent.application.action.UnlinkScreenComponentAction;
import com.company.scopery.modules.traceability.screencomponent.application.command.LinkScreenComponentCommand;
import com.company.scopery.modules.traceability.screencomponent.application.response.ScreenComponentResponse;
import com.company.scopery.modules.traceability.screencomponent.application.service.ScreenComponentQueryService;
import com.company.scopery.modules.traceability.screencomponent.http.request.LinkScreenComponentRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_COMPONENTS)
@Tag(name = "Traceability - Screen Components")
public class ScreenComponentController {

    private final LinkScreenComponentAction link;
    private final UnlinkScreenComponentAction unlink;
    private final ScreenComponentQueryService query;

    public ScreenComponentController(LinkScreenComponentAction link,
                                     UnlinkScreenComponentAction unlink,
                                     ScreenComponentQueryService query) {
        this.link = link;
        this.unlink = unlink;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Link a component to a screen")
    public ApiResponse<ScreenComponentResponse> link(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId,
            @Valid @RequestBody LinkScreenComponentRequest r) {
        return ApiResponse.success(link.execute(new LinkScreenComponentCommand(
                workspaceId, screenId, r.componentId(), r.sectionId(), r.displayOrder(), r.note())));
    }

    @GetMapping
    @Operation(summary = "List components linked to a screen")
    public ApiResponse<List<ScreenComponentResponse>> list(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId) {
        return ApiResponse.success(query.listByScreen(screenId));
    }

    @DeleteMapping("/{componentId}")
    @Operation(summary = "Unlink a component from a screen")
    public ApiResponse<Void> unlink(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId,
            @PathVariable UUID componentId) {
        unlink.execute(workspaceId, screenId, componentId);
        return ApiResponse.success(null);
    }
}
