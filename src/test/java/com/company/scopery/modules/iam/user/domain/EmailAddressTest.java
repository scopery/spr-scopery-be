package com.company.scopery.modules.iam.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailAddressTest {

    @Test
    void of_validEmail_succeeds() {
        EmailAddress email = EmailAddress.of("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void of_uppercaseEmail_isLowercased() {
        EmailAddress email = EmailAddress.of("User@Example.COM");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void of_nullInput_throwsException() {
        assertThatThrownBy(() -> EmailAddress.of(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_noAtSymbol_throwsException() {
        assertThatThrownBy(() -> EmailAddress.of("notanemail"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_noDomain_throwsException() {
        assertThatThrownBy(() -> EmailAddress.of("user@"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void of_complexValidEmail_succeeds() {
        assertThat(EmailAddress.of("first.last+tag@sub.domain.org").value())
                .isEqualTo("first.last+tag@sub.domain.org");
    }
}
