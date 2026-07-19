package com.company.scopery.modules.clientportal.auth.application.action;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.auth.application.response.PortalMeResponse;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ChangePortalPasswordAction {
    private final CurrentPortalAccountService currentPortalAccount;
    private final ExternalPortalAccountRepository accounts;
    private final PasswordEncoder passwordEncoder;
    public ChangePortalPasswordAction(CurrentPortalAccountService currentPortalAccount, ExternalPortalAccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.currentPortalAccount=currentPortalAccount; this.accounts=accounts; this.passwordEncoder=passwordEncoder;
    }
    @Transactional
    public PortalMeResponse execute(String currentPassword, String newPassword) {
        var account = currentPortalAccount.requireCurrentPortalAccount();
        if (account.passwordHash() == null || !passwordEncoder.matches(currentPassword, account.passwordHash())) {
            throw ClientPortalExceptions.invalidCredentials();
        }
        var saved = accounts.save(account.changePassword(passwordEncoder.encode(newPassword)));
        return new PortalMeResponse(saved.id(), saved.workspaceId(), saved.email(), saved.displayName(), saved.status().name(), saved.lastLoginAt());
    }
}
