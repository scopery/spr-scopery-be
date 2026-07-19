package com.company.scopery.modules.servicesupport.knowledgelink.application.service;
import com.company.scopery.modules.servicesupport.knowledgelink.application.response.SupportKnowledgeLinkResponse;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLinkRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportKnowledgeLinkQueryService {
    private final SupportKnowledgeLinkRepository repo; private final SupportAuthorizationService auth;
    public SupportKnowledgeLinkQueryService(SupportKnowledgeLinkRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportKnowledgeLinkResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportKnowledgeLinkResponse::from).toList();
    }
}
