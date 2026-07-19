package com.company.scopery.modules.traceability.tracelink.application.service;
import com.company.scopery.modules.traceability.tracelink.application.response.TraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TraceLinkQueryService {
    private final TraceLinkRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public TraceLinkQueryService(TraceLinkRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<TraceLinkResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(TraceLinkResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public TraceLinkResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(TraceLinkResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.traceLinkNotFound(id));
    }
}
