package com.company.scopery.modules.scope.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(30)
public class ScopeEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_SCOPE";
    private final EventDefinitionRepository repo;
    public ScopeEventDefinitionSeedInitializer(EventDefinitionRepository repo){ this.repo=repo; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new String[]{"SCOPE_PACKAGE_CREATED","Scope Package Created","Scope package created"},
                new String[]{"SCOPE_PACKAGE_APPROVED","Scope Package Approved","Scope package approved"},
                new String[]{"DELIVERABLE_CREATED","Deliverable Created","Deliverable created"},
                new String[]{"DELIVERABLE_ACCEPTED","Deliverable Accepted","Deliverable formally accepted"},
                new String[]{"DELIVERABLE_REOPENED","Deliverable Reopened","Accepted deliverable reopened"}
        )) {
            EventDefinitionSeedSupport.findOrCreate(repo, SOURCE_SYSTEM, s[0], s[1], s[2], EventDataClassification.INTERNAL, "SCOPE");
        }
    }
}
