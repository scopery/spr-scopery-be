package com.company.scopery.modules.workspace.team.domain;

import com.company.scopery.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TeamCodeTest {

    @Test
    void of_normalizesToUppercase() {
        TeamCode code = TeamCode.of("backend");
        assertThat(code.value()).isEqualTo("BACKEND");
    }

    @Test
    void of_tripsWhitespaceAndNormalizes() {
        TeamCode code = TeamCode.of("  backend_team  ");
        assertThat(code.value()).isEqualTo("BACKEND_TEAM");
    }

    @Test
    void of_acceptsValidPatterns() {
        assertThatCode(() -> TeamCode.of("BACKEND")).doesNotThrowAnyException();
        assertThatCode(() -> TeamCode.of("TEAM_01")).doesNotThrowAnyException();
        assertThatCode(() -> TeamCode.of("TEAM123")).doesNotThrowAnyException();
        assertThatCode(() -> TeamCode.of("BACKEND_TEAM_2")).doesNotThrowAnyException();
    }

    @Test
    void of_rejectsBlankInput() {
        assertThatThrownBy(() -> TeamCode.of(""))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsNullInput() {
        assertThatThrownBy(() -> TeamCode.of(null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithSpaces() {
        assertThatThrownBy(() -> TeamCode.of("BACKEND TEAM"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithHyphen() {
        assertThatThrownBy(() -> TeamCode.of("backend-team"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithSpecialChars() {
        assertThatThrownBy(() -> TeamCode.of("backend.team"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void equalCodes_areEqual() {
        assertThat(TeamCode.of("BACKEND")).isEqualTo(TeamCode.of("backend"));
    }
}
