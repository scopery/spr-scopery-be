package com.company.scopery.modules.externalparty.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent; import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Component @Order(35)
public class ExternalPartyEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public ExternalPartyEventDefinitionSeedInitializer(EventDefinitionRepository repo){this.repo=repo;}
    @Override @Transactional public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("EXTERNAL_ORGANIZATION_CREATED","External Organization Created","Org created"),
                new Seed("EXTERNAL_CONTACT_CREATED","External Contact Created","Contact created"),
                new Seed("PROJECT_STAKEHOLDER_CREATED","Project Stakeholder Created","Stakeholder created")
        )) EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_EXTERNAL_PARTY", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "EXTERNAL_PARTY");
    }
    private record Seed(String code, String name, String desc){}
}
