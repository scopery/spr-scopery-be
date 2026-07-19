package com.company.scopery.modules.externalparty.authority.application.service;
import com.company.scopery.modules.externalparty.authority.application.response.ProjectApprovalAuthorityResponse;
import com.company.scopery.modules.externalparty.authority.domain.model.ProjectApprovalAuthorityRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ProjectApprovalAuthorityQueryService {
    private final ProjectApprovalAuthorityRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    public ProjectApprovalAuthorityQueryService(ProjectApprovalAuthorityRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ProjectApprovalAuthorityResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ProjectApprovalAuthorityResponse::from).toList();
    }
}
