package com.company.scopery.modules.traceability.requirement.application.service;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse; import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService; import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RequirementQueryService {
    private final RequirementRepository repo; private final TraceabilityAuthorizationService authorization;
    public RequirementQueryService(RequirementRepository repo, TraceabilityAuthorizationService authorization){this.repo=repo;this.authorization=authorization;}
    @Transactional(readOnly=true) public List<RequirementResponse> list(UUID projectId){authorization.requireView(projectId);return repo.findByProjectId(projectId).stream().map(RequirementResponse::from).toList();}
    @Transactional(readOnly=true) public RequirementResponse get(UUID projectId, UUID id){
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(RequirementResponse::from).orElseThrow(() -> TraceabilityExceptions.requirementNotFound(id));
    }
}
