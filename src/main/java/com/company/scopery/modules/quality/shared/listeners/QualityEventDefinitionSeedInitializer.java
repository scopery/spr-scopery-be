package com.company.scopery.modules.quality.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component
@Order(32)
public class QualityEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_QUALITY_RELEASE";
    public static final String OWNER_MODULE = "QUALITY";
    private final EventDefinitionRepository eventDefinitionRepository;
    public QualityEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(eventDefinitionRepository, SOURCE_SYSTEM, seed.code(), seed.name(),
                    seed.description(), EventDataClassification.INTERNAL, OWNER_MODULE);
        }
    }
    private record SeedEvent(String code, String name, String description) {}
    static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("QUALITY_PLAN_CREATED", "Quality Plan Created", "Quality plan created"),
            new SeedEvent("QUALITY_PLAN_APPROVED", "Quality Plan Approved", "Quality plan approved"),
            new SeedEvent("QUALITY_PLAN_MARKED_CURRENT", "Quality Plan Marked Current", "Quality plan marked current"),
            new SeedEvent("TEST_PLAN_CREATED", "Test Plan Created", "Test plan created"),
            new SeedEvent("TEST_PLAN_APPROVED", "Test Plan Approved", "Test plan approved"),
            new SeedEvent("TEST_CASE_CREATED", "Test Case Created", "Test case created"),
            new SeedEvent("TEST_CASE_APPROVED", "Test Case Approved", "Test case approved"),
            new SeedEvent("TEST_RUN_CREATED", "Test Run Created", "Test run created"),
            new SeedEvent("TEST_RUN_STARTED", "Test Run Started", "Test run started"),
            new SeedEvent("TEST_RUN_COMPLETED", "Test Run Completed", "Test run completed"),
            new SeedEvent("TEST_CASE_RESULT_RECORDED", "Test Case Result Recorded", "Test case result recorded"),
            new SeedEvent("DEFECT_CREATED", "Defect Created", "Defect created"),
            new SeedEvent("DEFECT_TRIAGED", "Defect Triaged", "Defect triaged"),
            new SeedEvent("DEFECT_ASSIGNED", "Defect Assigned", "Defect assigned"),
            new SeedEvent("DEFECT_FIXED", "Defect Fixed", "Defect fixed"),
            new SeedEvent("DEFECT_VERIFIED", "Defect Verified", "Defect verified"),
            new SeedEvent("DEFECT_CLOSED", "Defect Closed", "Defect closed"),
            new SeedEvent("DEFECT_REOPENED", "Defect Reopened", "Defect reopened"),
            new SeedEvent("RELEASE_PACKAGE_CREATED", "Release Package Created", "Release package created"),
            new SeedEvent("RELEASE_READINESS_CHECKED", "Release Readiness Checked", "Release readiness checked"),
            new SeedEvent("RELEASE_READY", "Release Ready", "Release marked ready"),
            new SeedEvent("RELEASE_RELEASED", "Release Released", "Release marked released"),
            new SeedEvent("RELEASE_ROLLED_BACK", "Release Rolled Back", "Release rolled back"),
            new SeedEvent("DEPLOYMENT_RECORD_CREATED", "Deployment Record Created", "Deployment record created"),
            new SeedEvent("DEPLOYMENT_SUCCEEDED", "Deployment Succeeded", "Deployment succeeded"),
            new SeedEvent("DEPLOYMENT_FAILED", "Deployment Failed", "Deployment failed"),
            new SeedEvent("ROLLBACK_PLAN_APPROVED", "Rollback Plan Approved", "Rollback plan approved")
    );
}
