package com.company.scopery.modules.externalparty.stakeholder.application.service;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService; import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import com.company.scopery.modules.externalparty.stakeholder.application.response.ProjectStakeholderResponse; import com.company.scopery.modules.externalparty.stakeholder.domain.model.ProjectStakeholderRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ProjectStakeholderQueryService {
    private final ProjectStakeholderRepository repo; private final ExternalPartyAuthorizationService authorization;
    public ProjectStakeholderQueryService(ProjectStakeholderRepository repo, ExternalPartyAuthorizationService authorization){this.repo=repo;this.authorization=authorization;}
    @Transactional(readOnly=true) public List<ProjectStakeholderResponse> list(UUID projectId){authorization.requireView(projectId);return repo.findByProjectId(projectId).stream().map(ProjectStakeholderResponse::from).toList();}
    @Transactional(readOnly=true) public ProjectStakeholderResponse get(UUID projectId, UUID id){
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(ProjectStakeholderResponse::from).orElseThrow(() -> ExternalPartyExceptions.stakeholderNotFound(id));
    }
}
