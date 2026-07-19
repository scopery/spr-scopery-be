package com.company.scopery.modules.ratecard.ratecardversion.domain.model;

import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RateCardVersion(
        UUID id, UUID rateCardId, int versionNumber, String name, String description,
        LocalDate effectiveFrom, LocalDate effectiveTo, RateCardVersionStatus status,
        Instant publishedAt, UUID publishedBy, Instant archivedAt, UUID archivedBy,
        int version, Instant createdAt, Instant updatedAt
) {
    public static RateCardVersion create(UUID rateCardId, int versionNumber, String name, String description,
                                         LocalDate effectiveFrom, LocalDate effectiveTo) {
        return new RateCardVersion(UUID.randomUUID(), rateCardId, versionNumber, name, description,
                effectiveFrom, effectiveTo, RateCardVersionStatus.DRAFT,
                null, null, null, null, 0, null, null);
    }

    public RateCardVersion update(String name, String description, LocalDate effectiveFrom, LocalDate effectiveTo) {
        return new RateCardVersion(id, rateCardId, versionNumber, name, description, effectiveFrom, effectiveTo,
                status, publishedAt, publishedBy, archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public RateCardVersion publish(UUID actorId) {
        return new RateCardVersion(id, rateCardId, versionNumber, name, description, effectiveFrom, effectiveTo,
                RateCardVersionStatus.PUBLISHED, Instant.now(), actorId, archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public RateCardVersion archive(UUID actorId) {
        return new RateCardVersion(id, rateCardId, versionNumber, name, description, effectiveFrom, effectiveTo,
                RateCardVersionStatus.ARCHIVED, publishedAt, publishedBy, Instant.now(), actorId, version, createdAt, updatedAt);
    }

    public boolean covers(LocalDate date) {
        if (date == null || effectiveFrom == null) return false;
        if (date.isBefore(effectiveFrom)) return false;
        return effectiveTo == null || !date.isAfter(effectiveTo);
    }
}
