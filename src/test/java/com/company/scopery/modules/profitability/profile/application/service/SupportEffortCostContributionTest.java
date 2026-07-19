package com.company.scopery.modules.profitability.profile.application.service;

import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupportEffortCostContributionTest {

    @Test
    void defaultHourlyCost_isDocumentedConstant() {
        assertThat(ProfitabilitySummaryRebuildService.DEFAULT_SUPPORT_HOURLY_COST)
                .isEqualByComparingTo("75.00");
    }

    @Test
    void cancelledEffort_domainExcludesFromActiveCostSemantics() {
        SupportEffortRecord active = SupportEffortRecord.create(
                UUID.randomUUID(), UUID.randomUUID(), null, new BigDecimal("2.0"), LocalDate.now());
        SupportEffortRecord cancelled = active.cancel();
        assertThat(cancelled.status()).isEqualTo("CANCELLED");
        assertThat(cancelled.effortHours()).isEqualByComparingTo("2.0");
    }
}
