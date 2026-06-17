package com.company.scopery.modules.workspace.onboarding.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.onboarding.api.request.ChooseOnboardingOptionRequest;
import com.company.scopery.modules.workspace.onboarding.api.request.OnboardingAcceptInvitationRequest;
import com.company.scopery.modules.workspace.onboarding.api.request.OnboardingJoinRequestRequest;
import com.company.scopery.modules.workspace.onboarding.application.WorkspaceOnboardingApplicationService;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.workspace.api.request.CreateWorkspaceRequest;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(WorkspaceApiPaths.WORKSPACE_ONBOARDING)
@Tag(name = "Workspace - Onboarding")
public class WorkspaceOnboardingController {

    private final WorkspaceOnboardingApplicationService onboardingService;

    public WorkspaceOnboardingController(WorkspaceOnboardingApplicationService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get current onboarding status")
    public ApiResponse<WorkspaceOnboardingStatusResponse> getStatus() {
        return ApiResponse.success(onboardingService.getStatus());
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start workspace onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> start() {
        return ApiResponse.success(onboardingService.start());
    }

    @PostMapping("/choose-option")
    @Operation(summary = "Choose onboarding option")
    public ApiResponse<WorkspaceOnboardingStatusResponse> chooseOption(
            @Valid @RequestBody ChooseOnboardingOptionRequest request) {
        return ApiResponse.success(onboardingService.chooseOption(request.option()));
    }

    @PostMapping("/create-workspace")
    @Operation(summary = "Create workspace during onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request) {
        var command = new CreateWorkspaceCommand(
                request.organizationId(), request.name(), request.code(),
                request.description(), request.defaultVisibility(), request.joinPolicy());
        return ApiResponse.success(onboardingService.createWorkspace(command));
    }

    @PostMapping("/accept-invitation")
    @Operation(summary = "Accept invitation during onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> acceptInvitation(
            @Valid @RequestBody OnboardingAcceptInvitationRequest request) {
        return ApiResponse.success(onboardingService.acceptInvitation(request.code()));
    }

    @PostMapping("/join-request")
    @Operation(summary = "Submit join request during onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> joinRequest(
            @RequestBody OnboardingJoinRequestRequest request) {
        return ApiResponse.success(onboardingService.joinRequest(
                request.workspaceId(), request.workspaceCode(), request.message()));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> cancel() {
        return ApiResponse.success(onboardingService.cancel());
    }
}
