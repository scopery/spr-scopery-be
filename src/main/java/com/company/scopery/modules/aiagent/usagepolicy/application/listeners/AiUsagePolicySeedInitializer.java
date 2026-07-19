package com.company.scopery.modules.aiagent.usagepolicy.application.listeners;

import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Order(13)
public class AiUsagePolicySeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiUsagePolicySeedInitializer.class);

    public static final String DEV_CODE = "DEFAULT_AI_USAGE_POLICY_DEV";
    public static final String PROD_CODE = "DEFAULT_AI_USAGE_POLICY_PROD";

    private final UsagePolicyRepository usagePolicyRepository;

    public AiUsagePolicySeedInitializer(UsagePolicyRepository usagePolicyRepository) {
        this.usagePolicyRepository = usagePolicyRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        trySeedDev();
        trySeedProd();
        log.info("[AiUsagePolicySeed] Default usage policy seeding complete");
    }

    @Transactional
    public void trySeedDev() {
        UsagePolicyCode code = UsagePolicyCode.of(DEV_CODE);
        if (usagePolicyRepository.existsByCode(code)) {
            return;
        }
        try {
            UsagePolicy policy = UsagePolicy.create(
                    code,
                    "Default AI Usage Policy (DEV)",
                    UsagePolicyTargetType.GLOBAL,
                    null,
                    1000,
                    500_000L,
                    new BigDecimal("50.000000"),
                    20,
                    new BigDecimal("50.000000"),
                    "DEV",
                    60,
                    5000,
                    8000,
                    1_000_000,
                    new BigDecimal("50.000000"),
                    null,
                    null,
                    UsagePolicyPeriod.DAY,
                    UsagePolicyAction.BLOCK,
                    100,
                    "Conservative default DEV policy — does not enable execution without provider/deployment");
            policy.activate();
            usagePolicyRepository.save(policy);
        } catch (Exception e) {
            log.warn("[AiUsagePolicySeed] DEV policy skipped — constraint violation: {}", e.getMessage());
        }
    }

    @Transactional
    public void trySeedProd() {
        UsagePolicyCode code = UsagePolicyCode.of(PROD_CODE);
        if (usagePolicyRepository.existsByCode(code)) {
            return;
        }
        try {
            UsagePolicy policy = UsagePolicy.create(
                    code,
                    "Default AI Usage Policy (PROD)",
                    UsagePolicyTargetType.GLOBAL,
                    null,
                    200,
                    100_000L,
                    new BigDecimal("20.000000"),
                    5,
                    new BigDecimal("20.000000"),
                    "PROD",
                    20,
                    1000,
                    4000,
                    200_000,
                    new BigDecimal("20.000000"),
                    null,
                    null,
                    UsagePolicyPeriod.DAY,
                    UsagePolicyAction.BLOCK,
                    50,
                    "Stricter default PROD policy — does not enable execution without provider/deployment");
            policy.activate();
            usagePolicyRepository.save(policy);
        } catch (Exception e) {
            log.warn("[AiUsagePolicySeed] PROD policy skipped — constraint violation: {}", e.getMessage());
        }
    }
}
