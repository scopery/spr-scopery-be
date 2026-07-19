package com.company.scopery.modules.reporting.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(29)
public class ReportingEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_REPORTING";
    private final EventDefinitionRepository repo;
    public ReportingEventDefinitionSeedInitializer(EventDefinitionRepository repo) { this.repo = repo; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (var s : List.of(
                new String[]{"REPORT_RUN_CREATED", "Report Run Created", "Report run created"},
                new String[]{"REPORT_RUN_COMPLETED", "Report Run Completed", "Report run completed"},
                new String[]{"REPORT_EXPORT_CREATED", "Report Export Created", "Report export created"},
                new String[]{"REPORT_EXPORT_COMPLETED", "Report Export Completed", "Report export completed"}
        )) {
            EventDefinitionSeedSupport.findOrCreate(repo, SOURCE_SYSTEM, s[0], s[1], s[2], EventDataClassification.INTERNAL, "REPORTING");
        }
    }
}
