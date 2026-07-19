package com.company.scopery.modules.notification.advanced.digest.application.response;
import com.company.scopery.modules.notification.advanced.digest.domain.model.DigestRule;
import java.util.UUID;
public record DigestRuleResponse(UUID id, String code, String name, String frequency, String status) {
    public static DigestRuleResponse from(DigestRule r) { return new DigestRuleResponse(r.id(), r.code(), r.name(), r.frequency(), r.status()); }
}
