package com.company.scopery.modules.quote.quoteversion.domain.model;

import com.company.scopery.modules.quote.quoteversion.domain.enums.CostBaseMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.QuoteVersionStatus;
import com.company.scopery.modules.quote.shared.constant.QuoteFormulaVersions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record QuoteVersion(
        UUID id,
        UUID quoteId,
        UUID projectId,
        UUID financeScenarioId,
        int versionNumber,
        QuoteVersionStatus status,
        String titleSnapshot,
        String clientSnapshotJson,
        String financeSnapshotJson,
        String formulaVersion,
        String currencyCode,
        BigDecimal targetMarginPercent,
        PricingMethod pricingMethod,
        CostBaseMethod costBaseMethod,
        boolean currentFlag,
        DiscountMethod discountMethod,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        String discountReason,
        LocalDate validUntil,
        String proposalTitle,
        String proposalNotes,
        Instant submittedAt,
        UUID submittedBy,
        Instant approvedAt,
        UUID approvedBy,
        Instant rejectedAt,
        UUID rejectedBy,
        String rejectionReason,
        Instant sentAt,
        UUID sentBy,
        Instant acceptedAt,
        UUID acceptedBy,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static QuoteVersion create(
            UUID quoteId,
            UUID projectId,
            UUID financeScenarioId,
            int versionNumber,
            String titleSnapshot,
            String clientSnapshotJson,
            String financeSnapshotJson,
            String currencyCode,
            BigDecimal targetMarginPercent,
            PricingMethod pricingMethod,
            CostBaseMethod costBaseMethod,
            DiscountMethod discountMethod,
            BigDecimal discountPercent,
            BigDecimal discountAmount,
            String discountReason,
            LocalDate validUntil,
            String proposalTitle,
            String proposalNotes) {
        return new QuoteVersion(
                UUID.randomUUID(), quoteId, projectId, financeScenarioId, versionNumber,
                QuoteVersionStatus.DRAFT, titleSnapshot, clientSnapshotJson, financeSnapshotJson,
                QuoteFormulaVersions.QUOTE_V1, currencyCode, targetMarginPercent, pricingMethod,
                costBaseMethod, false,
                discountMethod == null ? DiscountMethod.NONE : discountMethod,
                discountPercent, discountAmount == null ? BigDecimal.ZERO : discountAmount,
                discountReason, validUntil, proposalTitle, proposalNotes,
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, 0, null, null);
    }

    public boolean isDraft() {
        return status == QuoteVersionStatus.DRAFT;
    }

    public boolean isEditable() {
        return status == QuoteVersionStatus.DRAFT;
    }

    public boolean isImmutable() {
        return status == QuoteVersionStatus.SUBMITTED
                || status == QuoteVersionStatus.APPROVED
                || status == QuoteVersionStatus.SENT
                || status == QuoteVersionStatus.ACCEPTED;
    }

    public QuoteVersion updateDraft(
            BigDecimal targetMarginPercent,
            PricingMethod pricingMethod,
            CostBaseMethod costBaseMethod,
            DiscountMethod discountMethod,
            BigDecimal discountPercent,
            BigDecimal discountAmount,
            String discountReason,
            LocalDate validUntil,
            String proposalTitle,
            String proposalNotes) {
        return new QuoteVersion(
                id, quoteId, projectId, financeScenarioId, versionNumber, status, titleSnapshot,
                clientSnapshotJson, financeSnapshotJson, formulaVersion, currencyCode,
                targetMarginPercent, pricingMethod, costBaseMethod, currentFlag,
                discountMethod, discountPercent, discountAmount, discountReason, validUntil,
                proposalTitle, proposalNotes, submittedAt, submittedBy, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, acceptedAt, acceptedBy,
                archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public QuoteVersion withCurrentFlag(boolean current) {
        return copyStatus(status, current, submittedAt, submittedBy, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, acceptedAt, acceptedBy,
                archivedAt, archivedBy);
    }

    public QuoteVersion submit(UUID actorId) {
        return copyStatus(QuoteVersionStatus.SUBMITTED, currentFlag,
                Instant.now(), actorId, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, acceptedAt, acceptedBy,
                archivedAt, archivedBy);
    }

    public QuoteVersion approve(UUID actorId) {
        return copyStatus(QuoteVersionStatus.APPROVED, currentFlag,
                submittedAt, submittedBy, Instant.now(), actorId,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, acceptedAt, acceptedBy,
                archivedAt, archivedBy);
    }

    public QuoteVersion reject(UUID actorId, String reason) {
        return copyStatus(QuoteVersionStatus.REJECTED, currentFlag,
                submittedAt, submittedBy, approvedAt, approvedBy,
                Instant.now(), actorId, reason, sentAt, sentBy, acceptedAt, acceptedBy,
                archivedAt, archivedBy);
    }

    public QuoteVersion send(UUID actorId) {
        return copyStatus(QuoteVersionStatus.SENT, currentFlag,
                submittedAt, submittedBy, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, Instant.now(), actorId, acceptedAt, acceptedBy,
                archivedAt, archivedBy);
    }

    public QuoteVersion accept(UUID actorId) {
        return copyStatus(QuoteVersionStatus.ACCEPTED, currentFlag,
                submittedAt, submittedBy, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, Instant.now(), actorId,
                archivedAt, archivedBy);
    }

    public QuoteVersion archive(UUID actorId) {
        return copyStatus(QuoteVersionStatus.ARCHIVED, false,
                submittedAt, submittedBy, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, acceptedAt, acceptedBy,
                Instant.now(), actorId);
    }

    private QuoteVersion copyStatus(
            QuoteVersionStatus newStatus,
            boolean current,
            Instant submittedAt, UUID submittedBy,
            Instant approvedAt, UUID approvedBy,
            Instant rejectedAt, UUID rejectedBy, String rejectionReason,
            Instant sentAt, UUID sentBy,
            Instant acceptedAt, UUID acceptedBy,
            Instant archivedAt, UUID archivedBy) {
        return new QuoteVersion(
                id, quoteId, projectId, financeScenarioId, versionNumber, newStatus, titleSnapshot,
                clientSnapshotJson, financeSnapshotJson, formulaVersion, currencyCode,
                targetMarginPercent, pricingMethod, costBaseMethod, current,
                discountMethod, discountPercent, discountAmount, discountReason, validUntil,
                proposalTitle, proposalNotes, submittedAt, submittedBy, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, sentAt, sentBy, acceptedAt, acceptedBy,
                archivedAt, archivedBy, version, createdAt, updatedAt);
    }
}
