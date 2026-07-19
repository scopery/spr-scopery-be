package com.company.scopery.modules.trust.sensitiveobject.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveObjectRegistryDomainTest {
    @Test
    void createAndDeactivate_success() {
        var entry = SensitiveObjectRegistry.create(UUID.randomUUID(), "DOCUMENT", "CONFIDENTIAL");
        assertThat(entry.enabled()).isTrue();
        assertThat(entry.deactivate().enabled()).isFalse();
    }
}
