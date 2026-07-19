package com.company.scopery.modules.clientportal.comment.application.service;
import com.company.scopery.modules.clientportal.comment.application.response.ClientCommentResponse;
import com.company.scopery.modules.clientportal.comment.domain.model.ClientCommentRepository;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ClientCommentQueryService {
    private final ClientCommentRepository repo;
    private final ClientPortalAuthorizationService authorization;
    private final PortalGrantEnforcementService grantEnforcement;
    public ClientCommentQueryService(ClientCommentRepository repo, ClientPortalAuthorizationService authorization, PortalGrantEnforcementService grantEnforcement) {
        this.repo=repo; this.authorization=authorization; this.grantEnforcement=grantEnforcement;
    }
    @Transactional(readOnly=true)
    public List<ClientCommentResponse> listInternal(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ClientCommentResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<ClientCommentResponse> listPortal(UUID projectId) {
        grantEnforcement.requireActiveGrant(projectId);
        return repo.findByProjectId(projectId).stream().map(ClientCommentResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<ClientCommentResponse> listPortalByTarget(UUID projectId, String targetType, UUID targetId) {
        grantEnforcement.requireActiveGrant(projectId);
        return repo.findByProjectIdAndTargetTypeAndTargetId(projectId, targetType, targetId).stream().map(ClientCommentResponse::from).toList();
    }
}
