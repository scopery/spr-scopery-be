package com.company.scopery.modules.notification.emailrule.application.service;

import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailRecipientResolver {

    private static final Logger log = LoggerFactory.getLogger(EmailRecipientResolver.class);

    public record RecipientResult(String email, boolean skipped, String skipReason) {
        // email found → skipped=false, skipReason=null
        public static RecipientResult resolved(String email) { return new RecipientResult(email, false, null); }
        // email not resolvable → email=null, skipped=true, skipReason explains why
        public static RecipientResult skipped(String reason) { return new RecipientResult(null, true, reason); }
    }

    public RecipientResult resolve(EmailRule rule, Map<String, Object> payload) {
        return switch (rule.recipientStrategy()) {
            case EVENT_ACTOR -> resolveFromPayload(payload, "actor.email");
            case EVENT_TARGET_USER -> resolveFromPayload(payload, "targetUser.email");
            case INVITEE_EMAIL -> resolveFromPayload(payload, "invitee.email");
            case REQUESTER_EMAIL -> resolveFromPayload(payload, "requester.email");
            case STATIC_EMAIL -> resolveStaticEmail(rule);
            case WORKSPACE_OWNER -> resolveFromPayload(payload, "workspace.ownerEmail");
            case WORKSPACE_USERS_WITH_RIGHT -> {
                // Phase 1: unsupported — full IAM grant resolution deferred
                log.info("[EmailRecipientResolver] WORKSPACE_USERS_WITH_RIGHT is unsupported in Phase 1, skipping rule {}",
                        rule.code());
                yield RecipientResult.skipped(
                        "WORKSPACE_USERS_WITH_RIGHT resolution is not implemented in Phase 1. " +
                        "Requires IAM grant resolution across user/team/role subjects.");
            }
        };
    }

    private RecipientResult resolveFromPayload(Map<String, Object> payload, String path) {
        Object value = resolvePath(path, payload);
        if (value instanceof String email && !email.isBlank()) {
            return RecipientResult.resolved(email);
        }
        return RecipientResult.skipped("Could not resolve email from payload path: " + path);
    }

    private RecipientResult resolveStaticEmail(EmailRule rule) {
        String config = rule.recipientConfigJson();
        if (config == null || config.isBlank()) {
            return RecipientResult.skipped("STATIC_EMAIL requires recipientConfigJson with 'email' field");
        }
        // Simple JSON extraction without a library — look for "email":"..." pattern
        int idx = config.indexOf("\"email\"");
        if (idx < 0) return RecipientResult.skipped("STATIC_EMAIL config missing 'email' field");
        int colon = config.indexOf(':', idx);
        if (colon < 0) return RecipientResult.skipped("STATIC_EMAIL config malformed");
        int start = config.indexOf('"', colon + 1);
        if (start < 0) return RecipientResult.skipped("STATIC_EMAIL config value not a string");
        int end = config.indexOf('"', start + 1);
        if (end < 0) return RecipientResult.skipped("STATIC_EMAIL config value not closed");
        String email = config.substring(start + 1, end).trim();
        if (email.isBlank()) return RecipientResult.skipped("STATIC_EMAIL config email is blank");
        return RecipientResult.resolved(email);
    }

    @SuppressWarnings("unchecked")
    private Object resolvePath(String path, Map<String, Object> payload) {
        if (payload == null) return null;
        String[] parts = path.split("\\.", 2);
        Object val = payload.get(parts[0]);
        if (parts.length == 1 || val == null) return val;
        if (val instanceof Map<?, ?> nested) {
            return resolvePath(parts[1], (Map<String, Object>) nested);
        }
        return null;
    }
}
