package com.company.scopery.modules.notification.advanced.reminder.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.reminder.application.response.ReminderInstanceResponse;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.ReminderInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ReminderInstanceQueryService {
    private final ReminderInstanceRepository instances; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public ReminderInstanceQueryService(ReminderInstanceRepository instances, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.instances=instances; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public List<ReminderInstanceResponse> listMine(UUID workspaceId) {
        iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        return instances.findByWorkspaceIdAndRecipientUserId(workspaceId, currentUser.resolveCurrentUser().id()).stream().map(ReminderInstanceResponse::from).toList();
    }
}
