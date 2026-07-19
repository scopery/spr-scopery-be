package com.company.scopery.modules.profitability.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(41)
public class ProfitabilityEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public ProfitabilityEventDefinitionSeedInitializer(EventDefinitionRepository repo){this.repo=repo;}
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(new Seed("PROFITABILITY_PROFILE_CREATED","Profitability Profile Created","Profile created"),
                new Seed("PROFITABILITY_STATUS_CHANGED","Profitability Status Changed","Status changed"),
                new Seed("PROFITABILITY_MARGIN_THRESHOLD_BREACHED","Margin Threshold Breached","Margin threshold breached"))) {
            EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_PROFITABILITY", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "PROFITABILITY");
        }
    }
    private record Seed(String code, String name, String desc){}
}
