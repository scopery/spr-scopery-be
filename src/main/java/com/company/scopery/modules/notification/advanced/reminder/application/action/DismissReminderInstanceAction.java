package com.company.scopery.modules.notification.advanced.reminder.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.reminder.application.command.DismissReminderInstanceCommand;
import com.company.scopery.modules.notification.advanced.reminder.application.response.ReminderInstanceResponse;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.*;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DismissReminderInstanceAction {
    private final ReminderInstanceRepository instances; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public DismissReminderInstanceAction(ReminderInstanceRepository instances, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.instances=instances; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public ReminderInstanceResponse execute(DismissReminderInstanceCommand c) {
        iam.requireWorkspaceAccess(c.workspaceId(), currentUser.resolveCurrentUser().id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        var instance = instances.findByIdAndWorkspaceId(c.reminderInstanceId(), c.workspaceId())
                .orElseThrow(() -> AdvancedNotificationExceptions.reminderInstanceNotFound(c.reminderInstanceId()));
        return ReminderInstanceResponse.from(instances.save(instance.dismiss()));
    }
}
