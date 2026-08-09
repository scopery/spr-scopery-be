package com.company.scopery.modules.elicitation.session.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.elicitation.session.application.action.CancelElicitationSessionAction;
import com.company.scopery.modules.elicitation.session.application.action.CloseElicitationSessionAction;
import com.company.scopery.modules.elicitation.session.application.action.StartElicitationSessionAction;
import com.company.scopery.modules.elicitation.session.application.command.CancelSessionCommand;
import com.company.scopery.modules.elicitation.session.application.command.CloseSessionCommand;
import com.company.scopery.modules.elicitation.session.application.command.StartSessionCommand;
import com.company.scopery.modules.elicitation.session.application.response.ElicitationSessionResponse;
import com.company.scopery.modules.elicitation.session.application.service.ElicitationSessionQueryService;
import com.company.scopery.modules.elicitation.session.http.request.StartSessionRequest;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Elicitation - Sessions")
public class ElicitationSessionController {

    private final StartElicitationSessionAction startAction;
    private final CloseElicitationSessionAction closeAction;
    private final CancelElicitationSessionAction cancelAction;
    private final ElicitationSessionQueryService queryService;

    public ElicitationSessionController(StartElicitationSessionAction startAction,
                                         CloseElicitationSessionAction closeAction,
                                         CancelElicitationSessionAction cancelAction,
                                         ElicitationSessionQueryService queryService) {
        this.startAction = startAction;
        this.closeAction = closeAction;
        this.cancelAction = cancelAction;
        this.queryService = queryService;
    }

    @PostMapping(ElicitationApiPaths.BASE_SESSIONS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start a new elicitation session")
    public ApiResponse<ElicitationSessionResponse> start(@PathVariable UUID projectId,
                                                          @Valid @RequestBody StartSessionRequest request) {
        return ApiResponse.success(startAction.execute(
                new StartSessionCommand(projectId, request.scopePackageId(), request.title())));
    }

    @GetMapping(ElicitationApiPaths.BASE_SESSIONS)
    @Operation(summary = "List elicitation sessions for a project")
    public ApiResponse<List<ElicitationSessionResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listByProject(projectId));
    }

    @GetMapping(ElicitationApiPaths.SESSION_BY_ID)
    @Operation(summary = "Get elicitation session by ID")
    public ApiResponse<ElicitationSessionResponse> getById(@PathVariable UUID projectId,
                                                            @PathVariable UUID sessionId) {
        return ApiResponse.success(queryService.getById(projectId, sessionId));
    }

    @PostMapping(ElicitationApiPaths.SESSION_CLOSE)
    @Operation(summary = "Close elicitation session and create a round")
    public ApiResponse<ElicitationSessionResponse> close(@PathVariable UUID projectId,
                                                          @PathVariable UUID sessionId) {
        return ApiResponse.success(closeAction.execute(new CloseSessionCommand(projectId, sessionId)));
    }

    @PostMapping(ElicitationApiPaths.SESSION_CANCEL)
    @Operation(summary = "Cancel elicitation session")
    public ApiResponse<ElicitationSessionResponse> cancel(@PathVariable UUID projectId,
                                                           @PathVariable UUID sessionId) {
        return ApiResponse.success(cancelAction.execute(new CancelSessionCommand(projectId, sessionId)));
    }
}
