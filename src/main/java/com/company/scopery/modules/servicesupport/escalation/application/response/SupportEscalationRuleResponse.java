package com.company.scopery.modules.servicesupport.escalation.application.response;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRule;
import java.time.Instant; import java.util.UUID;
public record SupportEscalationRuleResponse(UUID id, UUID workspaceId, String ruleCode, String name, String triggerType,
        boolean enabled, String status, Instant createdAt) {
    public static SupportEscalationRuleResponse from(SupportEscalationRule d) {
        return new SupportEscalationRuleResponse(d.id(), d.workspaceId(), d.ruleCode(), d.name(), d.triggerType(),
                d.enabled(), d.status(), d.createdAt());
    }
}
