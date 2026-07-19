package com.company.scopery.modules.governance.ownership.application.service;
import com.company.scopery.modules.governance.ownership.application.response.ObjectOwnershipResponse;
import com.company.scopery.modules.governance.ownership.domain.model.ObjectOwnershipRepository;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ObjectOwnershipQueryService {
    private final ObjectOwnershipRepository ownerships; private final GovernanceAuthorizationService authorization;
    public ObjectOwnershipQueryService(ObjectOwnershipRepository ownerships, GovernanceAuthorizationService authorization) {
        this.ownerships=ownerships; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public ObjectOwnershipResponse getActive(UUID projectId, String objectType, UUID targetId) {
        authorization.requireOwnershipView(projectId);
        return ObjectOwnershipResponse.from(ownerships.findActive(objectType, targetId).orElseThrow(() -> GovernanceExceptions.ownershipNotFound(objectType, targetId)));
    }
    @Transactional(readOnly=true)
    public List<ObjectOwnershipResponse> listByProject(UUID projectId) {
        authorization.requireOwnershipView(projectId);
        return ownerships.findByProjectId(projectId).stream().map(ObjectOwnershipResponse::from).toList();
    }
}
