package com.company.scopery.modules.workspace.onboarding.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.onboarding.application.command.CreateWorkspaceOnboardingCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.organization.application.action.CreateOrganizationAction;
import com.company.scopery.modules.workspace.organization.application.command.CreateOrganizationCommand;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.workspace.application.action.CreateWorkspaceAction;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateWorkspaceOnboardingAction {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final WorkspaceUserContextRepository contextRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final CreateOrganizationAction createOrganizationAction;
    private final CreateWorkspaceAction createWorkspaceAction;
    private final WorkspaceActivityLogger activityLogger;

    public CreateWorkspaceOnboardingAction(WorkspaceOnboardingStateRepository onboardingRepository,
                                            WorkspaceUserContextRepository contextRepository,
                                            CurrentUserAuthorizationService currentUserService,
                                            CreateOrganizationAction createOrganizationAction,
                                            CreateWorkspaceAction createWorkspaceAction,
                                            WorkspaceActivityLogger activityLogger) {
        this.onboardingRepository = onboardingRepository;
        this.contextRepository = contextRepository;
        this.currentUserService = currentUserService;
        this.createOrganizationAction = createOrganizationAction;
        this.createWorkspaceAction = createWorkspaceAction;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse execute(CreateWorkspaceOnboardingCommand command) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);

        var org = createOrganizationAction.execute(
                new CreateOrganizationCommand(command.organizationName(), command.organizationCode(), null));
        WorkspaceDetailResponse ws = createWorkspaceAction.execute(
                new CreateWorkspaceCommand(org.id(), command.workspaceName(), command.workspaceCode(), command.workspaceDescription(), null, null));

        WorkspaceOnboardingState updated = state.onWorkspaceCreated(org.id(), ws.id());
        WorkspaceOnboardingState saved = onboardingRepository.save(updated);

        setCurrentWorkspace(userId, ws.id());

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_ONBOARDING, saved.id(),
                WorkspaceActivityActions.COMPLETE_ONBOARDING, "Onboarding completed via workspace creation");

        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    private WorkspaceOnboardingState loadOrCreate(UUID userId) {
        return onboardingRepository.findByUserId(userId)
                .orElseGet(() -> onboardingRepository.save(WorkspaceOnboardingState.create(userId)));
    }

    private void setCurrentWorkspace(UUID userId, UUID workspaceId) {
        WorkspaceUserContext ctx = contextRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceUserContext.create(userId));
        contextRepository.save(ctx.switchTo(workspaceId).completeOnboarding());
    }
}
