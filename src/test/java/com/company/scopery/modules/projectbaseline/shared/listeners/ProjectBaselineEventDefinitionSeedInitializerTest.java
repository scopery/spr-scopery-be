package com.company.scopery.modules.projectbaseline.shared.listeners;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectBaselineEventDefinitionSeedInitializerTest {

    @Test
    void baselineChangeEventSeeder_firstRun_createsDefinitions() {
        assertThat(ProjectBaselineEventDefinitionSeedInitializer.SOURCE_SYSTEM)
                .isEqualTo("SCOPERY_PROJECT_GOVERNANCE");
        assertThat(ProjectBaselineEventDefinitionSeedInitializer.OWNER_MODULE)
                .isEqualTo("PROJECT_BASELINE");
        assertThat(ProjectBaselineEventDefinitionSeedInitializer.EVENTS).hasSize(28);
    }

    @Test
    void baselineChangeEventSeeder_secondRun_noDuplicates() {
        assertThat(ProjectBaselineEventDefinitionSeedInitializer.EVENTS).isNotEmpty();
    }
}
