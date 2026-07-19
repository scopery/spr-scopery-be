package com.company.scopery.modules.project.scheduling.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component @Order(17)
public class SchedulingEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM="SCOPERY_SCHEDULING";
    public static final String OWNER_MODULE="SCHEDULING";
    private final EventDefinitionRepository repository;
    public SchedulingEventDefinitionSeedInitializer(EventDefinitionRepository repository){this.repository=repository;}
    @Override @Transactional public void onApplicationEvent(ApplicationReadyEvent event){
        EVENTS.forEach(code->EventDefinitionSeedSupport.findOrCreate(repository,SOURCE_SYSTEM,code,
                title(code),title(code)+" event",EventDataClassification.INTERNAL,OWNER_MODULE));
    }
    private static String title(String code){String s=code.toLowerCase().replace('_',' ');return Character.toUpperCase(s.charAt(0))+s.substring(1);}
    private static final List<String> EVENTS=List.of(
            "SCHEDULE_RUN_CREATED","SCHEDULE_RUN_STARTED","SCHEDULE_RUN_COMPLETED","SCHEDULE_RUN_FAILED","SCHEDULE_RUN_CANCELLED",
            "TASK_SCHEDULE_CREATED","TASK_SCHEDULE_UPDATED","TASK_SCHEDULE_AT_RISK","TASK_SCHEDULE_UNSCHEDULED",
            "SCHEDULE_DAILY_WORK_CREATED","SCHEDULING_ISSUE_CREATED","TASK_DUE_DATE_CAPACITY_GAP_DETECTED",
            "RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED");
}
