package com.company.scopery.modules.traceability.tracelink.application.action;
import com.company.scopery.modules.traceability.tracelink.application.response.TraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTraceLinkAction {
    private final TraceLinkRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public ArchiveTraceLinkAction(TraceLinkRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public TraceLinkResponse execute(UUID projectId, UUID id) {
        authorization.requireUpdate(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> TraceabilityExceptions.traceLinkNotFound(id));
        return TraceLinkResponse.from(repo.save(e.archive()));
    }
}
