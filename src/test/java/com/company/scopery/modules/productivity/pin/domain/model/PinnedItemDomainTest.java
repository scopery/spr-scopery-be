package com.company.scopery.modules.productivity.pin.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class PinnedItemDomainTest {
    @Test void createPersonalPin() {
        var p = PinnedItem.create(UUID.randomUUID(), null, "PERSONAL", UUID.randomUUID(), "PROJECT", UUID.randomUUID(), 1);
        assertThat(p.archivedAt()).isNull();
        assertThat(p.archive().archivedAt()).isNotNull();
    }
}
