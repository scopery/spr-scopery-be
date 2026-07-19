package com.company.scopery.modules.governance.objecttype.application.listeners;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectType;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectTypeRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(41)
public class GovernedObjectTypeSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final GovernedObjectTypeRepository repo;
    public GovernedObjectTypeSeedInitializer(GovernedObjectTypeRepository repo) { this.repo = repo; }

    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : seeds()) {
            if (!repo.existsByCode(s.code())) {
                repo.save(GovernedObjectType.create(s.code(), s.ownership(), s.versioning(), s.locking(), s.restore()));
            }
        }
    }

    private List<Seed> seeds() {
        return List.of(
            new Seed("PROJECT",                true,  true,  true,  true),
            new Seed("TASK",                   true,  true,  false, true),
            new Seed("QUOTE_VERSION",          true,  true,  true,  false),
            new Seed("CHANGE_REQUEST",         true,  true,  false, false),
            new Seed("BASELINE",               true,  true,  true,  false),
            new Seed("SCOPE_PACKAGE",          true,  true,  true,  true),
            new Seed("SCOPE_ITEM",             true,  true,  false, true),
            new Seed("DELIVERABLE",            true,  true,  true,  true),
            new Seed("DELIVERABLE_ACCEPTANCE", false, true,  true,  false),
            new Seed("REQUIREMENT",            true,  true,  true,  true),
            new Seed("REQUIREMENT_VERSION",    false, true,  true,  false),
            new Seed("DOCUMENT",               true,  true,  true,  true),
            new Seed("DOCUMENT_VERSION",       false, true,  true,  false),
            new Seed("RAID_ITEM",              true,  true,  true,  true),
            new Seed("DECISION_RECORD",        true,  true,  true,  true),
            new Seed("TEST_PLAN",              true,  true,  true,  true),
            new Seed("TEST_CASE",              true,  true,  false, true),
            new Seed("TEST_RUN",               false, true,  false, false),
            new Seed("DEFECT",                 true,  true,  false, true),
            new Seed("RELEASE_PACKAGE",        true,  true,  true,  false),
            new Seed("DEPLOYMENT_RECORD",      false, true,  true,  false),
            new Seed("MEETING_MINUTES",        true,  true,  true,  true),
            new Seed("CUSTOM_FORM_DEFINITION", true,  true,  true,  true),
            new Seed("CUSTOM_FORM_VERSION",    false, true,  true,  false),
            new Seed("CUSTOM_FIELD_DEFINITION",true,  true,  false, true),
            new Seed("EXTERNAL_CONTACT",       true,  false, false, false),
            new Seed("CLIENT_FEEDBACK",        false, true,  false, false)
        );
    }

    private record Seed(String code, boolean ownership, boolean versioning, boolean locking, boolean restore) {}
}
