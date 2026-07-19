package com.company.scopery.modules.clientportal.invite.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.clientportal.invite.application.command.CreatePortalInviteCommand;
import com.company.scopery.modules.clientportal.invite.application.response.ExternalPortalInviteResponse;
import com.company.scopery.modules.clientportal.invite.domain.model.ExternalPortalInvite;
import com.company.scopery.modules.clientportal.invite.domain.model.ExternalPortalInviteRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;
@Component
public class CreatePortalInviteAction {
    private final ProjectRepository projects;
    private final ExternalPortalInviteRepository repo;
    private final ClientPortalAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ClientPortalActivityLogger activityLogger;
    public CreatePortalInviteAction(ProjectRepository projects, ExternalPortalInviteRepository repo,
            ClientPortalAuthorizationService authorization, CurrentUserAuthorizationService currentUser, ClientPortalActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public ExternalPortalInviteResponse execute(CreatePortalInviteCommand c) {
        authorization.requireManage(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw ClientPortalExceptions.projectArchived(c.projectId());
        String rawToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        String hash = sha256(rawToken);
        int days = c.expiresInDays() == null || c.expiresInDays() < 1 ? 14 : c.expiresInDays();
        var saved = repo.save(ExternalPortalInvite.create(project.workspaceId(), project.id(), c.email().trim(), hash,
                Instant.now().plus(days, ChronoUnit.DAYS), currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess(ClientPortalEntityTypes.INVITE, saved.id(), ClientPortalActivityActions.INVITE_CREATED, "Portal invite created");
        return ExternalPortalInviteResponse.from(saved, rawToken);
    }
    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
