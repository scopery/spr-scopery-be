package com.company.scopery.modules.aiagent.provider.domain;

import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ProviderTest {

    @Test
    void create_withValidData_createsActiveProvider() {
        Provider provider = Provider.create(
                "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", "OpenAI provider");

        assertThat(provider.id()).isNotNull();
        assertThat(provider.status()).isEqualTo(ProviderStatus.ACTIVE);
        assertThat(provider.code().value()).isEqualTo("OPENAI");
        assertThat(provider.createdAt()).isNotNull();
    }

    @Test
    void create_withoutApiBaseUrl_throwsException() {
        assertThatThrownBy(() -> Provider.create(
                "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("API base URL");
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThatThrownBy(() -> Provider.create(
                "", ProviderCode.of("OPENAI"), ProviderType.LLM, "https://api.openai.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void deactivate_setsStatusToInactive() {
        Provider provider = activeProvider();
        provider.deactivate();
        assertThat(provider.status()).isEqualTo(ProviderStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_withApiBaseUrl_succeeds() {
        Provider provider = inactiveProvider("https://api.openai.com");
        provider.activate();
        assertThat(provider.status()).isEqualTo(ProviderStatus.ACTIVE);
    }

    @Test
    void activate_fromInactive_withoutApiBaseUrl_throwsException() {
        Provider provider = inactiveProvider(null);
        assertThatThrownBy(provider::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACTIVE_PROVIDER_REQUIRES_API_BASE_URL");
    }

    @Test
    void activate_fromDeprecated_throwsException() {
        Provider provider = deprecatedProvider();
        assertThatThrownBy(provider::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED");
    }

    @Test
    void update_onActiveProvider_withoutApiBaseUrl_throwsException() {
        Provider provider = activeProvider();
        assertThatThrownBy(() -> provider.update("New Name", ProviderType.LLM, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("API base URL");
    }

    // --- helpers ---

    private Provider activeProvider() {
        return Provider.create("OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", null);
    }

    private Provider inactiveProvider(String apiBaseUrl) {
        return Provider.reconstitute(UUID.randomUUID(), "OpenAI", ProviderCode.of("OPENAI"),
                ProviderType.LLM, apiBaseUrl, null, ProviderStatus.INACTIVE, Instant.now(), Instant.now());
    }

    private Provider deprecatedProvider() {
        return Provider.reconstitute(UUID.randomUUID(), "Old Provider", ProviderCode.of("OLD"),
                ProviderType.LLM, "https://old.api.com", null, ProviderStatus.DEPRECATED, Instant.now(), Instant.now());
    }
}
