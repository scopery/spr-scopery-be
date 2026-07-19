package com.company.scopery.modules.notification.advanced.reminder.domain.model;
import java.time.Instant; import java.util.UUID;
public record ReminderRule(UUID id, UUID workspaceId, String ruleCode, String name, String conditionJson,
        String recipientRuleJson, String status, int version, Instant createdAt, Instant updatedAt) {
    public static ReminderRule create(UUID workspaceId, String ruleCode, String name, String conditionJson, String recipientRuleJson) {
        Instant now = Instant.now();
        return new ReminderRule(UUID.randomUUID(), workspaceId, ruleCode, name, conditionJson, recipientRuleJson, "ACTIVE", 0, now, now);
    }
    public ReminderRule disable() {
        return new ReminderRule(id, workspaceId, ruleCode, name, conditionJson, recipientRuleJson, "INACTIVE", version, createdAt, Instant.now());
    }
}
