package com.company.scopery.modules.trust.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener; import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(42)
public class TrustEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public TrustEventDefinitionSeedInitializer(EventDefinitionRepository repo){this.repo=repo;}
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("PRIVACY_REQUEST_CREATED","Privacy Request Created","Privacy request created"),
                new Seed("LEGAL_HOLD_CREATED","Legal Hold Created","Legal hold created"),
                new Seed("LEGAL_HOLD_RELEASED","Legal Hold Released","Legal hold released"),
                new Seed("COMPLIANCE_EVIDENCE_FINALIZED","Compliance Evidence Finalized","Evidence finalized"),
                new Seed("SENSITIVE_ACCESS_RECORDED","Sensitive Access Recorded","Sensitive access logged"))) {
            EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_TRUST", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "TRUST");
        }
    }
    private record Seed(String code, String name, String desc){}
}
