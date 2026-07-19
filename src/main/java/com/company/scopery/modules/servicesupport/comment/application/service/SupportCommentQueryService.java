package com.company.scopery.modules.servicesupport.comment.application.service;
import com.company.scopery.modules.servicesupport.comment.application.response.SupportCommentResponse;
import com.company.scopery.modules.servicesupport.comment.domain.model.SupportCommentRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportCommentQueryService {
    private final SupportCommentRepository repo; private final SupportAuthorizationService auth;
    public SupportCommentQueryService(SupportCommentRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportCommentResponse> listByCase(UUID workspaceId, UUID caseId, boolean portalView) {
        auth.requireView(workspaceId);
        return repo.findBySupportCaseId(caseId).stream()
                .filter(c -> !portalView || c.isPortalVisible())
                .map(SupportCommentResponse::from).toList();
    }
}
