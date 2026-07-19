package com.company.scopery.modules.clientportal.account.application.action;
import com.company.scopery.modules.clientportal.account.application.command.DeactivatePortalAccountCommand;
import com.company.scopery.modules.clientportal.account.application.response.ExternalPortalAccountResponse;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccountRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DeactivatePortalAccountAction {
    private final ExternalPortalAccountRepository repo;
    private final ClientPortalActivityLogger activityLogger;
    public DeactivatePortalAccountAction(ExternalPortalAccountRepository repo, ClientPortalActivityLogger activityLogger) {
        this.repo = repo; this.activityLogger = activityLogger;
    }
    @Transactional
    public ExternalPortalAccountResponse execute(DeactivatePortalAccountCommand c) {
        var account = repo.findById(c.accountId()).orElseThrow(() -> ClientPortalExceptions.accountNotFound(c.accountId()));
        try {
            var saved = repo.save(account.deactivate());
            activityLogger.logSuccess(ClientPortalEntityTypes.ACCOUNT, saved.id(), ClientPortalActivityActions.ACCOUNT_DEACTIVATED, "Portal account deactivated");
            return ExternalPortalAccountResponse.from(saved);
        } catch (IllegalStateException ex) { throw ClientPortalExceptions.accountAlreadyDeactivated(); }
    }
}
