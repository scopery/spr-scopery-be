package com.company.scopery.modules.governance.ownership.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class ObjectOwnershipDomainTest {
    @Test void assignAndRevoke() {
        var o = ObjectOwnership.assign(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(), "INTERNAL_USER", UUID.randomUUID(), UUID.randomUUID());
        assertThat(o.status()).isEqualTo("ACTIVE");
        o = o.revoke(UUID.randomUUID());
        assertThat(o.status()).isEqualTo("REVOKED");
        assertThat(o.revokedAt()).isNotNull();
    }
}
