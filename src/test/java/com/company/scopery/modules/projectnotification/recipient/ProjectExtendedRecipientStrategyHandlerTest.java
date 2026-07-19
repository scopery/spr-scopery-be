package com.company.scopery.modules.projectnotification.recipient;

import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreferenceRepository;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscriptionRepository;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectExtendedRecipientStrategyHandlerTest {

    @Mock ProjectRepository projects;
    @Mock TaskRepository tasks;
    @Mock ProjectNotificationSubscriptionRepository projectSubscriptions;
    @Mock TaskNotificationSubscriptionRepository taskSubscriptions;
    @Mock ProjectNotificationPreferenceRepository preferences;
    @Mock IamUserRepository users;
    @Mock WorkspaceMemberRepository members;

    @InjectMocks ProjectExtendedRecipientStrategyHandler handler;

    @Test
    void resolveTaskAssignee_success() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        Task task = org.mockito.Mockito.mock(Task.class);
        when(task.inChargeUserId()).thenReturn(userId);
        when(tasks.findById(taskId)).thenReturn(Optional.of(task));

        IamUser user = IamUser.create(Username.of("user1"), EmailAddress.of("a@example.com"), "User");
        // recreate with fixed id via mock
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(members.findByWorkspaceIdAndUserId(workspaceId, userId))
                .thenReturn(Optional.of(WorkspaceMember.create(workspaceId, userId)));

        EmailRule rule = EmailRule.createSystem(
                "RULE_TEST", "Test", null, UUID.randomUUID(), UUID.randomUUID(),
                EmailRecipientStrategy.TASK_ASSIGNEE, null, 10);

        var results = handler.resolveAll(rule, Map.of(
                "task", Map.of("id", taskId.toString()),
                "workspace", Map.of("id", workspaceId.toString())
        ));
        assertFalse(results.isEmpty());
        assertFalse(results.getFirst().skipped());
        assertEquals("a@example.com", results.getFirst().email());
    }

    @Test
    void supportsProjectStrategies() {
        assertTrue(handler.supports(EmailRecipientStrategy.PROJECT_WATCHERS));
        assertTrue(handler.supports(EmailRecipientStrategy.FINANCE_WATCHERS));
        assertFalse(handler.supports(EmailRecipientStrategy.EVENT_ACTOR));
    }
}
