package com.company.scopery.modules.externalparty.contact.application.action;
import com.company.scopery.modules.externalparty.contact.application.command.CreateExternalContactCommand;
import com.company.scopery.modules.externalparty.contact.application.response.ExternalContactResponse;
import com.company.scopery.modules.externalparty.contact.domain.model.ExternalContact;
import com.company.scopery.modules.externalparty.contact.domain.model.ExternalContactRepository;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyActivityActions;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateExternalContactAction {
    private final ExternalContactRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    private final ExternalPartyActivityLogger activityLogger;
    public CreateExternalContactAction(ExternalContactRepository repo, ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ExternalContactResponse execute(CreateExternalContactCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        boolean primary = Boolean.TRUE.equals(c.primaryFlag());
        var saved = repo.save(ExternalContact.create(c.workspaceId(), c.organizationId(), c.firstName().trim(), c.lastName().trim(), c.email(), c.phone(), c.title(), primary));
        activityLogger.logSuccess(ExternalPartyEntityTypes.CONTACT, saved.id(), ExternalPartyActivityActions.CONTACT_CREATED, "Contact created");
        return ExternalContactResponse.from(saved);
    }
}
