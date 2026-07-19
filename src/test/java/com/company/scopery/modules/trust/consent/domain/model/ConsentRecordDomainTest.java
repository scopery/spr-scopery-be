package com.company.scopery.modules.trust.consent.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class ConsentRecordDomainTest {
    @Test void withdrawConsent_success() {
        var c = ConsentRecord.given(UUID.randomUUID(), "EMAIL_NOTIFICATION").withdraw();
        assertThat(c.status()).isEqualTo("WITHDRAWN");
    }
}
