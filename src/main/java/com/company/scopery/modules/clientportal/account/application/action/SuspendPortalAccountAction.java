package com.company.scopery.modules.clientportal.account.application.action;
import com.company.scopery.modules.clientportal.account.application.command.SuspendPortalAccountCommand;
import com.company.scopery.modules.clientportal.account.application.response.ExternalPortalAccountResponse;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class SuspendPortalAccountAction {
    private final ExternalPortalAccountRepository repo;
    private final ClientPortalActivityLogger activityLogger;
    public SuspendPortalAccountAction(ExternalPortalAccountRepository repo, ClientPortalActivityLogger activityLogger) {
        this.repo = repo; this.activityLogger = activityLogger;
    }
    @Transactional
    public ExternalPortalAccountResponse execute(SuspendPortalAccountCommand c) {
        var account = repo.findById(c.accountId()).orElseThrow(() -> ClientPortalExceptions.accountNotFound(c.accountId()));
        try {
            var saved = repo.save(account.suspend());
            activityLogger.logSuccess(ClientPortalEntityTypes.ACCOUNT, saved.id(), ClientPortalActivityActions.ACCOUNT_SUSPENDED, "Portal account suspended");
            return ExternalPortalAccountResponse.from(saved);
        } catch (IllegalStateException ex) { throw ClientPortalExceptions.accountNotSuspendable(); }
    }
}
