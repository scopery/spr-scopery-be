package com.company.scopery.modules.notification.emailrule.application;

import com.company.scopery.modules.notification.emailrule.application.service.EmailRecipientResolver;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class EmailRecipientResolverTest {

    private EmailRecipientResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new EmailRecipientResolver(List.of());
    }

    @Test
    void resolve_eventActor_returnsActorEmail() {
        var rule = makeRule(EmailRecipientStrategy.EVENT_ACTOR, null);
        var payload = Map.<String, Object>of("actor", Map.of("email", "actor@example.com"));
        var result = resolver.resolve(rule, payload);
        assertThat(result.skipped()).isFalse();
        assertThat(result.email()).isEqualTo("actor@example.com");
    }

    @Test
    void resolve_eventActor_resolvesUserId() {
        UUID userId = UUID.randomUUID();
        var rule = makeRule(EmailRecipientStrategy.EVENT_ACTOR, null);
        var payload = Map.<String, Object>of("actor", Map.of(
                "email", "actor@example.com",
                "userId", userId.toString()));
        var result = resolver.resolve(rule, payload);
        assertThat(result.userId()).isEqualTo(userId);
    }

    @Test
    void resolve_staticEmail_parsesFromConfig() {
        var rule = makeRule(EmailRecipientStrategy.STATIC_EMAIL, "{\"email\":\"static@example.com\"}");
        var result = resolver.resolve(rule, Map.of());
        assertThat(result.skipped()).isFalse();
        assertThat(result.email()).isEqualTo("static@example.com");
    }

    @Test
    void resolve_staticEmail_noConfig_skips() {
        var rule = makeRule(EmailRecipientStrategy.STATIC_EMAIL, null);
        var result = resolver.resolve(rule, Map.of());
        assertThat(result.skipped()).isTrue();
    }

    @Test
    void resolve_missingPayloadPath_skips() {
        var rule = makeRule(EmailRecipientStrategy.INVITEE_EMAIL, null);
        var result = resolver.resolve(rule, Map.of());
        assertThat(result.skipped()).isTrue();
        assertThat(result.skipReason()).contains("invitee.email");
    }

    @Test
    void resolve_workspaceUsersWithRight_skipsWithExplanation() {
        var rule = makeRule(EmailRecipientStrategy.WORKSPACE_USERS_WITH_RIGHT, "{\"rightCode\":\"MANAGE_MEMBER\"}");
        var result = resolver.resolve(rule, Map.of());
        assertThat(result.skipped()).isTrue();
        assertThat(result.skipReason()).contains("deferred");
    }

    private EmailRule makeRule(EmailRecipientStrategy strategy, String configJson) {
        return EmailRule.reconstitute(
                UUID.randomUUID(), "TEST_RULE", "Test Rule", null,
                EmailRuleScope.SYSTEM, null,
                UUID.randomUUID(), UUID.randomUUID(),
                strategy, configJson,
                10, true, false, false,
                com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleStatus.ACTIVE,
                java.time.Instant.now(), java.time.Instant.now(), null);
    }
}
