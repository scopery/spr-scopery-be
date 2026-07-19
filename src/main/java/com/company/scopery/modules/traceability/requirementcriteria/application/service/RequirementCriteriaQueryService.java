package com.company.scopery.modules.traceability.requirementcriteria.application.service;
import com.company.scopery.modules.traceability.requirementcriteria.application.response.RequirementCriteriaResponse;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteriaRepository;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RequirementCriteriaQueryService {
    private final RequirementCriteriaRepository repo;
    public RequirementCriteriaQueryService(RequirementCriteriaRepository repo) { this.repo = repo; }
    @Transactional(readOnly = true)
    public List<RequirementCriteriaResponse> list(UUID requirementId) {
        return repo.findByRequirementId(requirementId).stream().map(RequirementCriteriaResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public RequirementCriteriaResponse get(UUID requirementId, UUID criteriaId) {
        return repo.findByIdAndRequirementId(criteriaId, requirementId).map(RequirementCriteriaResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.requirementCriteriaNotFound(criteriaId));
    }
}
