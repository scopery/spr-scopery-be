package com.company.scopery.modules.clientportal.auth.application.action;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.modules.clientportal.auth.application.response.PortalAuthResult;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RefreshPortalTokenAction {
    private final CurrentPortalAccountService currentPortalAccount;
    private final JwtService jwtService;
    public RefreshPortalTokenAction(CurrentPortalAccountService currentPortalAccount, JwtService jwtService) {
        this.currentPortalAccount=currentPortalAccount; this.jwtService=jwtService;
    }
    @Transactional(readOnly=true)
    public PortalAuthResult execute() {
        var account = currentPortalAccount.requireCurrentPortalAccount();
        String token = jwtService.generatePortalToken(account.id(), account.email());
        return new PortalAuthResult(account.id(), account.email(), account.displayName(), token, jwtService.getExpirationMs());
    }
}
