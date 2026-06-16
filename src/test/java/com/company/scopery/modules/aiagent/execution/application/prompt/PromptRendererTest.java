package com.company.scopery.modules.aiagent.execution.application.prompt;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PromptRendererTest {

    private final PromptRenderer renderer = new PromptRenderer();

    @Test
    void render_noPlaceholders_returnsTemplateUnchanged() {
        String result = renderer.render("Hello, world!", Map.of());
        assertThat(result).isEqualTo("Hello, world!");
    }

    @Test
    void render_singlePlaceholder_replacesValue() {
        String result = renderer.render("Hello, {{name}}!", Map.of("name", "Alice"));
        assertThat(result).isEqualTo("Hello, Alice!");
    }

    @Test
    void render_multiplePlaceholders_replacesAll() {
        String result = renderer.render("{{greeting}}, {{name}}! You have {{count}} messages.",
                Map.of("greeting", "Hi", "name", "Bob", "count", "5"));
        assertThat(result).isEqualTo("Hi, Bob! You have 5 messages.");
    }

    @Test
    void render_samePlaceholderTwice_replacesAll() {
        String result = renderer.render("{{val}} and {{val}} again", Map.of("val", "X"));
        assertThat(result).isEqualTo("X and X again");
    }

    @Test
    void render_nullVariables_treatedAsEmpty() {
        String result = renderer.render("No placeholders here.", null);
        assertThat(result).isEqualTo("No placeholders here.");
    }

    @Test
    void render_missingVariable_throwsBadRequest() {
        assertThatThrownBy(() -> renderer.render("Hello, {{name}}!", Map.of()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.PROMPT_RENDER_VARIABLE_MISSING.code());
                });
    }

    @Test
    void render_nullTemplate_throwsUnprocessable() {
        assertThatThrownBy(() -> renderer.render(null, Map.of()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.PROMPT_RENDER_FAILED.code());
                });
    }

    @Test
    void render_valueContainsSpecialRegexChars_handledSafely() {
        String result = renderer.render("Cost: {{amount}}", Map.of("amount", "$1.00 (USD)"));
        assertThat(result).isEqualTo("Cost: $1.00 (USD)");
    }

    @Test
    void render_valueContainsBraces_handledSafely() {
        String result = renderer.render("Data: {{json}}", Map.of("json", "{\"key\": \"value\"}"));
        assertThat(result).isEqualTo("Data: {\"key\": \"value\"}");
    }
}
