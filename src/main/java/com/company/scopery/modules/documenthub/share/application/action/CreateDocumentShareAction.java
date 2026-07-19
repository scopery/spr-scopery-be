package com.company.scopery.modules.documenthub.share.application.action;
import com.company.scopery.modules.documenthub.share.application.command.CreateDocumentShareCommand;
import com.company.scopery.modules.documenthub.share.application.response.DocumentShareResponse;
import com.company.scopery.modules.documenthub.share.domain.model.DocumentShare;
import com.company.scopery.modules.documenthub.share.domain.model.DocumentShareRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDocumentShareAction {
    private final DocumentShareRepository repo;
    private final DocumentHubAuthorizationService authorization;
    public CreateDocumentShareAction(DocumentShareRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public DocumentShareResponse execute(CreateDocumentShareCommand c) {
        authorization.requireUpdate(c.projectId());
        return DocumentShareResponse.from(repo.save(DocumentShare.create(c.documentId(), c.projectId(), c.shareType().trim(), c.granteeType().trim(), c.granteeId(), c.expiresAt())));
    }
}
