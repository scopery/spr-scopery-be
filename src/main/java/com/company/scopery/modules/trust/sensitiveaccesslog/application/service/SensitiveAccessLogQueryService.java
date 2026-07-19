package com.company.scopery.modules.trust.sensitiveaccesslog.application.service;
import com.company.scopery.modules.trust.sensitiveaccesslog.application.response.SensitiveAccessLogResponse;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLogRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class SensitiveAccessLogQueryService {
    private final SensitiveAccessLogRepository repo;
    private final TrustAuthorizationService auth;
    public SensitiveAccessLogQueryService(SensitiveAccessLogRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<SensitiveAccessLogResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SensitiveAccessLogResponse::from).toList();
    }
}
