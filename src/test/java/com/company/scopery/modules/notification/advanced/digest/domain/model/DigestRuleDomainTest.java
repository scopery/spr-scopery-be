package com.company.scopery.modules.notification.advanced.digest.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class DigestRuleDomainTest {
    @Test void createActiveDigest() {
        var r = DigestRule.create(UUID.randomUUID(), "DAILY", "Daily digest", "WORKSPACE", "DAILY", null);
        assertThat(r.status()).isEqualTo("ACTIVE");
        assertThat(r.update(null, null, "WEEKLY", null, "INACTIVE").frequency()).isEqualTo("WEEKLY");
    }
}
