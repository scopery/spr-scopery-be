package com.company.scopery.modules.governance.shared.audit;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.governance.shared.constant.GovernanceModuleCodes;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * CVIS-001: Records audit events when client-visible governed data changes.
 * Call auditClientVisibleChange() from actions that mutate client-visible content.
 */
@Component
public class GovernanceClientVisibleChangeTracker {
    private static final Logger log = LoggerFactory.getLogger(GovernanceClientVisibleChangeTracker.class);
    private final ActivityLogService activityLog;
    public GovernanceClientVisibleChangeTracker(ActivityLogService activityLog) { this.activityLog = activityLog; }

    public void auditClientVisibleChange(String objectTypeCode, UUID targetId, String fieldName, UUID actorId) {
        try {
            activityLog.logSuccess(
                GovernanceModuleCodes.GOVERNANCE,
                objectTypeCode,
                targetId != null ? targetId.toString() : null,
                "CLIENT_VISIBLE_FIELD_CHANGED",
                actorId != null ? actorId.toString() : null,
                null,
                "Client-visible change: " + fieldName + " on " + objectTypeCode,
                "{\"field\":\"" + fieldName + "\"}"
            );
        } catch (Exception e) {
            log.warn("Failed to audit client-visible change for {}/{}: {}", objectTypeCode, targetId, e.getMessage());
        }
    }
}
