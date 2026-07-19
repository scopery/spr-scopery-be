package com.company.scopery.modules.quote.calculation;

import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuoteRecalculator {

    private final QuoteCalculationService calculationService;
    private final FinanceSnapshotBuilder snapshotBuilder;
    private final QuoteLineRepository lines;
    private final QuoteSummaryRepository summaries;

    public QuoteRecalculator(QuoteCalculationService calculationService,
                             FinanceSnapshotBuilder snapshotBuilder,
                             QuoteLineRepository lines,
                             QuoteSummaryRepository summaries) {
        this.calculationService = calculationService;
        this.snapshotBuilder = snapshotBuilder;
        this.lines = lines;
        this.summaries = summaries;
    }

    public QuoteSummary recalculateAndSave(QuoteVersion version) {
        List<QuoteLine> versionLines = lines.findByQuoteVersionId(version.id());
        var finance = snapshotBuilder.parseAmounts(version.financeSnapshotJson());
        QuoteSummary computed = calculationService.recalculate(version, versionLines, finance);
        return summaries.findByQuoteVersionId(version.id())
                .map(existing -> summaries.save(computed.withId(existing.id())))
                .orElseGet(() -> summaries.save(computed));
    }
}
