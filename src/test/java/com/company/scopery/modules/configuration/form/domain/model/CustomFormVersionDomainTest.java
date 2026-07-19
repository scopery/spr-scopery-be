package com.company.scopery.modules.configuration.form.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class CustomFormVersionDomainTest {
    @Test void publishMakesImmutableDraftOnly() {
        var v = CustomFormVersion.create(UUID.randomUUID(), UUID.randomUUID(), 1);
        assertThat(v.isDraft()).isTrue();
        var published = v.publish();
        assertThat(published.isPublished()).isTrue();
        assertThatThrownBy(published::publish).isInstanceOf(IllegalStateException.class);
    }
}
