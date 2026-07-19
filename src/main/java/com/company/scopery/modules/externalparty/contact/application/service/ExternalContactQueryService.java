package com.company.scopery.modules.externalparty.contact.application.service;
import com.company.scopery.modules.externalparty.contact.application.response.ExternalContactResponse;
import com.company.scopery.modules.externalparty.contact.domain.model.ExternalContactRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExternalContactQueryService {
    private final ExternalContactRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    public ExternalContactQueryService(ExternalContactRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ExternalContactResponse> list(UUID workspaceId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ExternalContactResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public ExternalContactResponse get(UUID workspaceId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(id, workspaceId).map(ExternalContactResponse::from)
                .orElseThrow(() -> ExternalPartyExceptions.contactNotFound(id));
    }
}
