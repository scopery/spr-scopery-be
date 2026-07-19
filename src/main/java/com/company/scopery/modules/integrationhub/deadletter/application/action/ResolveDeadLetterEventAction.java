package com.company.scopery.modules.integrationhub.deadletter.application.action;
import com.company.scopery.modules.integrationhub.deadletter.application.response.DeadLetterEventResponse;
import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEventRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ResolveDeadLetterEventAction {
    private final DeadLetterEventRepository repo;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public ResolveDeadLetterEventAction(DeadLetterEventRepository repo, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.repo = repo; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public DeadLetterEventResponse execute(UUID workspaceId, UUID deadLetterId) {
        auth.requireManage(workspaceId);
        var d = repo.findById(deadLetterId).orElseThrow(() -> IntegrationExceptions.deadLetterEventNotFound(deadLetterId));
        if (!workspaceId.equals(d.workspaceId())) throw IntegrationExceptions.deadLetterEventNotFound(deadLetterId);
        var saved = repo.save(d.resolve(null));
        activity.logSuccess("INTEGRATION_DEAD_LETTER", saved.id(), "INTEGRATION_DEAD_LETTER_RESOLVED", "Dead letter event resolved");
        return DeadLetterEventResponse.from(saved);
    }
}
