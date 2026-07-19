package com.company.scopery.modules.notification.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds Phase 05 Notification event contracts (sourceSystem = SCOPERY_NOTIFICATION).
 * Full notification engine ownership remains Phase 06.
 */
@Component
@Order(13)
public class NotificationEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_NOTIFICATION";
    public static final String OWNER_MODULE = "NOTIFICATION";

    private final EventDefinitionRepository eventDefinitionRepository;

    public NotificationEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
        log.info("[NotificationEventDefinitionSeed] Notification event seeding complete ({} events)", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("EMAIL_TEMPLATE_CREATED", "Email Template Created", "Email template created"),
            new SeedEvent("EMAIL_TEMPLATE_VERSION_PUBLISHED", "Email Template Version Published", "Template version published"),
            new SeedEvent("EMAIL_RULE_CREATED", "Email Rule Created", "Email rule created"),
            new SeedEvent("EMAIL_RULE_ENABLED", "Email Rule Enabled", "Email rule enabled"),
            new SeedEvent("EMAIL_RULE_DISABLED", "Email Rule Disabled", "Email rule disabled"),
            new SeedEvent("EMAIL_OUTBOX_ENQUEUED", "Email Outbox Enqueued", "Email outbox message enqueued"),
            new SeedEvent("EMAIL_DELIVERY_SENT", "Email Delivery Sent", "Email delivery sent"),
            new SeedEvent("EMAIL_DELIVERY_FAILED", "Email Delivery Failed", "Email delivery failed"),
            new SeedEvent("EMAIL_DELIVERY_RETRIED", "Email Delivery Retried", "Email delivery retried"),
            new SeedEvent("EMAIL_DELIVERY_DEAD_LETTERED", "Email Delivery Dead Lettered", "Email outbox moved to dead letter"),
            new SeedEvent("NOTIFICATION_ITEM_CREATED", "Notification Item Created", "In-app notification item created"),
            new SeedEvent("NOTIFICATION_DEDUPLICATED", "Notification Deduplicated", "Duplicate notification skipped"),
            new SeedEvent("NOTIFICATION_SENSITIVE_VARIABLE_MASKED", "Notification Sensitive Variable Masked",
                    "Sensitive variables masked during notification dispatch")
    );
}
