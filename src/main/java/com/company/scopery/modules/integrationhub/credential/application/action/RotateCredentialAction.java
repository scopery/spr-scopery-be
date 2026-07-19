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
public class RotateCredentialAction {
    private final CredentialReferenceRepository credentials;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public RotateCredentialAction(CredentialReferenceRepository credentials,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.credentials = credentials; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public CredentialReferenceResponse execute(UUID workspaceId, UUID credentialId, String newSecretReference) {
        auth.requireManage(workspaceId);
        var c = credentials.findById(credentialId).orElseThrow(() -> IntegrationExceptions.credentialNotFound(credentialId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.credentialNotFound(credentialId);
        if ("REVOKED".equals(c.status())) throw IntegrationExceptions.credentialRevoked(credentialId);
        var saved = credentials.save(c.rotate(newSecretReference));
        activity.logSuccess("INTEGRATION_CREDENTIAL", saved.id(), "INTEGRATION_CREDENTIAL_ROTATED", "Credential rotated");
        return CredentialReferenceResponse.from(saved);
    }
}
