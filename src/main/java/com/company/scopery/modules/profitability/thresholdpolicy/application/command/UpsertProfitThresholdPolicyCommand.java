package com.company.scopery.modules.profitability.thresholdpolicy.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpsertProfitThresholdPolicyCommand(
        UUID projectId,
        BigDecimal healthyMarginPercent,
        BigDecimal watchMarginPercent,
        BigDecimal atRiskMarginPercent,
        BigDecimal lossRiskMarginPercent
) {}
