package com.company.scopery.modules.workspace.workspace.domain;

import com.company.scopery.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class WorkspaceCodeTest {

    @Test
    void of_normalizesToUppercase() {
        WorkspaceCode code = WorkspaceCode.of("dev");
        assertThat(code.value()).isEqualTo("DEV");
    }

    @Test
    void of_tripsWhitespaceAndNormalizes() {
        WorkspaceCode code = WorkspaceCode.of("  dev_ops  ");
        assertThat(code.value()).isEqualTo("DEV_OPS");
    }

    @Test
    void of_acceptsValidPatterns() {
        assertThatCode(() -> WorkspaceCode.of("DEV")).doesNotThrowAnyException();
        assertThatCode(() -> WorkspaceCode.of("DEV_OPS")).doesNotThrowAnyException();
        assertThatCode(() -> WorkspaceCode.of("WS123")).doesNotThrowAnyException();
        assertThatCode(() -> WorkspaceCode.of("WS_123_ABC")).doesNotThrowAnyException();
    }

    @Test
    void of_rejectsBlankInput() {
        assertThatThrownBy(() -> WorkspaceCode.of(""))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsNullInput() {
        assertThatThrownBy(() -> WorkspaceCode.of(null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithSpaces() {
        assertThatThrownBy(() -> WorkspaceCode.of("DEV OPS"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithHyphen() {
        assertThatThrownBy(() -> WorkspaceCode.of("dev-ops"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithSpecialChars() {
        assertThatThrownBy(() -> WorkspaceCode.of("dev.ops"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void equalCodes_areEqual() {
        assertThat(WorkspaceCode.of("DEV")).isEqualTo(WorkspaceCode.of("dev"));
    }
}
