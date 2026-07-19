package com.company.scopery.modules.governance.grant.application.service;
import com.company.scopery.modules.governance.grant.application.response.ObjectAccessGrantResponse;
import com.company.scopery.modules.governance.grant.domain.model.ObjectAccessGrantRepository;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AccessGrantQueryService {
    private final ObjectAccessGrantRepository grants; private final GovernanceAuthorizationService authorization;
    public AccessGrantQueryService(ObjectAccessGrantRepository grants, GovernanceAuthorizationService authorization) { this.grants=grants; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<ObjectAccessGrantResponse> list(UUID projectId, String objectType, UUID targetId) {
        authorization.requireOwnershipView(projectId);
        return grants.findActiveByTarget(objectType, targetId).stream().map(ObjectAccessGrantResponse::from).toList();
    }
}
