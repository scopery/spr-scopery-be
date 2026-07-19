package com.company.scopery.modules.traceability.requirementsource.application.service;
import com.company.scopery.modules.traceability.requirementsource.application.response.RequirementSourceResponse;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSourceRepository;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RequirementSourceQueryService {
    private final RequirementSourceRepository repo;
    public RequirementSourceQueryService(RequirementSourceRepository repo) { this.repo = repo; }
    @Transactional(readOnly = true)
    public List<RequirementSourceResponse> list(UUID requirementId) {
        return repo.findByRequirementId(requirementId).stream().map(RequirementSourceResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public RequirementSourceResponse get(UUID requirementId, UUID sourceId) {
        return repo.findByIdAndRequirementId(sourceId, requirementId).map(RequirementSourceResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.requirementSourceNotFound(sourceId));
    }
}
