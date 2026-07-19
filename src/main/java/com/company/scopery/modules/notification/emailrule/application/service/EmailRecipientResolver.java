package com.company.scopery.modules.notification.emailrule.application.service;

import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class EmailRecipientResolver {

    private static final Logger log = LoggerFactory.getLogger(EmailRecipientResolver.class);

    private final List<ExtendedRecipientStrategyHandler> extendedHandlers;

    public EmailRecipientResolver(List<ExtendedRecipientStrategyHandler> extendedHandlers) {
        this.extendedHandlers = extendedHandlers == null ? List.of() : extendedHandlers;
    }

    public record RecipientResult(String email, UUID userId, boolean skipped, String skipReason) {
        public static RecipientResult resolved(String email) {
            return new RecipientResult(email, null, false, null);
        }

        public static RecipientResult resolved(String email, UUID userId) {
            return new RecipientResult(email, userId, false, null);
        }

        public static RecipientResult skipped(String reason) {
            return new RecipientResult(null, null, true, reason);
        }
    }

    public RecipientResult resolve(EmailRule rule, Map<String, Object> payload) {
        List<RecipientResult> all = resolveAll(rule, payload);
        if (all.isEmpty()) {
            return RecipientResult.skipped("No recipients resolved");
        }
        return all.getFirst();
    }

    public List<RecipientResult> resolveAll(EmailRule rule, Map<String, Object> payload) {
        for (ExtendedRecipientStrategyHandler handler : extendedHandlers) {
            if (handler.supports(rule.recipientStrategy())) {
                return handler.resolveAll(rule, payload);
            }
        }
        return List.of(resolveBuiltIn(rule, payload));
    }

    private RecipientResult resolveBuiltIn(EmailRule rule, Map<String, Object> payload) {
        return switch (rule.recipientStrategy()) {
            case EVENT_ACTOR -> resolveFromPayload(payload, "actor.email", "actor.userId");
            case EVENT_TARGET_USER -> resolveFromPayload(payload, "targetUser.email", "targetUser.userId");
            case INVITEE_EMAIL -> resolveFromPayload(payload, "invitee.email", "invitee.userId");
            case REQUESTER_EMAIL -> resolveFromPayload(payload, "requester.email", "requester.userId");
            case STATIC_EMAIL -> resolveStaticEmail(rule);
            case WORKSPACE_OWNER -> resolveFromPayload(payload, "workspace.ownerEmail", "workspace.ownerUserId");
            case WORKSPACE_USERS_WITH_RIGHT -> {
                log.info("[EmailRecipientResolver] WORKSPACE_USERS_WITH_RIGHT is deferred, skipping rule {}",
                        rule.code());
                yield RecipientResult.skipped(
                        "WORKSPACE_USERS_WITH_RIGHT resolution is deferred. " +
                        "Requires IAM grant resolution across user/team/role subjects.");
            }
            default -> RecipientResult.skipped("Unsupported recipient strategy: " + rule.recipientStrategy());
        };
    }

    private RecipientResult resolveFromPayload(Map<String, Object> payload, String emailPath, String userIdPath) {
        Object value = resolvePath(emailPath, payload);
        if (value instanceof String email && !email.isBlank()) {
            return RecipientResult.resolved(email, resolveUserId(payload, userIdPath));
        }
        return RecipientResult.skipped("Could not resolve email from payload path: " + emailPath);
    }

    private UUID resolveUserId(Map<String, Object> payload, String userIdPath) {
        Object value = resolvePath(userIdPath, payload);
        if (value == null) return null;
        if (value instanceof UUID uuid) return uuid;
        if (value instanceof String s && !s.isBlank()) {
            try {
                return UUID.fromString(s.trim());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private RecipientResult resolveStaticEmail(EmailRule rule) {
        String config = rule.recipientConfigJson();
        if (config == null || config.isBlank()) {
            return RecipientResult.skipped("STATIC_EMAIL requires recipientConfigJson with 'email' field");
        }
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
