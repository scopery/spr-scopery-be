package com.company.scopery.modules.notification.advanced.digest.application.jobs;
import com.company.scopery.modules.notification.advanced.digest.domain.model.DigestRuleRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DigestEvaluationJob {
    private static final Logger log = LoggerFactory.getLogger(DigestEvaluationJob.class);
    private final DigestRuleRepository rules;
    public DigestEvaluationJob(DigestRuleRepository rules) { this.rules = rules; }
    @Scheduled(cron = "${scopery.advanced-notification.digest-eval-cron:0 0 * * * *}")
    @Transactional(readOnly = true)
    public void evaluateDigests() {
        long active = rules.findAllActive().size();
        if (active > 0) {
            log.info("Digest evaluation cycle scanned {} active digest rule(s); delivery deferred to outbox", active);
        }
    }
}
