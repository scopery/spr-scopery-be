package com.company.scopery.modules.governance.shared.audit;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.governance.shared.constant.GovernanceModuleCodes;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * SENS-001: Records audit events when sensitive governed data is accessed.
 * Call auditSensitiveAccess() from query services that return finance/restricted/sensitive fields.
 */
@Component
public class GovernanceSensitiveAccessTracker {
    private static final Logger log = LoggerFactory.getLogger(GovernanceSensitiveAccessTracker.class);
    private final ActivityLogService activityLog;
    public GovernanceSensitiveAccessTracker(ActivityLogService activityLog) { this.activityLog = activityLog; }

    public void auditSensitiveAccess(String objectTypeCode, UUID targetId, String accessType, UUID actorId) {
        try {
            activityLog.logSuccess(
                GovernanceModuleCodes.GOVERNANCE,
                objectTypeCode,
                targetId != null ? targetId.toString() : null,
                "SENSITIVE_OBJECT_ACCESSED",
                actorId != null ? actorId.toString() : null,
                null,
                "Sensitive access: " + accessType + " on " + objectTypeCode,
                "{\"accessType\":\"" + accessType + "\"}"
            );
        } catch (Exception e) {
            log.warn("Failed to audit sensitive access for {}/{}: {}", objectTypeCode, targetId, e.getMessage());
        }
    }
}
