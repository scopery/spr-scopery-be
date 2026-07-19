package com.company.scopery.modules.externalparty.documentlink.application.action;

import com.company.scopery.modules.externalparty.documentlink.application.command.CreateExternalPartyDocumentLinkCommand;
import com.company.scopery.modules.externalparty.documentlink.application.response.ExternalPartyDocumentLinkResponse;
import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLink;
import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLinkRepository;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyActivityActions;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateExternalPartyDocumentLinkAction {
    private final ExternalPartyDocumentLinkRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    private final ExternalPartyActivityLogger activityLogger;

    public CreateExternalPartyDocumentLinkAction(ExternalPartyDocumentLinkRepository repo,
            ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public ExternalPartyDocumentLinkResponse execute(CreateExternalPartyDocumentLinkCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var saved = repo.save(ExternalPartyDocumentLink.create(c.workspaceId(),
                c.externalOrganizationId(), c.externalContactId(), c.documentId(), c.linkNote()));
        activityLogger.logSuccess(ExternalPartyEntityTypes.DOCUMENT_LINK, saved.id(),
                ExternalPartyActivityActions.DOCUMENT_LINK_CREATED, "Document link created");
        return ExternalPartyDocumentLinkResponse.from(saved);
    }
}
