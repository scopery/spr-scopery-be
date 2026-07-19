package com.company.scopery.modules.productivity.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(38)
public class ProductivityEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public ProductivityEventDefinitionSeedInitializer(EventDefinitionRepository repo) { this.repo = repo; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("GLOBAL_SEARCH_EXECUTED","Global Search Executed","User executed global search"),
                new Seed("SAVED_SEARCH_CREATED","Saved Search Created","Saved search created"),
                new Seed("FAVORITE_CREATED","Favorite Created","Favorite created"),
                new Seed("WORK_INBOX_ITEM_READ","Work Inbox Item Read","Inbox item marked read")
        )) {
            EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_PRODUCTIVITY", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "PRODUCTIVITY");
        }
    }
    private record Seed(String code, String name, String desc) {}
}
