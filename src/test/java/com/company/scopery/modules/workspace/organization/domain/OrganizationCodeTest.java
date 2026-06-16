package com.company.scopery.modules.workspace.organization.domain;

import com.company.scopery.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrganizationCodeTest {

    @Test
    void of_normalizesToUppercase() {
        OrganizationCode code = OrganizationCode.of("acme");
        assertThat(code.value()).isEqualTo("ACME");
    }

    @Test
    void of_tripsWhitespaceAndNormalizes() {
        OrganizationCode code = OrganizationCode.of("  acme_corp  ");
        assertThat(code.value()).isEqualTo("ACME_CORP");
    }

    @Test
    void of_acceptsValidPatterns() {
        assertThatCode(() -> OrganizationCode.of("ACME")).doesNotThrowAnyException();
        assertThatCode(() -> OrganizationCode.of("ACME_CORP")).doesNotThrowAnyException();
        assertThatCode(() -> OrganizationCode.of("ORG123")).doesNotThrowAnyException();
        assertThatCode(() -> OrganizationCode.of("ORG_123_ABC")).doesNotThrowAnyException();
    }

    @Test
    void of_rejectsBlankInput() {
        assertThatThrownBy(() -> OrganizationCode.of(""))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsNullInput() {
        assertThatThrownBy(() -> OrganizationCode.of(null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithSpaces() {
        assertThatThrownBy(() -> OrganizationCode.of("ACME CORP"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithHyphen() {
        assertThatThrownBy(() -> OrganizationCode.of("acme-corp"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void of_rejectsCodeWithSpecialChars() {
        assertThatThrownBy(() -> OrganizationCode.of("acme.corp"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void equalCodes_areEqual() {
        assertThat(OrganizationCode.of("ACME")).isEqualTo(OrganizationCode.of("acme"));
    }
}
