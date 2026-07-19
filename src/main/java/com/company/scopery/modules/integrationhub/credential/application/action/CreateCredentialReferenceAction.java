package com.company.scopery.modules.integrationhub.credential.application.action;
import com.company.scopery.modules.integrationhub.credential.application.response.CredentialReferenceResponse;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReference;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReferenceRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateCredentialReferenceAction {
    private final CredentialReferenceRepository credentials;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateCredentialReferenceAction(CredentialReferenceRepository credentials,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.credentials = credentials; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public CredentialReferenceResponse execute(UUID workspaceId, String providerCode, String credentialType, String secretReference) {
        auth.requireManage(workspaceId);
        String ref = (secretReference == null || secretReference.isBlank())
                ? "secret-ref://" + UUID.randomUUID() : secretReference;
        var saved = credentials.save(CredentialReference.create(workspaceId, providerCode, credentialType, ref));
        activity.logSuccess("INTEGRATION_CREDENTIAL", saved.id(), "INTEGRATION_CREDENTIAL_CREATED", "Credential created");
        return CredentialReferenceResponse.from(saved);
    }
}
