package com.company.scopery.modules.clientportal.auth.application.service;
import com.company.scopery.modules.clientportal.auth.application.response.PortalMeResponse;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class PortalAuthQueryService {
    private final CurrentPortalAccountService currentPortalAccount;
    public PortalAuthQueryService(CurrentPortalAccountService currentPortalAccount) { this.currentPortalAccount = currentPortalAccount; }
    @Transactional(readOnly=true)
    public PortalMeResponse me() {
        var a = currentPortalAccount.requireCurrentPortalAccount();
        return new PortalMeResponse(a.id(), a.workspaceId(), a.email(), a.displayName(), a.status().name(), a.lastLoginAt());
    }
}
