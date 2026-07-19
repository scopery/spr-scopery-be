package com.company.scopery.modules.notification.advanced.reminder.application.jobs;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.ReminderInstanceRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Component
public class AdvancedReminderEvaluationJob {
    private static final Logger log = LoggerFactory.getLogger(AdvancedReminderEvaluationJob.class);
    private final ReminderInstanceRepository instances;
    public AdvancedReminderEvaluationJob(ReminderInstanceRepository instances) { this.instances = instances; }

    @Scheduled(cron = "${scopery.advanced-notification.reminder-eval-cron:0 */15 * * * *}")
    @Transactional
    public void evaluateDueReminders() {
        var due = instances.findPendingDue(Instant.now());
        int dispatched = 0;
        for (var instance : due) {
            instances.save(instance.dispatch());
            dispatched++;
        }
        if (dispatched > 0) {
            log.info("Advanced reminder evaluation dispatched {} instance(s)", dispatched);
        }
    }
}
