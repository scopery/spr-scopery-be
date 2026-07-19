package com.company.scopery.modules.quote.quoteversion.infrastructure.mapper;

import com.company.scopery.modules.quote.quoteversion.domain.enums.CostBaseMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.QuoteVersionStatus;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.infrastructure.persistence.QuoteVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class QuoteVersionPersistenceMapper {
    public QuoteVersion toDomain(QuoteVersionJpaEntity e) {
        return new QuoteVersion(
                e.getId(), e.getQuoteId(), e.getProjectId(), e.getFinanceScenarioId(),
                e.getVersionNumber() == null ? 1 : e.getVersionNumber(),
                QuoteVersionStatus.valueOf(e.getStatus()), e.getTitleSnapshot(),
                e.getClientSnapshotJson(), e.getFinanceSnapshotJson(), e.getFormulaVersion(),
                e.getCurrencyCode(), e.getTargetMarginPercent(),
                PricingMethod.valueOf(e.getPricingMethod()),
                CostBaseMethod.valueOf(e.getCostBaseMethod()),
                Boolean.TRUE.equals(e.getCurrentFlag()),
                DiscountMethod.valueOf(e.getDiscountMethod()),
                e.getDiscountPercent(), e.getDiscountAmount(), e.getDiscountReason(),
                e.getValidUntil(), e.getProposalTitle(), e.getProposalNotes(),
                e.getSubmittedAt(), e.getSubmittedBy(), e.getApprovedAt(), e.getApprovedBy(),
                e.getRejectedAt(), e.getRejectedBy(), e.getRejectionReason(),
                e.getSentAt(), e.getSentBy(), e.getAcceptedAt(), e.getAcceptedBy(),
                e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public QuoteVersionJpaEntity toJpaEntity(QuoteVersion d) {
        QuoteVersionJpaEntity e = new QuoteVersionJpaEntity();
        e.setId(d.id());
        e.setQuoteId(d.quoteId());
        e.setProjectId(d.projectId());
        e.setFinanceScenarioId(d.financeScenarioId());
        e.setVersionNumber(d.versionNumber());
        e.setStatus(d.status().name());
        e.setTitleSnapshot(d.titleSnapshot());
        e.setClientSnapshotJson(d.clientSnapshotJson());
        e.setFinanceSnapshotJson(d.financeSnapshotJson());
        e.setFormulaVersion(d.formulaVersion());
        e.setCurrencyCode(d.currencyCode());
        e.setTargetMarginPercent(d.targetMarginPercent());
        e.setPricingMethod(d.pricingMethod().name());
        e.setCostBaseMethod(d.costBaseMethod().name());
        e.setCurrentFlag(d.currentFlag());
        e.setDiscountMethod(d.discountMethod().name());
        e.setDiscountPercent(d.discountPercent());
        e.setDiscountAmount(d.discountAmount());
        e.setDiscountReason(d.discountReason());
        e.setValidUntil(d.validUntil());
        e.setProposalTitle(d.proposalTitle());
        e.setProposalNotes(d.proposalNotes());
        e.setSubmittedAt(d.submittedAt());
        e.setSubmittedBy(d.submittedBy());
        e.setApprovedAt(d.approvedAt());
        e.setApprovedBy(d.approvedBy());
        e.setRejectedAt(d.rejectedAt());
        e.setRejectedBy(d.rejectedBy());
        e.setRejectionReason(d.rejectionReason());
        e.setSentAt(d.sentAt());
        e.setSentBy(d.sentBy());
        e.setAcceptedAt(d.acceptedAt());
        e.setAcceptedBy(d.acceptedBy());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
