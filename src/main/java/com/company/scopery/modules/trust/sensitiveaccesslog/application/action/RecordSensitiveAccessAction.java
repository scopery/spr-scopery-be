package com.company.scopery.modules.trust.sensitiveaccesslog.application.action;
import com.company.scopery.modules.trust.sensitiveaccesslog.application.response.SensitiveAccessLogResponse;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLog;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLogRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RecordSensitiveAccessAction {
    private final SensitiveAccessLogRepository repo;
    private final TrustAuthorizationService auth;
    public RecordSensitiveAccessAction(SensitiveAccessLogRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public SensitiveAccessLogResponse execute(UUID workspaceId, UUID projectId, String actorPrincipalType,
            UUID actorUserId, String targetObjectType, UUID targetObjectId, String fieldPath,
            String classification, String accessAction, String accessChannel, String reason,
            String requestPath, String traceId) {
        auth.requireManage(workspaceId);
        var saved = repo.save(SensitiveAccessLog.record(workspaceId, projectId, actorPrincipalType,
                actorUserId, targetObjectType, targetObjectId, fieldPath, classification,
                accessAction, accessChannel, reason, requestPath, traceId));
        return SensitiveAccessLogResponse.from(saved);
    }
}
