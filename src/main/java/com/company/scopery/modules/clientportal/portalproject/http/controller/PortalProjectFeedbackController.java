package com.company.scopery.modules.clientportal.portalproject.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.feedback.application.action.CreateClientFeedbackAction;
import com.company.scopery.modules.clientportal.feedback.application.command.CreateClientFeedbackCommand;
import com.company.scopery.modules.clientportal.feedback.application.response.ClientFeedbackResponse;
import com.company.scopery.modules.clientportal.feedback.application.service.ClientFeedbackQueryService;
import com.company.scopery.modules.clientportal.feedback.http.request.CreateClientFeedbackRequest;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_PROJECTS + "/{projectId}/feedback")
@Tag(name = "Client Portal - Project Feedback")
public class PortalProjectFeedbackController {
    private final CreateClientFeedbackAction create;
    private final ClientFeedbackQueryService query;
    public PortalProjectFeedbackController(CreateClientFeedbackAction create, ClientFeedbackQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Submit feedback as portal user")
    public ApiResponse<ClientFeedbackResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateClientFeedbackRequest r) {
        return ApiResponse.success(create.execute(new CreateClientFeedbackCommand(projectId, r.category(), r.title(), r.body(), true)));
    }
    @GetMapping @Operation(summary = "List feedback for granted project")
    public ApiResponse<List<ClientFeedbackResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listPortal(projectId));
    }
}
