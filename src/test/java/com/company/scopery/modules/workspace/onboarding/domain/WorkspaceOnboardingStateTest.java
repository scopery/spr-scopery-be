package com.company.scopery.modules.workspace.onboarding.domain;

import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingOption;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStatus;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStep;
import com.company.scopery.common.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceOnboardingStateTest {

    private final UUID userId = UUID.randomUUID();

    @Test
    void resetChoice_fromCreateWorkspace_returnsChooseOption() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId)
                .chooseOption(WorkspaceOnboardingOption.CREATE_WORKSPACE);

        WorkspaceOnboardingState reset = state.resetChoice();

        assertThat(reset.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS);
        assertThat(reset.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION);
        assertThat(reset.selectedOption()).isNull();
    }

    @Test
    void chooseOption_fromCancelled_advancesToSubStep() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId).cancel();

        WorkspaceOnboardingState next = state.chooseOption(WorkspaceOnboardingOption.CREATE_WORKSPACE);

        assertThat(next.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS);
        assertThat(next.currentStep()).isEqualTo(WorkspaceOnboardingStep.CREATE_WORKSPACE);
        assertThat(next.selectedOption()).isEqualTo(WorkspaceOnboardingOption.CREATE_WORKSPACE);
    }

    @Test
    void resetChoice_fromCancelled_returnsChooseOption() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId).cancel();

        WorkspaceOnboardingState reset = state.resetChoice();

        assertThat(reset.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS);
        assertThat(reset.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION);
        assertThat(reset.selectedOption()).isNull();
    }

    @Test
    void resetChoice_fromChooseOption_throwsInvalidStep() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId);

        assertThatThrownBy(state::resetChoice)
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Cannot perform this action at the current onboarding step");
    }

    @Test
    void resetChoice_fromWaitingApproval_returnsChooseOption() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId)
                .onJoinRequestSubmitted(UUID.randomUUID());

        WorkspaceOnboardingState reset = state.resetChoice();

        assertThat(reset.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS);
        assertThat(reset.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION);
        assertThat(reset.selectedOption()).isNull();
    }

    @Test
    void chooseOption_requestToJoin_throwsNotSupported() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId);

        assertThatThrownBy(() -> state.chooseOption(WorkspaceOnboardingOption.REQUEST_TO_JOIN))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not supported");
    }
}
