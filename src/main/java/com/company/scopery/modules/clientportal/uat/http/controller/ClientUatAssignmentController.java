package com.company.scopery.modules.clientportal.uat.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import com.company.scopery.modules.clientportal.uat.application.action.CreateClientUatAssignmentAction;
import com.company.scopery.modules.clientportal.uat.application.command.CreateClientUatAssignmentCommand;
import com.company.scopery.modules.clientportal.uat.application.response.ClientUatAssignmentResponse;
import com.company.scopery.modules.clientportal.uat.application.service.ClientUatAssignmentQueryService;
import com.company.scopery.modules.clientportal.uat.http.request.CreateClientUatAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.UAT_ASSIGNMENTS)
@Tag(name = "Client Portal - UAT Assignments")
public class ClientUatAssignmentController {
    private final CreateClientUatAssignmentAction create;
    private final ClientUatAssignmentQueryService query;
    public ClientUatAssignmentController(CreateClientUatAssignmentAction create, ClientUatAssignmentQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create UAT assignment")
    public ApiResponse<ClientUatAssignmentResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateClientUatAssignmentRequest r) {
        return ApiResponse.success(create.execute(new CreateClientUatAssignmentCommand(projectId, r.testCaseId(), r.testRunId(), r.portalAccountId(), r.notes())));
    }
    @GetMapping @Operation(summary = "List UAT assignments")
    public ApiResponse<List<ClientUatAssignmentResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
}
