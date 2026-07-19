package com.company.scopery.modules.traceability.requirementversion.application.service;
import com.company.scopery.modules.traceability.requirementversion.application.response.RequirementVersionResponse;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersionRepository;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RequirementVersionQueryService {
    private final RequirementVersionRepository repo;
    public RequirementVersionQueryService(RequirementVersionRepository repo) { this.repo = repo; }
    @Transactional(readOnly = true)
    public List<RequirementVersionResponse> list(UUID requirementId) {
        return repo.findByRequirementId(requirementId).stream().map(RequirementVersionResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public RequirementVersionResponse get(UUID requirementId, UUID versionId) {
        return repo.findByIdAndRequirementId(versionId, requirementId).map(RequirementVersionResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.requirementVersionNotFound(versionId));
    }
}
