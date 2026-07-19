package com.company.scopery.modules.governance.versioning.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class GovernanceVersionRecordDomainTest {
    @Test void finalizeAndClearCurrent() {
        var v = GovernanceVersionRecord.create(UUID.randomUUID(), UUID.randomUUID(), "QUOTE", UUID.randomUUID(), UUID.randomUUID(), "UPDATE", null, 1);
        assertThat(v.currentFlag()).isTrue();
        assertThat(v.clearCurrent().currentFlag()).isFalse();
        assertThat(v.markFinalized().finalizedFlag()).isTrue();
    }
}
