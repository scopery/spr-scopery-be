package com.company.scopery.modules.notification.emailtemplate.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class SimpleMustacheEmailTemplateRendererTest {

    private SimpleMustacheEmailTemplateRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new SimpleMustacheEmailTemplateRenderer();
    }

    @Test
    void render_replacesTopLevelVariable() {
        String result = renderer.render("Hello {{name}}!", Map.of("name", "Alice"));
        assertThat(result).isEqualTo("Hello Alice!");
    }

    @Test
    void render_replacesNestedVariable() {
        String result = renderer.render("Hi {{user.email}}", Map.of("user", Map.of("email", "alice@example.com")));
        assertThat(result).isEqualTo("Hi alice@example.com");
    }

    @Test
    void render_missingVariableBecomesEmpty() {
        String result = renderer.render("Hello {{missing}}!", Map.of());
        assertThat(result).isEqualTo("Hello !");
    }

    @Test
    void render_nullTemplateReturnsNull() {
        assertThat(renderer.render(null, Map.of())).isNull();
    }

    @Test
    void render_multipleVariables() {
        String result = renderer.render("{{greeting}} {{user.name}}",
                Map.of("greeting", "Hello", "user", Map.of("name", "Bob")));
        assertThat(result).isEqualTo("Hello Bob");
    }

    @Test
    void render_doesNotExecuteMethodLikeExpressions() {
        String template = "{{java.lang.System.exit(0)}}";
        String result = renderer.render(template, Map.of());
        assertThat(result).isEqualTo("");
    }

    @Test
    void extractVariablePaths_findsAllPaths() {
        Set<String> paths = renderer.extractVariablePaths("Hello {{user.name}}, your code is {{invite.code}}");
        assertThat(paths).containsExactlyInAnyOrder("user.name", "invite.code");
    }

    @Test
    void extractVariablePaths_emptyForNoVariables() {
        assertThat(renderer.extractVariablePaths("No variables here")).isEmpty();
    }

    @Test
    void render_htmlSpecialCharsNotEscaped() {
        String result = renderer.render("Link: {{url}}", Map.of("url", "https://example.com?a=1&b=2"));
        assertThat(result).contains("&");
    }
}
