package com.company.scopery.modules.quality.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.quality.shared.constant.QualityModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class QualityActivityLogger {
    private final ActivityLogService s;
    public QualityActivityLogger(ActivityLogService s) { this.s = s; }
    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        s.logSuccess(QualityModuleCodes.QUALITY, entityType, entityId == null ? null : entityId.toString(), action, null, null, message, null);
    }
}
