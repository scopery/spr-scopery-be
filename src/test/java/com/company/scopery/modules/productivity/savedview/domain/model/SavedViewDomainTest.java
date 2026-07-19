package com.company.scopery.modules.productivity.savedview.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class SavedViewDomainTest {
    @Test void createAndArchive() {
        var v = SavedView.create(UUID.randomUUID(), null, UUID.randomUUID(), "TASK", "My view", "{}", null);
        assertThat(v.status()).isEqualTo("ACTIVE");
        assertThat(v.archive().status()).isEqualTo("ARCHIVED");
    }
}
