package com.company.scopery.modules.reporting.shared.listeners;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinition;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinitionRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(29)
public class ReportingDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final ReportDefinitionRepository definitions;
    public ReportingDefinitionSeedInitializer(ReportDefinitionRepository definitions) { this.definitions = definitions; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (Seed s : SEEDS) {
            if (definitions.findByCode(s.code()).isEmpty()) {
                definitions.save(ReportDefinition.create(s.code(), s.name(), s.description(), "PROJECT", s.type(),
                        "[\"REPORTING_REPORT_VIEW\"]", "[\"CSV\",\"JSON\"]", s.sensitive()));
            }
        }
    }
    private record Seed(String code, String name, String description, String type, String sensitive) {}
    private static final List<Seed> SEEDS = List.of(
            new Seed("PROJECT_DASHBOARD", "Project Dashboard", "Project dashboard summary", "DASHBOARD", "[]"),
            new Seed("PROJECT_HEALTH", "Project Health", "Project health score", "PROJECT_HEALTH", "[]"),
            new Seed("PROJECT_TASK_RISK_REPORT", "Task Risk Report", "Task risk metrics", "TASK_RISK", "[]"),
            new Seed("PROJECT_SCHEDULE_RISK_REPORT", "Schedule Risk Report", "Schedule risk metrics", "SCHEDULE_RISK", "[]"),
            new Seed("PROJECT_CAPACITY_REPORT", "Capacity Report", "Capacity metrics", "CAPACITY", "[]"),
            new Seed("PROJECT_ESTIMATION_REPORT", "Estimation Report", "Estimation metrics", "ESTIMATION", "[\"totalLaborCost\"]"),
            new Seed("PROJECT_FINANCE_REPORT", "Finance Report", "Finance metrics", "FINANCE", "[\"plannedRevenue\",\"grossMargin\"]"),
            new Seed("PROJECT_QUOTE_REPORT", "Quote Report", "Quote metrics", "QUOTE", "[\"totalQuotedAmount\"]"),
            new Seed("PROJECT_BASELINE_VS_CURRENT_REPORT", "Baseline vs Current", "Baseline comparison", "BASELINE_VS_CURRENT", "[]"),
            new Seed("PROJECT_CHANGE_IMPACT_REPORT", "Change Impact Report", "Change request impact", "CHANGE_IMPACT", "[\"totalCostImpact\"]"),
            new Seed("PROJECT_NOTIFICATION_ATTENTION_REPORT", "Notification Attention", "Notification attention", "NOTIFICATION", "[]"),
            new Seed("PROJECT_AI_PLANNING_REPORT", "AI Planning Report", "AI planning metrics", "AI_PLANNING", "[]")
    );
}
