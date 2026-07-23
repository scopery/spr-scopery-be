package com.company.scopery.modules.traceability.functionscreen.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.functionscreen.application.action.LinkFunctionScreenAction;
import com.company.scopery.modules.traceability.functionscreen.application.action.UnlinkFunctionScreenAction;
import com.company.scopery.modules.traceability.functionscreen.application.command.LinkFunctionScreenCommand;
import com.company.scopery.modules.traceability.functionscreen.application.response.FunctionScreenResponse;
import com.company.scopery.modules.traceability.functionscreen.application.service.FunctionScreenQueryService;
import com.company.scopery.modules.traceability.functionscreen.http.request.LinkFunctionScreenRequest;
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
@RequestMapping(TraceabilityApiPaths.FUNCTION_SCREENS)
@Tag(name = "Traceability - Function Screens")
public class FunctionScreenController {

    private final LinkFunctionScreenAction link;
    private final UnlinkFunctionScreenAction unlink;
    private final FunctionScreenQueryService query;

    public FunctionScreenController(LinkFunctionScreenAction link,
                                    UnlinkFunctionScreenAction unlink,
                                    FunctionScreenQueryService query) {
        this.link = link;
        this.unlink = unlink;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Link a screen to a function")
    public ApiResponse<FunctionScreenResponse> link(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody LinkFunctionScreenRequest r) {
        return ApiResponse.success(link.execute(
                new LinkFunctionScreenCommand(projectId, functionalItemId, r.screenId(), r.note())));
    }

    @GetMapping
    @Operation(summary = "List screens linked to a function")
    public ApiResponse<List<FunctionScreenResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId) {
        return ApiResponse.success(query.listByFunction(functionalItemId));
    }

    @DeleteMapping("/{screenId}")
    @Operation(summary = "Unlink a screen from a function")
    public ApiResponse<Void> unlink(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID screenId) {
        unlink.execute(projectId, functionalItemId, screenId);
        return ApiResponse.success(null);
    }
}
