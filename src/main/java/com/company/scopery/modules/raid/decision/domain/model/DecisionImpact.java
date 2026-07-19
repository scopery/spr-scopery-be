package com.company.scopery.modules.raid.decision.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record DecisionImpact(UUID id, UUID decisionId, UUID projectId, String scopeImpact, Integer scheduleImpactDays,
        BigDecimal estimateHoursImpact, BigDecimal costImpact, BigDecimal revenueImpact, BigDecimal marginImpact,
        String riskImpact, String deliverableImpact, String acceptanceImpact, int version, Instant createdAt) {
    public static DecisionImpact create(UUID decisionId, UUID projectId, String scopeImpact, Integer scheduleImpactDays,
                                        BigDecimal estimateHoursImpact, BigDecimal costImpact, BigDecimal revenueImpact,
                                        BigDecimal marginImpact, String riskImpact, String deliverableImpact, String acceptanceImpact) {
        return new DecisionImpact(UUID.randomUUID(), decisionId, projectId, scopeImpact, scheduleImpactDays, estimateHoursImpact,
                costImpact, revenueImpact, marginImpact, riskImpact, deliverableImpact, acceptanceImpact, 0, Instant.now());
    }
}
