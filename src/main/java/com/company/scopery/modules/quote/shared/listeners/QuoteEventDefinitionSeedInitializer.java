package com.company.scopery.modules.quote.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(24)
public class QuoteEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SOURCE_SYSTEM = "SCOPERY_QUOTE";
    public static final String OWNER_MODULE = "QUOTE";

    private final EventDefinitionRepository eventDefinitionRepository;

    public QuoteEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository,
                    SOURCE_SYSTEM,
                    seed.code(),
                    seed.name(),
                    seed.description(),
                    EventDataClassification.INTERNAL,
                    OWNER_MODULE);
        }
    }

    private record SeedEvent(String code, String name, String description) {}

    static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("QUOTE_CREATED", "Quote Created", "A quote was created"),
            new SeedEvent("QUOTE_UPDATED", "Quote Updated", "A quote was updated"),
            new SeedEvent("QUOTE_ARCHIVED", "Quote Archived", "A quote was archived"),
            new SeedEvent("QUOTE_VERSION_CREATED", "Quote Version Created", "A quote version was created"),
            new SeedEvent("QUOTE_VERSION_UPDATED", "Quote Version Updated", "A quote version was updated"),
            new SeedEvent("QUOTE_VERSION_DUPLICATED", "Quote Version Duplicated", "A quote version was duplicated"),
            new SeedEvent("QUOTE_VERSION_ARCHIVED", "Quote Version Archived", "A quote version was archived"),
            new SeedEvent("QUOTE_VERSION_MARKED_CURRENT", "Quote Version Marked Current", "A quote version was marked current"),
            new SeedEvent("QUOTE_LINE_CREATED", "Quote Line Created", "A quote line was created"),
            new SeedEvent("QUOTE_LINE_UPDATED", "Quote Line Updated", "A quote line was updated"),
            new SeedEvent("QUOTE_LINE_DELETED", "Quote Line Deleted", "A quote line was deleted"),
            new SeedEvent("QUOTE_LINES_REORDERED", "Quote Lines Reordered", "Quote lines were reordered"),
            new SeedEvent("QUOTE_TERM_CREATED", "Quote Term Created", "A quote term was created"),
            new SeedEvent("QUOTE_TERM_UPDATED", "Quote Term Updated", "A quote term was updated"),
            new SeedEvent("QUOTE_TERM_DELETED", "Quote Term Deleted", "A quote term was deleted"),
            new SeedEvent("QUOTE_TERMS_REORDERED", "Quote Terms Reordered", "Quote terms were reordered"),
            new SeedEvent("QUOTE_TARGET_MARGIN_SOLVED", "Quote Target Margin Solved", "Target margin was solved"),
            new SeedEvent("QUOTE_RECALCULATED", "Quote Recalculated", "Quote summary was recalculated"),
            new SeedEvent("QUOTE_DISCOUNT_UPDATED", "Quote Discount Updated", "Quote discount was updated"),
            new SeedEvent("QUOTE_MARGIN_THRESHOLD_WARNING", "Quote Margin Threshold Warning", "Quote margin threshold warning"),
            new SeedEvent("QUOTE_SUBMITTED", "Quote Submitted", "A quote version was submitted"),
            new SeedEvent("QUOTE_APPROVED", "Quote Approved", "A quote version was approved"),
            new SeedEvent("QUOTE_REJECTED", "Quote Rejected", "A quote version was rejected"),
            new SeedEvent("QUOTE_SENT", "Quote Sent", "A quote version was marked sent"),
            new SeedEvent("QUOTE_ACCEPTED", "Quote Accepted", "A quote version was marked accepted")
    );
}
