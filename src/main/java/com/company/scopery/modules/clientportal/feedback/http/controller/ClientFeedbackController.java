package com.company.scopery.modules.clientportal.feedback.http.controller;
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
@RequestMapping(ClientPortalApiPaths.FEEDBACK)
@Tag(name = "Client Portal - Feedback")
public class ClientFeedbackController {
    private final CreateClientFeedbackAction create;
    private final ClientFeedbackQueryService query;
    public ClientFeedbackController(CreateClientFeedbackAction create, ClientFeedbackQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create client feedback (internal)")
    public ApiResponse<ClientFeedbackResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateClientFeedbackRequest r) {
        return ApiResponse.success(create.execute(new CreateClientFeedbackCommand(projectId, r.category(), r.title(), r.body(), false)));
    }
    @GetMapping @Operation(summary = "List client feedback (internal)")
    public ApiResponse<List<ClientFeedbackResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.listInternal(projectId)); }
}
