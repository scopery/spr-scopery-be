package com.company.scopery.modules.quote.quoteversion.domain.model;

import com.company.scopery.modules.quote.quoteversion.domain.enums.CostBaseMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.QuoteVersionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteVersionLifecycleTest {

    @Test
    void draftIsEditable_submittedApprovedSentAcceptedAreImmutable() {
        QuoteVersion draft = QuoteVersion.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1,
                "Title", "{}", "{}", "VND", new BigDecimal("30"),
                PricingMethod.TARGET_MARGIN_SOLVER, CostBaseMethod.BUDGET_OF_COSTS,
                DiscountMethod.NONE, null, BigDecimal.ZERO, null, null, null, null);
        assertThat(draft.isEditable()).isTrue();
        assertThat(draft.isImmutable()).isFalse();

        UUID actor = UUID.randomUUID();
        QuoteVersion submitted = draft.submit(actor);
        assertThat(submitted.status()).isEqualTo(QuoteVersionStatus.SUBMITTED);
        assertThat(submitted.isImmutable()).isTrue();

        QuoteVersion approved = submitted.approve(actor);
        assertThat(approved.status()).isEqualTo(QuoteVersionStatus.APPROVED);
        assertThat(approved.isImmutable()).isTrue();

        QuoteVersion sent = approved.send(actor);
        assertThat(sent.status()).isEqualTo(QuoteVersionStatus.SENT);
        assertThat(sent.isImmutable()).isTrue();

        QuoteVersion accepted = sent.accept(actor);
        assertThat(accepted.status()).isEqualTo(QuoteVersionStatus.ACCEPTED);
        assertThat(accepted.isImmutable()).isTrue();
    }

    @Test
    void rejectedCanStillBeDuplicatedAsNewDraftViaCreate() {
        QuoteVersion rejected = QuoteVersion.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1,
                "Title", "{}", "{}", "VND", null,
                PricingMethod.FROM_FINANCE_PLANNED_REVENUE, CostBaseMethod.BUDGET_OF_COSTS,
                DiscountMethod.NONE, null, BigDecimal.ZERO, null, null, null, null)
                .submit(UUID.randomUUID())
                .reject(UUID.randomUUID(), "Margin too low");
        assertThat(rejected.status()).isEqualTo(QuoteVersionStatus.REJECTED);
        assertThat(rejected.isEditable()).isFalse();

        QuoteVersion revision = QuoteVersion.create(
                rejected.quoteId(), rejected.projectId(), rejected.financeScenarioId(), 2,
                rejected.titleSnapshot(), rejected.clientSnapshotJson(), rejected.financeSnapshotJson(),
                rejected.currencyCode(), rejected.targetMarginPercent(), rejected.pricingMethod(),
                rejected.costBaseMethod(), rejected.discountMethod(), rejected.discountPercent(),
                rejected.discountAmount(), rejected.discountReason(), rejected.validUntil(),
                rejected.proposalTitle(), rejected.proposalNotes());
        assertThat(revision.status()).isEqualTo(QuoteVersionStatus.DRAFT);
        assertThat(revision.isEditable()).isTrue();
    }
}
