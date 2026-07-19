package com.company.scopery.modules.notification.emailtemplate.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateRenderer;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateVariableValidator;
import com.company.scopery.modules.notification.emailtemplate.application.service.SimpleMustacheEmailTemplateRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class EmailTemplateVariableValidatorTest {

    private EmailTemplateVariableValidator validator;

    @BeforeEach
    void setUp() {
        EmailTemplateRenderer renderer = new SimpleMustacheEmailTemplateRenderer();
        validator = new EmailTemplateVariableValidator(renderer);
    }

    @Test
    void validate_unknownVariable_rejected() {
        assertThatThrownBy(() -> validator.validate(
                "Hello {{actor.name}}", "<p>{{unknown.path}}</p>", null,
                Set.of("actor.name"), Set.of(), false))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("unknown.path");
    }

    @Test
    void validate_sensitiveInSubject_alwaysRejected() {
        assertThatThrownBy(() -> validator.validate(
                "Reset {{invitee.token}}", "<p>Hello</p>", null,
                Set.of("invitee.token"), Set.of("invitee.token"), true))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("subject");
    }

    @Test
    void validate_sensitiveInBody_rejectedUnlessAllowed() {
        assertThatThrownBy(() -> validator.validate(
                "Hello", "<p>{{invitee.token}}</p>", null,
                Set.of("invitee.token"), Set.of("invitee.token"), false))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("body");
    }

    @Test
    void validate_sensitiveInBody_allowedWhenFlagTrue() {
        assertThatCode(() -> validator.validate(
                "Hello", "<p>{{invitee.token}}</p>", null,
                Set.of("invitee.token"), Set.of("invitee.token"), true))
                .doesNotThrowAnyException();
    }
}
