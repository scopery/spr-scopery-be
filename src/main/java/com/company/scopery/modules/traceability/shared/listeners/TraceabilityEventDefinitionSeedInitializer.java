package com.company.scopery.modules.traceability.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent; import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Component @Order(34)
public class TraceabilityEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public TraceabilityEventDefinitionSeedInitializer(EventDefinitionRepository repo){this.repo=repo;}
    @Override @Transactional public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("REQUIREMENT_CREATED","Requirement Created","Requirement created"),
                new Seed("REQUIREMENT_APPROVED","Requirement Approved","Requirement approved"),
                new Seed("TRACE_LINK_CREATED","Trace Link Created","Trace link created")
        )) EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_TRACEABILITY", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "TRACEABILITY");
    }
    private record Seed(String code, String name, String desc){}
}
