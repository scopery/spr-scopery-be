package com.company.scopery.modules.servicesupport.serviceprofile.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class ServiceProfileDomainTest {
    @Test void createServiceProfile_success() {
        var p = ServiceProfile.create(UUID.randomUUID(), "PROJECT", UUID.randomUUID());
        assertThat(p.enabled()).isTrue();
        assertThat(p.scopeType()).isEqualTo("PROJECT");
    }
}
