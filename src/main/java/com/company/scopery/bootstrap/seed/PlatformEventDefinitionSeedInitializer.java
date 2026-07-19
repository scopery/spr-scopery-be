package com.company.scopery.bootstrap.seed;

import com.company.scopery.common.outbox.OutboxEventCodeValidator;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds Phase 04 platform reliability event definitions (sourceSystem = SCOPERY_PLATFORM).
 * Also exposes {@link OutboxEventCodeValidator} for outbox enqueue monitoring.
 */
@Component
@Order(5)
public class PlatformEventDefinitionSeedInitializer
        implements ApplicationListener<ApplicationReadyEvent>, OutboxEventCodeValidator {

    private static final Logger log = LoggerFactory.getLogger(PlatformEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_PLATFORM";

    private final EventDefinitionRepository eventDefinitionRepository;

    public PlatformEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : PLATFORM_EVENTS) {
            findOrCreate(seed.code(), seed.name(), seed.description());
        }
        log.info("[PlatformEventDefinitionSeed] Platform event seeding complete");
    }

    @Override
    public boolean isKnown(String eventCode) {
        if (eventCode == null || eventCode.isBlank()) {
            return false;
        }
        return eventDefinitionRepository.existsByCode(EventDefinitionCode.of(eventCode));
    }

    private EventDefinition findOrCreate(String code, String name, String description) {
        EventDefinitionCode defCode = EventDefinitionCode.of(code);
        return eventDefinitionRepository.findByCode(defCode).orElseGet(() ->
                eventDefinitionRepository.save(EventDefinition.create(
                        defCode, name,
                        SourceSystemCode.of(SOURCE_SYSTEM),
                        EventKey.of(code),
                        description, null, null)));
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> PLATFORM_EVENTS = List.of(
            new SeedEvent("PLATFORM_OUTBOX_MESSAGE_DEAD_LETTERED",
                    "Platform Outbox Dead Letter",
                    "Fired when an outbox message exceeds max attempts"),
            new SeedEvent("PLATFORM_OUTBOX_MESSAGE_RETRIED",
                    "Platform Outbox Retried",
                    "Fired when an outbox message is retried"),
            new SeedEvent("PLATFORM_IDEMPOTENCY_CONFLICT_DETECTED",
                    "Platform Idempotency Conflict",
                    "Fired when an idempotency key is reused with a different body"),
            new SeedEvent("PLATFORM_JOB_FAILED",
                    "Platform Job Failed",
                    "Fired when a scheduled platform job fails"),
            new SeedEvent("PLATFORM_AUDIT_WRITE_FAILED",
                    "Platform Audit Write Failed",
                    "Fired when immutable audit persistence fails")
    );
}
