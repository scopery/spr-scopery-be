package com.company.scopery.modules.eventregistry.shared.seed;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Shared idempotent helpers for module EventDefinition seeders (Phase 05 contract).
 *
 * findOrCreate uses a SELECT-first strategy: checks (source_system, event_key) first, then
 * code, and only inserts when the row genuinely does not exist. This avoids triggering
 * constraint violations inside @Transactional seeders — a PostgreSQL constraint violation
 * aborts the connection, making any subsequent SQL fail until the transaction ends.
 *
 * The JdbcTemplate is wired in at application startup by EventDefinitionSeedJdbcBridge, which is
 * created during Spring context initialization — before any ApplicationReadyEvent seeders run.
 */
public final class EventDefinitionSeedSupport {

    private EventDefinitionSeedSupport() {}

    // --- JDBC plumbing (wired by EventDefinitionSeedJdbcBridge) ---

    private static volatile JdbcTemplate jdbcTemplate;

    static void setJdbcTemplate(JdbcTemplate jdbc) {
        jdbcTemplate = jdbc;
    }

    private static final String UPSERT_RETURNING_SQL =
            "INSERT INTO app_event_definition " +
            "(id, code, name, source_system, event_key, description, status, event_version, " +
            " is_system_event, owner_module, created_at, updated_at, created_by, updated_by) " +
            "VALUES (gen_random_uuid(), ?, ?, ?, ?, ?, 'ACTIVE', 1, true, ?, " +
            "        now(), now(), 'SYSTEM', 'SYSTEM') " +
            "ON CONFLICT ON CONSTRAINT uq_app_event_definition_source_system_event_key " +
            "DO UPDATE SET updated_at = now() " +
            "RETURNING id";

    // --- Main API ---

    public static EventDefinition findOrCreate(EventDefinitionRepository repository,
                                               String sourceSystem,
                                               String code,
                                               String name,
                                               String description,
                                               EventDataClassification classification,
                                               String ownerModule) {
        if (jdbcTemplate != null) {
            return findOrCreateViaJdbc(repository, sourceSystem, code, name, description, ownerModule);
        }
        // Fallback: JPA only (EventDefinitionSeedJdbcBridge not yet wired — should not happen at runtime)
        EventDefinitionCode defCode = EventDefinitionCode.of(code);
        return repository.findByCode(defCode).orElseGet(() ->
                repository.save(EventDefinition.create(
                        defCode, name,
                        SourceSystemCode.of(sourceSystem),
                        EventKey.of(code),
                        description, null, null,
                        classification, ownerModule, true)));
    }

    private static UUID resolveId(String code, String name, String sourceSystem, String description, String ownerModule) {
        // SELECT-first: avoid triggering constraint violations, which abort the PostgreSQL
        // connection inside @Transactional seeders and make any subsequent SQL fail.
        List<UUID> byKey = jdbcTemplate.query(
                "SELECT id FROM app_event_definition WHERE source_system = ? AND event_key = ?",
                (rs, n) -> rs.getObject("id", UUID.class),
                sourceSystem, code);
        if (!byKey.isEmpty()) {
            return byKey.get(0);
        }

        // Cross-source overlap: same code exists under a different source_system.
        List<UUID> byCode = jdbcTemplate.query(
                "SELECT id FROM app_event_definition WHERE code = ?",
                (rs, n) -> rs.getObject("id", UUID.class),
                code);
        if (!byCode.isEmpty()) {
            return byCode.get(0);
        }

        // Truly new row: INSERT via UPSERT as a safety net against concurrent inserts.
        return jdbcTemplate.queryForObject(UPSERT_RETURNING_SQL, UUID.class,
                code, name, sourceSystem, code, description, ownerModule);
    }

    private static EventDefinition findOrCreateViaJdbc(EventDefinitionRepository repository,
                                                        String sourceSystem,
                                                        String code,
                                                        String name,
                                                        String description,
                                                        String ownerModule) {
        final UUID id = resolveId(code, name, sourceSystem, description, ownerModule);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Event definition not found after JDBC upsert: sourceSystem="
                                + sourceSystem + ", code=" + code + ", id=" + id));
    }

    /**
     * Adds missing variables only. Never deletes existing variables (consumer-safe).
     * Logs drift when type/required/sensitive differs from expected.
     */
    public static void ensureVariables(EventDefinitionRepository repository,
                                       Logger log,
                                       UUID eventDefinitionId,
                                       List<VariableSpec> expected) {
        Map<String, EventVariable> existing = repository.findVariablesByEventDefinitionId(eventDefinitionId)
                .stream()
                .collect(Collectors.toMap(EventVariable::variablePath, Function.identity(), (a, b) -> a));

        for (VariableSpec spec : expected) {
            EventVariable current = existing.get(spec.path());
            if (current == null) {
                repository.saveVariable(EventVariable.create(
                        eventDefinitionId,
                        spec.path(),
                        spec.label(),
                        spec.type(),
                        spec.required(),
                        spec.sensitive(),
                        null,
                        null));
                continue;
            }
            if (current.variableType() != spec.type()
                    || current.required() != spec.required()
                    || current.sensitive() != spec.sensitive()) {
                log.warn("[EventDefinitionSeed] drift detected path={} expectedType={} actualType={} "
                                + "expectedRequired={} actualRequired={} expectedSensitive={} actualSensitive={}",
                        spec.path(),
                        spec.type(), current.variableType(),
                        spec.required(), current.required(),
                        spec.sensitive(), current.sensitive());
            }
        }
    }

    public record VariableSpec(
            String path,
            String label,
            VariableType type,
            boolean required,
            boolean sensitive
    ) {
        public static VariableSpec of(String path, String label, VariableType type, boolean required) {
            return new VariableSpec(path, label, type, required, false);
        }

        public static VariableSpec sensitive(String path, String label, VariableType type, boolean required) {
            return new VariableSpec(path, label, type, required, true);
        }
    }
}
