package com.company.scopery.modules.resourcecapacity.risk.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class ResourceRiskFlagDomainTest {
    @Test void createResourceRiskFlag_success() {
        var f = ResourceRiskFlag.open(UUID.randomUUID(), UUID.randomUUID(), null, "RESOURCE_OVERLOADED", "DELIVERY_RISK", "overload");
        assertThat(f.status()).isEqualTo("OPEN");
    }
    @Test void mitigateResourceRiskFlag_success() {
        var f = ResourceRiskFlag.open(UUID.randomUUID(), UUID.randomUUID(), null, "CAPACITY_GAP", "COST_RISK", "gap").mitigate();
        assertThat(f.status()).isEqualTo("MITIGATED");
    }
    @Test void closeResourceRiskFlag_success() {
        var f = ResourceRiskFlag.open(UUID.randomUUID(), UUID.randomUUID(), null, "CAPACITY_GAP", "COST_RISK", "gap").close();
        assertThat(f.status()).isEqualTo("CLOSED");
    }
}
