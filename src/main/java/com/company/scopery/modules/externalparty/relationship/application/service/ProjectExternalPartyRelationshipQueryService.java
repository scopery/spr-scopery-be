package com.company.scopery.modules.externalparty.relationship.application.service;
import com.company.scopery.modules.externalparty.relationship.application.response.ProjectExternalPartyRelationshipResponse;
import com.company.scopery.modules.externalparty.relationship.domain.model.ProjectExternalPartyRelationshipRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ProjectExternalPartyRelationshipQueryService {
    private final ProjectExternalPartyRelationshipRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    public ProjectExternalPartyRelationshipQueryService(ProjectExternalPartyRelationshipRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ProjectExternalPartyRelationshipResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ProjectExternalPartyRelationshipResponse::from).toList();
    }
}
