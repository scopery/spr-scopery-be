package com.company.scopery.modules.profitability.shared.domain;
import com.company.scopery.modules.profitability.profile.domain.enums.ProfitabilityStatus;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;
class ProfitabilityCalculatorTest {
    @Test void profitAndMarginUseBigDecimal() {
        assertThat(ProfitabilityCalculator.profit(new BigDecimal("100.00"), new BigDecimal("40.00"))).isEqualByComparingTo("60.00");
        assertThat(ProfitabilityCalculator.marginPercent(new BigDecimal("100"), new BigDecimal("40"))).isEqualByComparingTo("60.0000");
    }
    @Test void statusThresholds() {
        assertThat(ProfitabilityCalculator.status(new BigDecimal("35"), new BigDecimal("30"), new BigDecimal("20"), new BigDecimal("10"), BigDecimal.ZERO))
                .isEqualTo(ProfitabilityStatus.HEALTHY);
        assertThat(ProfitabilityCalculator.status(new BigDecimal("-1"), new BigDecimal("30"), new BigDecimal("20"), new BigDecimal("10"), BigDecimal.ZERO))
                .isEqualTo(ProfitabilityStatus.LOSS);
    }
}
