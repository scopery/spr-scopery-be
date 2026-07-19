package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import org.junit.jupiter.api.Test; import java.time.Instant; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class MaintenanceWindowDomainTest {
    @Test void scheduleAndComplete_success() {
        Instant start = Instant.now().plusSeconds(60);
        var w = MaintenanceWindow.schedule(UUID.randomUUID(), UUID.randomUUID(), "Patch", start, start.plusSeconds(120)).complete();
        assertThat(w.status()).isEqualTo("COMPLETED");
    }
    @Test void invalidWindowRejected() {
        Instant start = Instant.now();
        assertThatThrownBy(() -> MaintenanceWindow.schedule(UUID.randomUUID(), UUID.randomUUID(), "x", start, start.minusSeconds(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
