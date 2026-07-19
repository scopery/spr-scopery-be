package com.company.scopery.modules.scope.deliverable.application.service;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DeliverableQueryService {
    private final DeliverableRepository deliverables; private final ScopeAuthorizationService authorization;
    public DeliverableQueryService(DeliverableRepository deliverables, ScopeAuthorizationService authorization) {
        this.deliverables=deliverables; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DeliverableResponse> list(UUID projectId){ authorization.requireDeliverableView(projectId); return deliverables.findByProjectId(projectId).stream().map(DeliverableResponse::from).toList(); }
    @Transactional(readOnly=true)
    public DeliverableResponse get(UUID projectId, UUID id){
        authorization.requireDeliverableView(projectId);
        return deliverables.findByIdAndProjectId(id, projectId).map(DeliverableResponse::from).orElseThrow(() -> ScopeExceptions.deliverableNotFound(id));
    }
}
