package com.company.scopery.modules.trust.accessreview.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class AccessReviewDomainTest {
    @Test void findingDoesNotAutoRevokeAccess() {
        var f = PermissionReviewFinding.open(UUID.randomUUID(), UUID.randomUUID(), "EXPIRED_ACCESS", "MEDIUM", "Revoke via IAM").resolve();
        assertThat(f.status()).isEqualTo("RESOLVED");
        assertThat(f.recommendation()).contains("IAM");
    }
    @Test void createAccessReviewCampaign_success() {
        var c = AccessReviewCampaign.draft(UUID.randomUUID(), "Q2").start();
        assertThat(c.status()).isEqualTo("ACTIVE");
    }
}
