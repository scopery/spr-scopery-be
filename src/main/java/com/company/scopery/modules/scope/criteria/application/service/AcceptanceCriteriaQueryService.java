package com.company.scopery.modules.scope.criteria.application.service;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AcceptanceCriteriaQueryService {
    private final AcceptanceCriteriaRepository criteria; private final DeliverableRepository deliverables;
    private final ScopeAuthorizationService authorization;
    public AcceptanceCriteriaQueryService(AcceptanceCriteriaRepository criteria, DeliverableRepository deliverables,
                                          ScopeAuthorizationService authorization) {
        this.criteria=criteria; this.deliverables=deliverables; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<AcceptanceCriteriaResponse> listByDeliverable(UUID projectId, UUID deliverableId) {
        authorization.requireDeliverableView(projectId);
        deliverables.findByIdAndProjectId(deliverableId, projectId).orElseThrow(() -> ScopeExceptions.deliverableNotFound(deliverableId));
        return criteria.findByDeliverableId(deliverableId).stream().map(AcceptanceCriteriaResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public AcceptanceCriteriaResponse get(UUID projectId, UUID criteriaId) {
        authorization.requireDeliverableView(projectId);
        var c = criteria.findById(criteriaId).orElseThrow(() -> ScopeExceptions.criteriaNotFound(criteriaId));
        if (!c.projectId().equals(projectId)) throw ScopeExceptions.criteriaNotFound(criteriaId);
        return AcceptanceCriteriaResponse.from(c);
    }
}
