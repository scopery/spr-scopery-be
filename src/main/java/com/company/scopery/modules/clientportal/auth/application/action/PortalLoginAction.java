package com.company.scopery.modules.clientportal.auth.application.action;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.modules.clientportal.account.domain.enums.PortalAccountStatus;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.auth.application.response.PortalAuthResult;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class PortalLoginAction {
    private final ExternalPortalAccountRepository accounts;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public PortalLoginAction(ExternalPortalAccountRepository accounts, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.accounts=accounts; this.passwordEncoder=passwordEncoder; this.jwtService=jwtService;
    }
    @Transactional
    public PortalAuthResult execute(UUID workspaceId, String email, String password) {
        var account = accounts.findByWorkspaceIdAndEmail(workspaceId, email.trim().toLowerCase())
                .orElseThrow(ClientPortalExceptions::invalidCredentials);
        if (account.status() != PortalAccountStatus.ACTIVE
                || account.passwordHash() == null
                || !passwordEncoder.matches(password, account.passwordHash())) {
            throw ClientPortalExceptions.invalidCredentials();
        }
        var loggedIn = accounts.save(account.recordLogin());
        String token = jwtService.generatePortalToken(loggedIn.id(), loggedIn.email());
        return new PortalAuthResult(loggedIn.id(), loggedIn.email(), loggedIn.displayName(), token, jwtService.getExpirationMs());
    }
}
