package com.company.scopery.modules.servicesupport.queue.application.service;
import com.company.scopery.modules.servicesupport.queue.application.response.SupportQueueResponse;
import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueueRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportQueueQueryService {
    private final SupportQueueRepository repo; private final SupportAuthorizationService auth;
    public SupportQueueQueryService(SupportQueueRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportQueueResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportQueueResponse::from).toList();
    }
}
