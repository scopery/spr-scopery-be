package com.company.scopery.modules.ratecard.inflationpolicy.domain.model;

import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.CompoundFrequency;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public record InflationPolicy(
        UUID id, String code, String name, String description, InflationPolicyScope scope,
        UUID organizationId, UUID workspaceId, BigDecimal inflationPercent, CompoundFrequency compoundFrequency,
        LocalDate effectiveFrom, LocalDate effectiveTo, InflationPolicyStatus status,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt
) {
    public static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    }

    public static InflationPolicy create(String code, String name, String description, InflationPolicyScope scope,
                                         UUID organizationId, UUID workspaceId, BigDecimal inflationPercent,
                                         CompoundFrequency frequency, LocalDate effectiveFrom, LocalDate effectiveTo) {
        return new InflationPolicy(UUID.randomUUID(), normalizeCode(code), name, description, scope,
                organizationId, workspaceId, inflationPercent, frequency, effectiveFrom, effectiveTo,
                InflationPolicyStatus.ACTIVE, null, null, 0, null, null);
    }

    public InflationPolicy update(String name, String description, BigDecimal inflationPercent,
                                  CompoundFrequency frequency, LocalDate effectiveFrom, LocalDate effectiveTo) {
        return new InflationPolicy(id, code, name, description, scope, organizationId, workspaceId,
                inflationPercent, frequency, effectiveFrom, effectiveTo, status, archivedAt, archivedBy,
                version, createdAt, updatedAt);
    }

    public InflationPolicy activate() {
        return withStatus(InflationPolicyStatus.ACTIVE, null, null);
    }
    public InflationPolicy deactivate() {
        return withStatus(InflationPolicyStatus.INACTIVE, archivedAt, archivedBy);
    }
    public InflationPolicy archive(UUID actorId) {
        return withStatus(InflationPolicyStatus.ARCHIVED, Instant.now(), actorId);
    }

    private InflationPolicy withStatus(InflationPolicyStatus s, Instant archivedAt, UUID archivedBy) {
        return new InflationPolicy(id, code, name, description, scope, organizationId, workspaceId,
                inflationPercent, compoundFrequency, effectiveFrom, effectiveTo, s,
                archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public boolean covers(LocalDate date) {
        if (date == null || effectiveFrom == null) return false;
        if (date.isBefore(effectiveFrom)) return false;
        return effectiveTo == null || !date.isAfter(effectiveTo);
    }
}
