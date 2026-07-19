package com.company.scopery.modules.project.milestone.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.milestone.application.command.AchieveProjectMilestoneCommand;
import com.company.scopery.modules.project.milestone.application.command.CreateProjectMilestoneCommand;
import com.company.scopery.modules.project.milestone.application.response.MilestoneResponse;
import com.company.scopery.modules.project.milestone.domain.enums.MilestoneStatus;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectMilestoneActionTest {

    @Mock ProjectMilestoneRepository milestones;
    @Mock ProjectPhaseRepository phases;
    @Mock WbsNodeRepository wbsNodes;
    @Mock ProjectWorkspaceAuthorizationService authorization;
    @Mock ProjectMutationGuard mutationGuard;
    @Mock ProjectPlatformPublisher publisher;
    @Mock ProjectActivityLogger activityLogger;
    @Mock CurrentUserAuthorizationService currentUser;

    CreateProjectMilestoneAction createAction;
    AchieveProjectMilestoneAction achieveAction;
    UUID projectId = UUID.randomUUID();
    UUID actorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createAction = new CreateProjectMilestoneAction(milestones, phases, wbsNodes, authorization,
                mutationGuard, publisher, activityLogger);
        achieveAction = new AchieveProjectMilestoneAction(milestones, authorization, mutationGuard,
                publisher, activityLogger, currentUser);
    }

    @Test
    void createMilestone() {
        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project());
        when(milestones.save(any())).thenAnswer(i -> i.getArgument(0));

        MilestoneResponse response = createAction.execute(new CreateProjectMilestoneCommand(
                projectId, null, null, "M1", "Kickoff", null, LocalDate.of(2026, 8, 15), 1));

        assertThat(response.name()).isEqualTo("Kickoff");
        assertThat(response.status()).isEqualTo("PLANNED");
        verify(publisher).enqueueMilestone(any(), eq("PROJECT_MILESTONE_CREATED"));
    }

    @Test
    void achieveMilestone() {
        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project());
        ProjectMilestone existing = ProjectMilestone.create(
                projectId, null, null, "M1", "Kickoff", null, LocalDate.of(2026, 8, 15), 1);
        when(milestones.findById(existing.id())).thenReturn(Optional.of(existing));
        when(milestones.save(any())).thenAnswer(i -> i.getArgument(0));
        when(currentUser.resolveCurrentUser()).thenReturn(IamUser.create(
                com.company.scopery.modules.iam.user.domain.valueobject.Username.of("actor"),
                com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress.of("actor@example.com"),
                "Actor"));

        MilestoneResponse response = achieveAction.execute(
                new AchieveProjectMilestoneCommand(projectId, existing.id()));

        assertThat(response.status()).isEqualTo(MilestoneStatus.ACHIEVED.name());
        verify(publisher).enqueueMilestone(any(), eq("PROJECT_MILESTONE_ACHIEVED"));
        verify(publisher).auditMilestoneAchieved(any(), any(), any());
    }

    private Project project() {
        return new Project(projectId, UUID.randomUUID(), UUID.randomUUID(), "P", "Project", null,
                actorId, "USD", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 31),
                ProjectStatus.ACTIVE, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null);
    }
}
