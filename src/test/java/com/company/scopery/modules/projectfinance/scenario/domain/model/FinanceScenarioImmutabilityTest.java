package com.company.scopery.modules.projectfinance.scenario.domain.model;

import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FinanceScenarioImmutabilityTest {

    @Test
    void approvedFinanceScenario_isNotEditable() {
        ProjectFinanceScenario draft = ProjectFinanceScenario.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "FS-1", "Base", "desc", "VND", new BigDecimal("1000"),
                RevenueSplitMethod.COST_PROPORTION, ContingencyMethod.PERCENT_OF_DIRECT_COST, new BigDecimal("5"), null,
                OverheadMethod.PERCENT_OF_LABOR_COST, new BigDecimal("10"), null,
                new BigDecimal("30"), "{}", UUID.randomUUID(), "trace");

        assertThat(draft.isEditable()).isTrue();
        assertThat(draft.isDraft()).isTrue();

        ProjectFinanceScenario approved = draft.approve(UUID.randomUUID());
        assertThat(approved.status()).isEqualTo(FinanceScenarioStatus.APPROVED);
        assertThat(approved.isEditable()).isFalse();
        assertThat(approved.isDraft()).isFalse();
        assertThat(approved.approvedAt()).isNotNull();
    }
}
