package com.company.scopery.modules.resourcecapacity.effortestimate.domain.model;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.enums.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class EffortEstimateDomainTest {
    @Test void createEffortEstimateForTask_success() {
        var e = EffortEstimate.create(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(),
                EffortEstimateType.INITIAL, new BigDecimal("8"), null, null, null);
        assertThat(e.status()).isEqualTo(EffortEstimateStatus.ACTIVE);
    }
    @Test void createEffortEstimateForDefectRework_success() {
        var e = EffortEstimate.create(UUID.randomUUID(), UUID.randomUUID(), "DEFECT", UUID.randomUUID(),
                EffortEstimateType.REWORK, new BigDecimal("4"), null, null, "rework");
        assertThat(e.estimateType()).isEqualTo(EffortEstimateType.REWORK);
    }
    @Test void revisedEstimateSupersedesOld() {
        var e = EffortEstimate.create(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(),
                EffortEstimateType.INITIAL, new BigDecimal("8"), null, null, null).supersede();
        assertThat(e.status()).isEqualTo(EffortEstimateStatus.SUPERSEDED);
    }
    @Test void invalidNegativeEffort_rejected() {
        assertThatThrownBy(() -> EffortEstimate.create(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(),
                EffortEstimateType.INITIAL, new BigDecimal("-1"), null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
