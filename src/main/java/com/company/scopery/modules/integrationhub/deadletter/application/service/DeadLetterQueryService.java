package com.company.scopery.modules.integrationhub.deadletter.application.service;
import com.company.scopery.modules.integrationhub.deadletter.application.response.DeadLetterEventResponse;
import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEventRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DeadLetterQueryService {
    private final DeadLetterEventRepository repo;
    private final IntegrationAuthorizationService auth;
    public DeadLetterQueryService(DeadLetterEventRepository repo, IntegrationAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<DeadLetterEventResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(DeadLetterEventResponse::from).toList();
    }
}
