package com.company.scopery.modules.notification.emailtemplate.application;

import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateVariableValidator;
import com.company.scopery.modules.notification.emailtemplate.application.service.SimpleMustacheEmailTemplateRenderer;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.notification.shared.error.NotificationErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class EmailTemplateVariableValidatorTest {

    private EmailTemplateVariableValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmailTemplateVariableValidator(new SimpleMustacheEmailTemplateRenderer());
    }

    @Test
    void validate_allVariablesAllowed_passes() {
        Set<String> allowed = Set.of("user.name", "workspace.name");
        assertThatNoException().isThrownBy(() ->
                validator.validate("Hello {{user.name}}", "<p>{{workspace.name}}</p>", null, allowed));
    }

    @Test
    void validate_undeclaredVariable_throws422() {
        Set<String> allowed = Set.of("user.name");
        assertThatThrownBy(() ->
                validator.validate("Hello {{user.name}}", "<p>{{unknown.var}}</p>", null, allowed))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ex = (AppException) e;
                    assertThat(ex.getErrorCode())
                            .isEqualTo(NotificationErrorCatalog.EMAIL_TEMPLATE_VERSION_VARIABLE_MISSING.code());
                });
    }

    @Test
    void validate_textBodyAlsoChecked() {
        Set<String> allowed = Set.of("user.name");
        assertThatThrownBy(() ->
                validator.validate("Subject", "<p>{{user.name}}</p>", "Text {{bad.var}}", allowed))
                .isInstanceOf(AppException.class);
    }

    @Test
    void validate_nullTextBody_skipped() {
        Set<String> allowed = Set.of("user.name");
        assertThatNoException().isThrownBy(() ->
                validator.validate("{{user.name}}", "<p>{{user.name}}</p>", null, allowed));
    }
}
