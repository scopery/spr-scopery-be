package com.company.scopery.modules.clientportal.shared.security;
import com.company.scopery.platform.security.JwtAuthFilter;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccount;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class CurrentPortalAccountService {
    private final ExternalPortalAccountRepository accounts;
    public CurrentPortalAccountService(ExternalPortalAccountRepository accounts) { this.accounts = accounts; }
    public ExternalPortalAccount requireCurrentPortalAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getDetails() == null) throw ClientPortalExceptions.accessDenied();
        if (!(auth.getDetails() instanceof JwtAuthFilter.MapTokenDetails details)) throw ClientPortalExceptions.accessDenied();
        if (!JwtService.PRINCIPAL_PORTAL.equals(details.principalType())) throw ClientPortalExceptions.accessDenied();
        return accounts.findById(details.subjectId()).orElseThrow(() -> ClientPortalExceptions.accountNotFound(details.subjectId()));
    }
    public UUID requireCurrentPortalAccountId() {
        return requireCurrentPortalAccount().id();
    }
}
