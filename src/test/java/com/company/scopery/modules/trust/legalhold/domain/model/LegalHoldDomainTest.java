package com.company.scopery.modules.trust.legalhold.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class LegalHoldDomainTest {
    @Test void createLegalHold_success() {
        var h = LegalHold.create(UUID.randomUUID(), "LEGAL", "PROJECT", UUID.randomUUID(), "litigation");
        assertThat(h.isActive()).isTrue();
    }
    @Test void releaseLegalHold_requiresReason() {
        var h = LegalHold.create(UUID.randomUUID(), "LEGAL", "WORKSPACE", null, "reason");
        assertThatThrownBy(() -> h.release("")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void legalHoldDoesNotGrantAccess() {
        var h = LegalHold.create(UUID.randomUUID(), "LEGAL", "WORKSPACE", null, "reason");
        assertThat(h.scopeType()).isEqualTo("WORKSPACE");
    }
}
