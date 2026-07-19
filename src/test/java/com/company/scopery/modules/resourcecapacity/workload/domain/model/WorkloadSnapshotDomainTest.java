package com.company.scopery.modules.resourcecapacity.workload.domain.model;
import org.junit.jupiter.api.Test; import java.math.BigDecimal; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class WorkloadSnapshotDomainTest {
    @Test void workloadSnapshotImmutable() {
        var s = WorkloadSnapshot.create(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN,
                BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ONE, 1, null, "MANUAL");
        assertThat(s.snapshotSource()).isEqualTo("MANUAL");
        assertThat(s.snapshotAt()).isNotNull();
    }
}
