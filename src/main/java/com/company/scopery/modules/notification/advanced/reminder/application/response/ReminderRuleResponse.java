package com.company.scopery.modules.notification.advanced.reminder.application.response;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.ReminderRule;
import java.time.Instant; import java.util.UUID;
public record ReminderRuleResponse(UUID id, UUID workspaceId, String ruleCode, String name, String conditionJson, String recipientRuleJson, String status, Instant createdAt) {
    public static ReminderRuleResponse from(ReminderRule r) {
        return new ReminderRuleResponse(r.id(), r.workspaceId(), r.ruleCode(), r.name(), r.conditionJson(), r.recipientRuleJson(), r.status(), r.createdAt());
    }
}
