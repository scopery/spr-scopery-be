package com.company.scopery.modules.scope.mapping.application.service;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.mapping.application.response.DeliverableTaskMappingResponse;
import com.company.scopery.modules.scope.mapping.application.response.ScopeItemWbsMappingResponse;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMappingRepository;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMappingRepository;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ScopeMappingQueryService {
    private final ScopeItemRepository items;
    private final DeliverableRepository deliverables;
    private final ScopeItemWbsMappingRepository wbsMappings;
    private final DeliverableTaskMappingRepository taskMappings;
    private final ScopeAuthorizationService authorization;
    public ScopeMappingQueryService(ScopeItemRepository items, DeliverableRepository deliverables,
                                    ScopeItemWbsMappingRepository wbsMappings, DeliverableTaskMappingRepository taskMappings,
                                    ScopeAuthorizationService authorization) {
        this.items = items; this.deliverables = deliverables; this.wbsMappings = wbsMappings;
        this.taskMappings = taskMappings; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<ScopeItemWbsMappingResponse> listWbsMappings(UUID projectId, UUID scopeItemId) {
        authorization.requireScopeView(projectId);
        items.findByIdAndProjectId(scopeItemId, projectId)
                .orElseThrow(() -> ScopeExceptions.itemNotFound(scopeItemId));
        return wbsMappings.findActiveByScopeItemId(scopeItemId).stream().map(ScopeItemWbsMappingResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public List<DeliverableTaskMappingResponse> listTaskMappings(UUID projectId, UUID deliverableId) {
        authorization.requireDeliverableView(projectId);
        deliverables.findByIdAndProjectId(deliverableId, projectId)
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(deliverableId));
        return taskMappings.findActiveByDeliverableId(deliverableId).stream().map(DeliverableTaskMappingResponse::from).toList();
    }
}
