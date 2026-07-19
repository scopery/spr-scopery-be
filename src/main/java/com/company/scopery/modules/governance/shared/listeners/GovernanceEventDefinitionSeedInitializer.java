package com.company.scopery.modules.governance.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(40)
public class GovernanceEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final EventDefinitionRepository repo;
    public GovernanceEventDefinitionSeedInitializer(EventDefinitionRepository repo){this.repo=repo;}
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new Seed("OBJECT_OWNER_ASSIGNED","Object Owner Assigned","Owner assigned to governed object"),
                new Seed("OBJECT_OWNER_REVOKED","Object Owner Revoked","Owner revoked from governed object"),
                new Seed("OBJECT_OWNER_TRANSFERRED","Object Owner Transferred","Ownership transferred to new owner"),
                new Seed("OBJECT_LOCK_CREATED","Object Lock Created","Object locked"),
                new Seed("OBJECT_LOCK_RELEASED","Object Lock Released","Object lock released"),
                new Seed("OBJECT_FINALIZED","Object Finalized","Object finalized via lock"),
                new Seed("OBJECT_UNFINALIZED","Object Unfinalized","Object finalize lock released"),
                new Seed("OBJECT_ACCESS_GRANT_CREATED","Object Access Grant Created","Access grant given to grantee"),
                new Seed("OBJECT_ACCESS_GRANT_REVOKED","Object Access Grant Revoked","Access grant revoked from grantee"),
                new Seed("OBJECT_VERSION_SNAPSHOT_CREATED","Object Version Snapshot Created","Version snapshot recorded"),
                new Seed("OBJECT_RESTORE_REQUESTED","Object Restore Requested","Restore from snapshot requested"),
                new Seed("OBJECT_RESTORE_COMPLETED","Object Restore Completed","Restore from snapshot completed"),
                new Seed("BASELINE_GUARD_BLOCKED","Baseline Guard Blocked","Action blocked by baseline guard policy"),
                new Seed("CLIENT_VISIBLE_FIELD_CHANGED","Client-Visible Field Changed","A client-visible field was modified"),
                new Seed("SENSITIVE_OBJECT_ACCESSED","Sensitive Object Accessed","Sensitive governed object was accessed"))) {
            EventDefinitionSeedSupport.findOrCreate(repo, "SCOPERY_GOVERNANCE", s.code, s.name, s.desc, EventDataClassification.INTERNAL, "GOVERNANCE");
        }
    }
    private record Seed(String code, String name, String desc){}
}
