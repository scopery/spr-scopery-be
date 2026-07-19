package com.company.scopery.modules.servicesupport.snapshot.application.service;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.snapshot.application.response.SupportMetricSnapshotResponse;
import com.company.scopery.modules.servicesupport.snapshot.domain.model.SupportMetricSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportMetricSnapshotQueryService {
    private final SupportMetricSnapshotRepository repo; private final SupportAuthorizationService auth;
    public SupportMetricSnapshotQueryService(SupportMetricSnapshotRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportMetricSnapshotResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportMetricSnapshotResponse::from).toList();
    }
}
