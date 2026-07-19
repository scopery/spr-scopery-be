package com.company.scopery.modules.clientportal.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent; import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Component @Order(36)
public class ClientPortalEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public ClientPortalEventDefinitionSeedInitializer(EventDefinitionRepository repo){this.repo=repo;}
    @Override @Transactional public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("CLIENT_REVIEW_CREATED","Client Review Created","Review created"),
                new Seed("CLIENT_REVIEW_DECIDED","Client Review Decided","Review decided"),
                new Seed("PORTAL_ACCESS_GRANTED","Portal Access Granted","Access granted"),
                new Seed("PORTAL_INVITE_SENT","Portal Invite Sent","Invite sent")
        )) EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_CLIENT_PORTAL", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "CLIENT_PORTAL");
    }
    private record Seed(String code, String name, String desc){}
}
