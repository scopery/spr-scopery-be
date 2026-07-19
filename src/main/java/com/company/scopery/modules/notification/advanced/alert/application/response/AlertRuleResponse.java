package com.company.scopery.modules.notification.advanced.alert.application.response;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertRule;
import java.util.UUID;
public record AlertRuleResponse(UUID id, String ruleCode, String name, String category, String status, boolean bypassQuietHours) {
    public static AlertRuleResponse from(AlertRule r) {
        return new AlertRuleResponse(r.id(), r.ruleCode(), r.name(), r.category(), r.status(), r.bypassQuietHours());
    }
}
