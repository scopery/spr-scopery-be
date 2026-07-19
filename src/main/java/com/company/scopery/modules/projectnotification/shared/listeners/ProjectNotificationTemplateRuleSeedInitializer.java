package com.company.scopery.modules.projectnotification.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;
import com.company.scopery.modules.notification.emailtemplate.domain.valueobject.EmailTemplateCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Order(27)
public class ProjectNotificationTemplateRuleSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectNotificationTemplateRuleSeedInitializer.class);

    private final EmailTemplateRepository templateRepository;
    private final EmailRuleRepository ruleRepository;
    private final EventDefinitionRepository eventDefinitionRepository;

    public ProjectNotificationTemplateRuleSeedInitializer(
            EmailTemplateRepository templateRepository,
            EmailRuleRepository ruleRepository,
            EventDefinitionRepository eventDefinitionRepository) {
        this.templateRepository = templateRepository;
        this.ruleRepository = ruleRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seed("PROJECT_TASK_ASSIGNED_EMAIL", "Task Assigned", "TASK_ASSIGNED",
                "Task assigned: {{task.title}}",
                "<p>You were assigned to task <strong>{{task.title}}</strong> in project {{project.name}}.</p>",
                "You were assigned to task {{task.title}} in project {{project.name}}.",
                EmailRecipientStrategy.TASK_ASSIGNEE, false, "{\"excludeActor\":true}");

        seed("PROJECT_TASK_DUE_SOON_EMAIL", "Task Due Soon", "PROJECT_TASK_DUE_SOON",
                "Task due soon: {{task.title}}",
                "<p>Task <strong>{{task.title}}</strong> is due soon ({{dueDate}}).</p>",
                "Task {{task.title}} is due soon ({{dueDate}}).",
                EmailRecipientStrategy.TASK_ASSIGNEE, false, null);

        seed("PROJECT_TASK_OVERDUE_EMAIL", "Task Overdue", "PROJECT_TASK_OVERDUE",
                "Task overdue: {{task.title}}",
                "<p>Task <strong>{{task.title}}</strong> is overdue ({{dueDate}}).</p>",
                "Task {{task.title}} is overdue ({{dueDate}}).",
                EmailRecipientStrategy.TASK_ASSIGNEE, true, null);

        seed("PROJECT_TASK_AT_RISK_EMAIL", "Task At Risk", "TASK_SCHEDULE_AT_RISK",
                "Task at risk: {{task.title}}",
                "<p>Task <strong>{{task.title}}</strong> is at schedule risk.</p>",
                "Task {{task.title}} is at schedule risk.",
                EmailRecipientStrategy.PROJECT_WATCHERS, true, null);

        seed("PROJECT_SCHEDULE_RUN_FAILED_EMAIL", "Schedule Run Failed", "SCHEDULE_RUN_FAILED",
                "Schedule run failed for project {{project.name}}",
                "<p>A schedule run failed for project <strong>{{project.name}}</strong>.</p>",
                "A schedule run failed for project {{project.name}}.",
                EmailRecipientStrategy.PROJECT_OWNER, true, null);

        seed("PROJECT_BASELINE_APPROVED_EMAIL", "Baseline Approved", "PROJECT_BASELINE_APPROVED",
                "Baseline approved for {{project.name}}",
                "<p>A project baseline was approved for <strong>{{project.name}}</strong>.</p>",
                "A project baseline was approved for {{project.name}}.",
                EmailRecipientStrategy.PROJECT_WATCHERS, false, null);

        seed("CHANGE_REQUEST_SUBMITTED_EMAIL", "Change Request Submitted", "CHANGE_REQUEST_SUBMITTED",
                "Change request submitted for {{project.name}}",
                "<p>A change request was submitted for project <strong>{{project.name}}</strong>.</p>",
                "A change request was submitted for project {{project.name}}.",
                EmailRecipientStrategy.CHANGE_WATCHERS, true, null);

        seed("CHANGE_REQUEST_APPROVED_EMAIL", "Change Request Approved", "CHANGE_REQUEST_APPROVED",
                "Change request approved for {{project.name}}",
                "<p>A change request was approved for project <strong>{{project.name}}</strong>.</p>",
                "A change request was approved for project {{project.name}}.",
                EmailRecipientStrategy.CHANGE_WATCHERS, false, null);

        seed("CHANGE_REQUEST_REJECTED_EMAIL", "Change Request Rejected", "CHANGE_REQUEST_REJECTED",
                "Change request rejected for {{project.name}}",
                "<p>A change request was rejected for project <strong>{{project.name}}</strong>.</p>",
                "A change request was rejected for project {{project.name}}.",
                EmailRecipientStrategy.CHANGE_WATCHERS, false, null);

        seed("CHANGE_REQUEST_APPLIED_EMAIL", "Change Request Applied", "CHANGE_REQUEST_APPLIED",
                "Change request applied for {{project.name}}",
                "<p>A change request was applied for project <strong>{{project.name}}</strong>.</p>",
                "A change request was applied for project {{project.name}}.",
                EmailRecipientStrategy.PROJECT_WATCHERS, false, null);

        seed("QUOTE_SUBMITTED_EMAIL", "Quote Submitted", "QUOTE_SUBMITTED",
                "Quote submitted for {{project.name}}",
                "<p>A quote was submitted for project <strong>{{project.name}}</strong>.</p>",
                "A quote was submitted for project {{project.name}}.",
                EmailRecipientStrategy.QUOTE_WATCHERS, true, null);

        seed("QUOTE_APPROVED_EMAIL", "Quote Approved", "QUOTE_APPROVED",
                "Quote approved for {{project.name}}",
                "<p>A quote was approved for project <strong>{{project.name}}</strong>.</p>",
                "A quote was approved for project {{project.name}}.",
                EmailRecipientStrategy.QUOTE_WATCHERS, false, null);

        seed("PROJECT_FINANCE_SCENARIO_APPROVED_EMAIL", "Finance Scenario Approved", "PROJECT_FINANCE_SCENARIO_APPROVED",
                "Finance scenario approved for {{project.name}}",
                "<p>A finance scenario was approved for project <strong>{{project.name}}</strong>.</p>",
                "A finance scenario was approved for project {{project.name}}.",
                EmailRecipientStrategy.FINANCE_WATCHERS, false, null);

        seed("PROJECT_MARGIN_WARNING_EMAIL", "Margin Warning", "PROJECT_MARGIN_THRESHOLD_WARNING",
                "Finance warning for {{project.name}}",
                "<p>A finance warning was detected for project <strong>{{project.name}}</strong>. Open finance if you have access.</p>",
                "A finance warning was detected for project {{project.name}}.",
                EmailRecipientStrategy.FINANCE_WATCHERS, true, null);

        // In-app oriented templates (same engine; shorter body)
        seed("PROJECT_TASK_ASSIGNED_INAPP", "Task Assigned In-App", "TASK_ASSIGNED",
                "Task assigned: {{task.title}}",
                "<p>Assigned: {{task.title}}</p>",
                "Assigned: {{task.title}}",
                EmailRecipientStrategy.TASK_ASSIGNEE, false, "{\"excludeActor\":true}");
        seed("PROJECT_TASK_DUE_SOON_INAPP", "Task Due Soon In-App", "PROJECT_TASK_DUE_SOON",
                "Due soon: {{task.title}}", "<p>Due soon: {{task.title}}</p>", "Due soon: {{task.title}}",
                EmailRecipientStrategy.TASK_ASSIGNEE, false, null);
        seed("PROJECT_TASK_OVERDUE_INAPP", "Task Overdue In-App", "PROJECT_TASK_OVERDUE",
                "Overdue: {{task.title}}", "<p>Overdue: {{task.title}}</p>", "Overdue: {{task.title}}",
                EmailRecipientStrategy.TASK_ASSIGNEE, true, null);
        seed("PROJECT_TASK_AT_RISK_INAPP", "Task At Risk In-App", "TASK_SCHEDULE_AT_RISK",
                "At risk: {{task.title}}", "<p>At risk: {{task.title}}</p>", "At risk: {{task.title}}",
                EmailRecipientStrategy.PROJECT_WATCHERS, true, null);
        seed("CHANGE_REQUEST_SUBMITTED_INAPP", "CR Submitted In-App", "CHANGE_REQUEST_SUBMITTED",
                "Change request submitted", "<p>Change request submitted for {{project.name}}</p>",
                "Change request submitted for {{project.name}}",
                EmailRecipientStrategy.CHANGE_WATCHERS, true, null);
        seed("QUOTE_APPROVED_INAPP", "Quote Approved In-App", "QUOTE_APPROVED",
                "Quote approved", "<p>Quote approved for {{project.name}}</p>",
                "Quote approved for {{project.name}}",
                EmailRecipientStrategy.QUOTE_WATCHERS, false, null);
        seed("PROJECT_BASELINE_APPROVED_INAPP", "Baseline Approved In-App", "PROJECT_BASELINE_APPROVED",
                "Baseline approved", "<p>Baseline approved for {{project.name}}</p>",
                "Baseline approved for {{project.name}}",
                EmailRecipientStrategy.PROJECT_WATCHERS, false, null);

        log.info("[ProjectNotificationTemplateRuleSeed] Project template/rule seeding complete");
    }

    private void seed(String templateCode, String name, String eventCode,
                      String subject, String html, String text,
                      EmailRecipientStrategy strategy, boolean mandatory, String recipientConfigJson) {
        if (templateRepository.existsByCode(templateCode)) {
            return;
        }
        UUID eventDefId = eventDefinitionRepository.findByCode(EventDefinitionCode.of(eventCode))
                .map(e -> e.id()).orElse(null);
        if (eventDefId == null) {
            log.warn("[ProjectNotificationTemplateRuleSeed] Event {} missing, skip {}", eventCode, templateCode);
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode), name, "Phase 20 project notification template", eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(template.id(), 1, subject, html, text);
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);

        String ruleCode = "RULE_" + templateCode;
        if (!ruleRepository.existsByCode(ruleCode)) {
            EmailRule rule = EmailRule.createSystem(
                    ruleCode, name + " Rule", null, eventDefId, template.id(),
                    strategy, recipientConfigJson, 20, mandatory, false);
            ruleRepository.save(rule);
        }
    }
}
