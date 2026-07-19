package com.company.scopery.modules.configuration.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(39)
public class ConfigurationEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public ConfigurationEventDefinitionSeedInitializer(EventDefinitionRepository repo) { this.repo = repo; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(new Seed("CUSTOM_FIELD_CREATED","Custom Field Created","Custom field created"),
                new Seed("CUSTOM_FIELD_VALUE_UPDATED","Custom Field Value Updated","Custom field value updated"))) {
            EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_CONFIGURATION", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "CONFIGURATION");
        }
    }
    private record Seed(String code, String name, String desc) {}
}
