package com.company.scopery.modules.trust.privacy.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class PrivacyRequestDomainTest {
    @Test void createPrivacyRequest_success() {
        var r = PrivacyRequest.submit(UUID.randomUUID(), "ACCESS_EXPORT", "user@x.com", UUID.randomUUID());
        assertThat(r.status()).isEqualTo("SUBMITTED");
    }
    @Test void privacyRequestReject_requiresReason() {
        var r = PrivacyRequest.submit(UUID.randomUUID(), "DELETION", "x", null);
        assertThatThrownBy(() -> r.reject(" ")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void privacyRequestLifecycle_success() {
        var r = PrivacyRequest.submit(UUID.randomUUID(), "ACCESS_EXPORT", "x", null).triage().markInReview().complete("exported");
        assertThat(r.status()).isEqualTo("COMPLETED");
    }
}
