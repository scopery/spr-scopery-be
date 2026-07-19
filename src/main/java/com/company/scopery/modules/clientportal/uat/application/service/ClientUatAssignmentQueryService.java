package com.company.scopery.modules.clientportal.uat.application.service;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.uat.application.response.ClientUatAssignmentResponse;
import com.company.scopery.modules.clientportal.uat.domain.model.ClientUatAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ClientUatAssignmentQueryService {
    private final ClientUatAssignmentRepository repo;
    private final ClientPortalAuthorizationService authorization;
    public ClientUatAssignmentQueryService(ClientUatAssignmentRepository repo, ClientPortalAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ClientUatAssignmentResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ClientUatAssignmentResponse::from).toList();
    }
}
