package com.company.scopery.modules.resourcecapacity.shared.domain;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
class ResourceUtilizationCalculatorTest {
    @Test void utilizationCalculated_success() {
        assertThat(ResourceUtilizationCalculator.utilizationPercent(new BigDecimal("40"), new BigDecimal("40")))
                .isEqualByComparingTo("100");
    }
    @Test void overloadDetected_success() {
        assertThat(ResourceUtilizationCalculator.overloadHours(new BigDecimal("50"), new BigDecimal("40")))
                .isEqualByComparingTo("10");
    }
    @Test void underAllocationDetected_success() {
        assertThat(ResourceUtilizationCalculator.underAllocationHours(new BigDecimal("20"), new BigDecimal("40")))
                .isEqualByComparingTo("20");
    }
    @Test void capacityZeroHandledSafely() {
        assertThat(ResourceUtilizationCalculator.utilizationPercent(new BigDecimal("10"), BigDecimal.ZERO)).isNull();
        assertThat(ResourceUtilizationCalculator.utilizationStatus(null,
                new BigDecimal("50"), new BigDecimal("50"), new BigDecimal("85"),
                new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("120"))).isEqualTo("UNAVAILABLE");
    }
    @Test void criticalOverloadStatus() {
        assertThat(ResourceUtilizationCalculator.utilizationStatus(new BigDecimal("130"),
                new BigDecimal("50"), new BigDecimal("50"), new BigDecimal("85"),
                new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("120"))).isEqualTo("CRITICAL_OVERLOAD");
    }
    @Test void costAmountFromEffortAndRate() {
        assertThat(ResourceUtilizationCalculator.costAmount(new BigDecimal("10"), new BigDecimal("50")))
                .isEqualByComparingTo("500.0000");
    }
}
