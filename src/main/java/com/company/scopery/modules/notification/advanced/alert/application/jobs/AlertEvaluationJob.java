package com.company.scopery.modules.notification.advanced.alert.application.jobs;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertRuleRepository;
import com.company.scopery.modules.notification.advanced.shared.util.SimpleConditionEvaluator;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionEntry;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Component
public class AlertEvaluationJob {
    private static final Logger log = LoggerFactory.getLogger(AlertEvaluationJob.class);
    private final AlertRuleRepository rules;
    private final NotificationSuppressionRepository suppressions;

    public AlertEvaluationJob(AlertRuleRepository rules, NotificationSuppressionRepository suppressions) {
        this.rules = rules; this.suppressions = suppressions;
    }

    @Scheduled(cron = "${scopery.advanced-notification.alert-eval-cron:0 */5 * * * *}")
    @Transactional
    public void evaluateAlerts() {
        int matched = 0;
        int suppressed = 0;
        for (var rule : rules.findAllActive()) {
            boolean ok = SimpleConditionEvaluator.matches(rule.conditionJson(), Map.of("status", "ACTIVE"));
            if (!ok) {
                suppressed++;
                suppressions.save(NotificationSuppressionEntry.create(
                        rule.workspaceId(), null, null, "ALERT", "IN_APP",
                        "CONDITION_NOT_MATCHED", "ALERT_RULE", rule.id()));
                continue;
            }
            matched++;
        }
        if (matched + suppressed > 0) {
            log.info("Alert evaluation: matched={}, suppressed={}, no email claimed", matched, suppressed);
        }
    }
}
