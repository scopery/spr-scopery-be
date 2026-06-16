package com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecretMaskerTest {

    @Test
    void mask_longKey_showsPrefixAndSuffix() {
        String result = SecretMasker.mask("sk-proj-abc123xyz789");
        assertThat(result).isEqualTo("sk-...z789");
    }

    @Test
    void mask_exactlyEightChars_showsPrefixAndSuffix() {
        String result = SecretMasker.mask("abcdefgh");
        assertThat(result).isEqualTo("abc...efgh");
    }

    @Test
    void mask_shortValue_returnsFourStars() {
        assertThat(SecretMasker.mask("abc")).isEqualTo("****");
        assertThat(SecretMasker.mask("1234567")).isEqualTo("****");
    }

    @Test
    void mask_nullValue_returnsFourStars() {
        assertThat(SecretMasker.mask(null)).isEqualTo("****");
    }

    @Test
    void mask_emptyString_returnsFourStars() {
        assertThat(SecretMasker.mask("")).isEqualTo("****");
    }

    @Test
    void mask_neverReturnsFullSecret() {
        String secret = "sk-proj-verylongapikey123456";
        String masked = SecretMasker.mask(secret);
        assertThat(masked).doesNotContain(secret);
        assertThat(masked).contains("...");
    }
}
