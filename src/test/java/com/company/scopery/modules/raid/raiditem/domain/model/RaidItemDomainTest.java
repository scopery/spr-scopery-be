package com.company.scopery.modules.raid.raiditem.domain.model;

import com.company.scopery.modules.raid.raiditem.domain.enums.RaidImpact;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidProbability;
import com.company.scopery.modules.raid.raiditem.domain.enums.RiskResponseStrategy;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RaidItemDomainTest {

    @Test
    void withRiskFields_setsScoreAndFormulaVersion() {
        RaidItem item = RaidItem.create(UUID.randomUUID(), UUID.randomUUID(), RaidItemType.RISK, "R-1", "Risk", null, null)
                .withRiskFields(RaidProbability.HIGH, RaidImpact.MEDIUM, RiskResponseStrategy.MITIGATE, "trigger");
        assertThat(item.riskScore()).isEqualTo(6);
        assertThat(item.riskScoreFormulaVersion()).isEqualTo("v1");
    }

    @Test
    void convertRiskToIssue_onlyAllowedForRisk() {
        RaidItem issue = RaidItem.create(UUID.randomUUID(), UUID.randomUUID(), RaidItemType.ISSUE, "I-1", "Issue", null, null);
        assertThatThrownBy(issue::convertRiskToIssue).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void convertRiskToIssue_changesType() {
        RaidItem risk = RaidItem.create(UUID.randomUUID(), UUID.randomUUID(), RaidItemType.RISK, "R-1", "Risk", null, null);
        RaidItem issue = risk.convertRiskToIssue();
        assertThat(issue.type()).isEqualTo(RaidItemType.ISSUE);
    }
}
