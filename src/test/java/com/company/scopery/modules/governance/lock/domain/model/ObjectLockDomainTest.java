package com.company.scopery.modules.governance.lock.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class ObjectLockDomainTest {
    @Test void createAndRelease() {
        var lock = ObjectLock.create(UUID.randomUUID(), UUID.randomUUID(), "QUOTE", UUID.randomUUID(), "EDIT", UUID.randomUUID(), "finalize");
        assertThat(lock.status()).isEqualTo("ACTIVE");
        assertThat(lock.release(UUID.randomUUID()).status()).isEqualTo("RELEASED");
    }
}
