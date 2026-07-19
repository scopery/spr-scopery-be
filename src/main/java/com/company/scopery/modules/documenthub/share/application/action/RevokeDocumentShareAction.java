package com.company.scopery.modules.documenthub.share.application.action;

import com.company.scopery.modules.documenthub.share.application.command.RevokeDocumentShareCommand;
import com.company.scopery.modules.documenthub.share.application.response.DocumentShareResponse;
import com.company.scopery.modules.documenthub.share.domain.model.DocumentShareRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RevokeDocumentShareAction {
    private final DocumentShareRepository repo;
    private final DocumentHubAuthorizationService authorization;

    public RevokeDocumentShareAction(DocumentShareRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public DocumentShareResponse execute(RevokeDocumentShareCommand c) {
        authorization.requireUpdate(c.projectId());
        var e = repo.findByIdAndProjectId(c.shareId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.shareNotFound(c.shareId()));
        return DocumentShareResponse.from(repo.save(e.revoke()));
    }
}
