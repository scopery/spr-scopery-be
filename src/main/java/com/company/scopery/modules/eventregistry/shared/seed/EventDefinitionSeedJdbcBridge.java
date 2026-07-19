package com.company.scopery.modules.eventregistry.shared.seed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Wires JdbcTemplate into EventDefinitionSeedSupport during Spring context initialization,
 * before any ApplicationReadyEvent seeders run. This enables the UPSERT+RETURNING strategy
 * that bypasses the JPA derived-query cache issue.
 */
@Component
public class EventDefinitionSeedJdbcBridge {

    public EventDefinitionSeedJdbcBridge(JdbcTemplate jdbcTemplate) {
        EventDefinitionSeedSupport.setJdbcTemplate(jdbcTemplate);
    }
}
