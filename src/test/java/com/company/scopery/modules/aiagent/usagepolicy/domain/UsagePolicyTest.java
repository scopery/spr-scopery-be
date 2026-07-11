package com.company.scopery.modules.aiagent.usagepolicy.domain;

import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class UsagePolicyTest {

    private static final UsagePolicyCode CODE = UsagePolicyCode.of("GLOBAL_LIMIT");

    private UsagePolicy buildGlobal() {
        return UsagePolicy.create(CODE, "Global Limit", UsagePolicyTargetType.GLOBAL, null,
                1000, null, null, null, null, UsagePolicyPeriod.DAY,
                UsagePolicyAction.BLOCK, 100, null);
    }

    @Test
    void create_newPolicy_isInactiveByDefault() {
        UsagePolicy policy = buildGlobal();

        assertThat(policy.id()).isNotNull();
        assertThat(policy.status()).isEqualTo(UsagePolicyStatus.INACTIVE);
        assertThat(policy.code().value()).isEqualTo("GLOBAL_LIMIT");
        assertThat(policy.targetType()).isEqualTo(UsagePolicyTargetType.GLOBAL);
        assertThat(policy.targetId()).isNull();
        assertThat(policy.priority()).isEqualTo(100);
    }

    @Test
    void create_blankName_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> UsagePolicy.create(CODE, "", UsagePolicyTargetType.GLOBAL, null,
                1000, null, null, null, null, UsagePolicyPeriod.DAY, UsagePolicyAction.BLOCK, 100, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usage policy name is required");
    }

    @Test
    void activate_fromInactive_setsStatusToActive() {
        UsagePolicy policy = buildGlobal();
        policy.activate();

        assertThat(policy.status()).isEqualTo(UsagePolicyStatus.ACTIVE);
    }

    @Test
    void deactivate_setsStatusToInactive() {
        UsagePolicy policy = buildGlobal();
        policy.activate();
        policy.deactivate();

        assertThat(policy.status()).isEqualTo(UsagePolicyStatus.INACTIVE);
    }

    @Test
    void activate_deprecatedPolicy_throwsIllegalStateException() {
        UsagePolicy policy = UsagePolicy.reconstitute(UUID.randomUUID(), CODE, "Old Limit",
                UsagePolicyTargetType.GLOBAL, null, 1000, null, null, null, null,
                UsagePolicyPeriod.DAY, UsagePolicyAction.BLOCK, 100, null,
                UsagePolicyStatus.DEPRECATED, Instant.now(), Instant.now());

        assertThatThrownBy(policy::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Deprecated usage policy cannot be activated again");
    }

    @Test
    void update_changesAllowedFields() {
        UsagePolicy policy = buildGlobal();

        policy.update("Updated Limit", 500, null, null, 10, null,
                UsagePolicyPeriod.HOUR, UsagePolicyAction.WARN, 50, "Updated description");

        assertThat(policy.name()).isEqualTo("Updated Limit");
        assertThat(policy.maxRequestsPerPeriod()).isEqualTo(500);
        assertThat(policy.maxConcurrentRequests()).isEqualTo(10);
        assertThat(policy.period()).isEqualTo(UsagePolicyPeriod.HOUR);
        assertThat(policy.action()).isEqualTo(UsagePolicyAction.WARN);
        assertThat(policy.priority()).isEqualTo(50);
    }

    @Test
    void update_codeAndTargetTypeRemainImmutable() {
        UsagePolicy policy = buildGlobal();
        policy.update("New Name", null, null, null, 5, null,
                null, UsagePolicyAction.WARN, 100, null);

        assertThat(policy.code().value()).isEqualTo("GLOBAL_LIMIT");
        assertThat(policy.targetType()).isEqualTo(UsagePolicyTargetType.GLOBAL);
    }

    @Test
    void create_withDailyBudgetOnly_noPeriodNeeded() {
        UsagePolicy policy = UsagePolicy.create(CODE, "Budget Only", UsagePolicyTargetType.GLOBAL, null,
                null, null, null, null, new BigDecimal("100.00"),
                null, UsagePolicyAction.BLOCK, 100, null);

        assertThat(policy.dailyBudget()).isEqualByComparingTo("100.00");
        assertThat(policy.period()).isNull();
    }
}