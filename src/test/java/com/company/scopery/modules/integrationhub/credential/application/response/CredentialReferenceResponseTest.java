package com.company.scopery.modules.integrationhub.credential.application.response;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReference;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class CredentialReferenceResponseTest {
    @Test void credentialApiDoesNotReturnRawSecret() {
        var c = CredentialReference.create(UUID.randomUUID(), "CSV", "API_KEY", "super-secret-value");
        var r = CredentialReferenceResponse.from(c);
        assertThat(r.secretReferenceMasked()).doesNotContain("super-secret-value");
        assertThat(r.secretReferenceMasked()).startsWith("ref:");
    }
}
