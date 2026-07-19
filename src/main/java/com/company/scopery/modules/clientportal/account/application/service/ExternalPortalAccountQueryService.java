package com.company.scopery.modules.clientportal.account.application.service;
import com.company.scopery.modules.clientportal.account.application.response.ExternalPortalAccountResponse;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class ExternalPortalAccountQueryService {
    private final ExternalPortalAccountRepository repo;
    public ExternalPortalAccountQueryService(ExternalPortalAccountRepository repo) { this.repo = repo; }
    @Transactional(readOnly = true)
    public ExternalPortalAccountResponse get(UUID workspaceId, UUID accountId) {
        var account = repo.findById(accountId).orElseThrow(() -> ClientPortalExceptions.accountNotFound(accountId));
        return ExternalPortalAccountResponse.from(account);
    }
}
