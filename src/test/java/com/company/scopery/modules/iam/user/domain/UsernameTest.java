package com.company.scopery.modules.iam.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UsernameTest {

    @Test
    void of_validUsername_succeeds() {
        Username username = Username.of("john.doe");
        assertThat(username.value()).isEqualTo("john.doe");
    }

    @Test
    void of_uppercaseInput_isLowercased() {
        Username username = Username.of("JohnDoe");
        assertThat(username.value()).isEqualTo("johndoe");
    }

    @Test
    void of_nullInput_throwsException() {
        assertThatThrownBy(() -> Username.of(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_tooShort_throwsException() {
        assertThatThrownBy(() -> Username.of("ab"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_invalidChars_throwsException() {
        assertThatThrownBy(() -> Username.of("john doe"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_atSymbol_throwsException() {
        assertThatThrownBy(() -> Username.of("john@doe"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_withDotDashUnderscore_succeeds() {
        assertThat(Username.of("john.doe_123-x").value()).isEqualTo("john.doe_123-x");
    }
}
