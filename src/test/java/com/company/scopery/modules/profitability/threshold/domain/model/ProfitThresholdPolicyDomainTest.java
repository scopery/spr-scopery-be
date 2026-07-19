package com.company.scopery.modules.profitability.threshold.domain.model;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class ProfitThresholdPolicyDomainTest {
    @Test void defaultsAndUpdate() {
        var p = ProfitThresholdPolicy.defaults(UUID.randomUUID(), UUID.randomUUID());
        assertThat(p.healthyMarginPercent()).isEqualByComparingTo("30");
        assertThat(p.update(BigDecimal.valueOf(25), null, null, null).healthyMarginPercent()).isEqualByComparingTo("25");
    }
}
