package com.company.scopery.modules.integrationhub.credential.application.action;
import com.company.scopery.modules.integrationhub.credential.application.response.CredentialReferenceResponse;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReferenceRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RevokeCredentialAction {
    private final CredentialReferenceRepository credentials;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public RevokeCredentialAction(CredentialReferenceRepository credentials,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.credentials = credentials; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public CredentialReferenceResponse execute(UUID workspaceId, UUID credentialId) {
        auth.requireManage(workspaceId);
        var c = credentials.findById(credentialId).orElseThrow(() -> IntegrationExceptions.credentialNotFound(credentialId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.credentialNotFound(credentialId);
        var saved = credentials.save(c.revoke());
        activity.logSuccess("INTEGRATION_CREDENTIAL", saved.id(), "INTEGRATION_CREDENTIAL_REVOKED", "Credential revoked");
        return CredentialReferenceResponse.from(saved);
    }
}
