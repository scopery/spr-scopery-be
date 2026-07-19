package com.company.scopery.modules.clientportal.feedback.application.service;
import com.company.scopery.modules.clientportal.feedback.application.response.ClientFeedbackResponse;
import com.company.scopery.modules.clientportal.feedback.domain.model.ClientFeedbackRepository;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ClientFeedbackQueryService {
    private final ClientFeedbackRepository repo;
    private final ClientPortalAuthorizationService authorization;
    private final PortalGrantEnforcementService grantEnforcement;
    public ClientFeedbackQueryService(ClientFeedbackRepository repo, ClientPortalAuthorizationService authorization, PortalGrantEnforcementService grantEnforcement) {
        this.repo=repo; this.authorization=authorization; this.grantEnforcement=grantEnforcement;
    }
    @Transactional(readOnly=true)
    public List<ClientFeedbackResponse> listInternal(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ClientFeedbackResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<ClientFeedbackResponse> listPortal(UUID projectId) {
        grantEnforcement.requireActiveGrant(projectId);
        return repo.findByProjectId(projectId).stream().map(ClientFeedbackResponse::from).toList();
    }
}
