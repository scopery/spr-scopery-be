package com.company.scopery.modules.servicesupport.supportcase.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class SupportCaseDomainTest {
    @Test void createSupportCase_success() {
        var c = SupportCase.create(UUID.randomUUID(), UUID.randomUUID(), "BUG_REPORT", "HIGH", "Login broken", "PORTAL_SUBMISSION", true);
        assertThat(c.status()).isEqualTo("NEW");
        assertThat(c.portalVisible()).isTrue();
    }
    @Test void supportCaseLifecycle_success() {
        var c = SupportCase.create(UUID.randomUUID(), null, "QUESTION", "NORMAL", "Help", "INTERNAL_CREATE", false)
                .triage(UUID.randomUUID()).startProgress().resolve().close();
        assertThat(c.status()).isEqualTo("CLOSED");
    }
}
