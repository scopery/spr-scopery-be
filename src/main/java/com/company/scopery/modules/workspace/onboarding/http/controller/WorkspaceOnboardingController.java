package com.company.scopery.modules.workspace.onboarding.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.onboarding.application.action.AcceptInvitationOnboardingAction;
import com.company.scopery.modules.workspace.onboarding.application.action.ChooseOnboardingOptionAction;
import com.company.scopery.modules.workspace.onboarding.application.action.CreateWorkspaceOnboardingAction;
import com.company.scopery.modules.workspace.onboarding.application.action.ResetOnboardingChoiceAction;
import com.company.scopery.modules.workspace.onboarding.application.action.StartOnboardingAction;
import com.company.scopery.modules.workspace.onboarding.application.command.AcceptInvitationOnboardingCommand;
import com.company.scopery.modules.workspace.onboarding.application.command.ChooseOnboardingOptionCommand;
import com.company.scopery.modules.workspace.onboarding.application.command.CreateWorkspaceOnboardingCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.application.service.OnboardingQueryService;
import com.company.scopery.modules.workspace.onboarding.http.request.ChooseOnboardingOptionRequest;
import com.company.scopery.modules.workspace.onboarding.http.request.OnboardingAcceptInvitationRequest;
import com.company.scopery.modules.workspace.onboarding.http.request.OnboardingCreateWorkspaceRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(WorkspaceApiPaths.WORKSPACE_ONBOARDING)
@Tag(name = "Workspace - Onboarding")
public class WorkspaceOnboardingController {

    private final StartOnboardingAction startOnboardingAction;
    private final ChooseOnboardingOptionAction chooseOnboardingOptionAction;
    private final CreateWorkspaceOnboardingAction createWorkspaceOnboardingAction;
    private final AcceptInvitationOnboardingAction acceptInvitationOnboardingAction;
    private final ResetOnboardingChoiceAction resetOnboardingChoiceAction;
    private final OnboardingQueryService queryService;

    public WorkspaceOnboardingController(StartOnboardingAction startOnboardingAction,
                                          ChooseOnboardingOptionAction chooseOnboardingOptionAction,
                                          CreateWorkspaceOnboardingAction createWorkspaceOnboardingAction,
                                          AcceptInvitationOnboardingAction acceptInvitationOnboardingAction,
                                          ResetOnboardingChoiceAction resetOnboardingChoiceAction,
                                          OnboardingQueryService queryService) {
        this.startOnboardingAction = startOnboardingAction;
        this.chooseOnboardingOptionAction = chooseOnboardingOptionAction;
        this.createWorkspaceOnboardingAction = createWorkspaceOnboardingAction;
        this.acceptInvitationOnboardingAction = acceptInvitationOnboardingAction;
        this.resetOnboardingChoiceAction = resetOnboardingChoiceAction;
        this.queryService = queryService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get current onboarding status")
    public ApiResponse<WorkspaceOnboardingStatusResponse> getStatus() {
        return ApiResponse.success(queryService.getStatus());
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start workspace onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> start() {
        return ApiResponse.success(startOnboardingAction.execute());
    }

    @PostMapping("/choose-option")
    @Operation(summary = "Choose onboarding option")
    public ApiResponse<WorkspaceOnboardingStatusResponse> chooseOption(
            @Valid @RequestBody ChooseOnboardingOptionRequest request) {
        return ApiResponse.success(chooseOnboardingOptionAction.execute(
                new ChooseOnboardingOptionCommand(request.option())));
    }

    @PostMapping("/create-workspace")
    @Operation(summary = "Create workspace during onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> createWorkspace(
            @Valid @RequestBody OnboardingCreateWorkspaceRequest request) {
        return ApiResponse.success(createWorkspaceOnboardingAction.execute(
                new CreateWorkspaceOnboardingCommand(request.organizationName(), request.organizationCode(),
                        request.workspaceName(), request.workspaceCode(), request.workspaceDescription())));
    }

    @PostMapping("/accept-invitation")
    @Operation(summary = "Accept invitation during onboarding")
    public ApiResponse<WorkspaceOnboardingStatusResponse> acceptInvitation(
            @Valid @RequestBody OnboardingAcceptInvitationRequest request) {
        return ApiResponse.success(acceptInvitationOnboardingAction.execute(
                new AcceptInvitationOnboardingCommand(request.code())));
    }

    @PostMapping("/reset-choice")
    @Operation(summary = "Reset onboarding to workspace option selection")
    public ApiResponse<WorkspaceOnboardingStatusResponse> resetChoice() {
        return ApiResponse.success(resetOnboardingChoiceAction.execute());
    }
}
