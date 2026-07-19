package com.company.scopery.modules.profitability.shared.listeners;
import com.company.scopery.modules.profitability.profile.application.action.RebuildProfitabilitySummaryAction;
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProfitabilityRebuildOnRequestListener {
    private static final Logger log = LoggerFactory.getLogger(ProfitabilityRebuildOnRequestListener.class);
    private final RebuildProfitabilitySummaryAction rebuild;

    public ProfitabilityRebuildOnRequestListener(RebuildProfitabilitySummaryAction rebuild) {
        this.rebuild = rebuild;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRebuildRequested(ProfitabilityRebuildRequestedEvent event) {
        try {
            rebuild.execute(event.projectId());
            log.info("Auto-rebuilt profitability summary for project {} reason={}", event.projectId(), event.reason());
        } catch (Exception ex) {
            log.warn("Profitability auto-rebuild skipped for project {}: {}", event.projectId(), ex.getMessage());
        }
    }
}
