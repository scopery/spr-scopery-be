package com.company.scopery.modules.profitability.threshold.http.request;
import java.math.BigDecimal;
public record UpsertThresholdRequest(BigDecimal healthyMarginPercent, BigDecimal watchMarginPercent, BigDecimal atRiskMarginPercent, BigDecimal lossRiskMarginPercent) {}
