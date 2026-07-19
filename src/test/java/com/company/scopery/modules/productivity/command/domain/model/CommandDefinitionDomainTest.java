package com.company.scopery.modules.productivity.command.domain.model;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class CommandDefinitionDomainTest {
    @Test void createEnabledCommand() {
        var c = CommandDefinition.create("OPEN_SEARCH", "Open search", "NAV", null, false);
        assertThat(c.enabled()).isTrue();
        assertThat(c.dangerous()).isFalse();
    }
}
