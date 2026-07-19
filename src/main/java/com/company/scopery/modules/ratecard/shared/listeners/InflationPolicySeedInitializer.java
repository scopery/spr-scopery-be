package com.company.scopery.modules.ratecard.shared.listeners;

import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.CompoundFrequency;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyStatus;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicyRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Order(21)
public class InflationPolicySeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String DEFAULT_CODE = "DEFAULT_ANNUAL_INFLATION_0_PERCENT";

    private final InflationPolicyRepository inflationPolicyRepository;

    public InflationPolicySeedInitializer(InflationPolicyRepository inflationPolicyRepository) {
        this.inflationPolicyRepository = inflationPolicyRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (inflationPolicyRepository.existsByScopeAndCode(InflationPolicyScope.SYSTEM, null, null, DEFAULT_CODE)) {
            return;
        }
        InflationPolicy policy = InflationPolicy.create(
                DEFAULT_CODE,
                "Default Annual Inflation 0%",
                "Safe system default: 0% annual inflation",
                InflationPolicyScope.SYSTEM,
                null,
                null,
                BigDecimal.ZERO,
                CompoundFrequency.ANNUAL,
                LocalDate.of(2000, 1, 1),
                null
        );
        // ensure ACTIVE SYSTEM built-in style
        if (policy.status() != InflationPolicyStatus.ACTIVE) {
            policy = policy.activate();
        }
        inflationPolicyRepository.save(policy);
    }
}
