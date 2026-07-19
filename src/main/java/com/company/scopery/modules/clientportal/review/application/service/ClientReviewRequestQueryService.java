package com.company.scopery.modules.clientportal.review.application.service;
import com.company.scopery.modules.clientportal.review.application.response.ClientReviewRequestResponse; import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewRequestRepository;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService; import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ClientReviewRequestQueryService {
    private final ClientReviewRequestRepository repo; private final ClientPortalAuthorizationService authorization;
    public ClientReviewRequestQueryService(ClientReviewRequestRepository repo, ClientPortalAuthorizationService authorization){this.repo=repo;this.authorization=authorization;}
    @Transactional(readOnly=true) public List<ClientReviewRequestResponse> list(UUID projectId){authorization.requireView(projectId);return repo.findByProjectId(projectId).stream().map(ClientReviewRequestResponse::from).toList();}
    @Transactional(readOnly=true) public ClientReviewRequestResponse get(UUID projectId, UUID id){
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(ClientReviewRequestResponse::from).orElseThrow(() -> ClientPortalExceptions.reviewNotFound(id));
    }
}
