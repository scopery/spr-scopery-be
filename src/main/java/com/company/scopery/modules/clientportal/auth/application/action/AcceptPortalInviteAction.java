package com.company.scopery.modules.clientportal.auth.application.action;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.modules.clientportal.account.domain.enums.PortalAccountStatus;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccount;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.auth.application.response.PortalAuthResult;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrant;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrantRepository;
import com.company.scopery.modules.clientportal.invite.domain.model.ExternalPortalInviteRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
@Component
public class AcceptPortalInviteAction {
    private final ExternalPortalInviteRepository invites;
    private final ExternalPortalAccountRepository accounts;
    private final ExternalProjectAccessGrantRepository grants;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ClientPortalActivityLogger activityLogger;
    public AcceptPortalInviteAction(ExternalPortalInviteRepository invites, ExternalPortalAccountRepository accounts,
            ExternalProjectAccessGrantRepository grants, PasswordEncoder passwordEncoder, JwtService jwtService,
            ClientPortalActivityLogger activityLogger) {
        this.invites=invites; this.accounts=accounts; this.grants=grants; this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService; this.activityLogger=activityLogger;
    }
    @Transactional
    public PortalAuthResult execute(String inviteToken, String password, String displayName) {
        String hash = sha256(inviteToken);
        var invite = invites.findByInviteTokenHash(hash).orElseThrow(ClientPortalExceptions::inviteInvalid);
        if (!invite.isUsable()) throw ClientPortalExceptions.inviteInvalid();
        var existing = accounts.findByWorkspaceIdAndEmail(invite.workspaceId(), invite.email());
        ExternalPortalAccount account;
        if (existing.isPresent()) {
            account = existing.get();
            if (account.status() != PortalAccountStatus.ACTIVE) {
                account = accounts.save(account.activate(passwordEncoder.encode(password)));
            }
        } else {
            account = accounts.save(ExternalPortalAccount.createInvited(invite.workspaceId(), null, invite.email(),
                    displayName != null ? displayName : invite.email()).activate(passwordEncoder.encode(password)));
        }
        invites.save(invite.accept(account.id()));
        final ExternalPortalAccount activated = account;
        if (invite.projectId() != null) {
            grants.findByProjectIdAndPortalAccountId(invite.projectId(), activated.id())
                    .orElseGet(() -> grants.save(ExternalProjectAccessGrant.create(
                            invite.projectId(), invite.workspaceId(), activated.id(), "CLIENT_DEFAULT", null)));
        }
        activityLogger.logSuccess(ClientPortalEntityTypes.ACCOUNT, account.id(), ClientPortalActivityActions.ACCOUNT_ACTIVATED, "Portal invite accepted");
        activityLogger.logSuccess(ClientPortalEntityTypes.INVITE, invite.id(), ClientPortalActivityActions.INVITE_ACCEPTED, "Invite accepted");
        String token = jwtService.generatePortalToken(account.id(), account.email());
        return new PortalAuthResult(account.id(), account.email(), account.displayName(), token, jwtService.getExpirationMs());
    }
    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
