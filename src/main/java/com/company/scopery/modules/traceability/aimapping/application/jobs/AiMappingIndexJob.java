package com.company.scopery.modules.traceability.aimapping.application.jobs;

import com.company.scopery.modules.traceability.aimapping.application.internal.MappingSummaryBuilderService;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingTableNames;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Periodically rebuilds stale AI mapping summaries.
 * A summary is stale when the entity version has advanced since the summary was last built,
 * or when no summary exists yet for an entity that is eligible for mapping.
 * This ensures vector retrieval finds up-to-date embeddings even for entities that
 * were never included in a previous generate-run.
 */
@Component
public class AiMappingIndexJob {

    private static final Logger log = LoggerFactory.getLogger(AiMappingIndexJob.class);
    private static final int STALE_BATCH_LIMIT = 100;

    private final JdbcTemplate jdbc;
    private final MappingSummaryBuilderService summaryBuilderService;

    public AiMappingIndexJob(JdbcTemplate jdbc, MappingSummaryBuilderService summaryBuilderService) {
        this.jdbc = jdbc;
        this.summaryBuilderService = summaryBuilderService;
    }

    @Scheduled(cron = "${scopery.ai-mapping.index-cron:0 0 */4 * * *}")
    public void run() {
        log.info("AiMappingIndexJob started");
        int total = 0;
        total += rebuildStale(SummaryEntityType.REQUIREMENT, staleRequirements());
        total += rebuildStale(SummaryEntityType.FUNCTION,    staleFunctions());
        total += rebuildStale(SummaryEntityType.USE_CASE,    staleUseCases());
        total += rebuildStale(SummaryEntityType.TEST_CASE,   staleTestCases());
        log.info("AiMappingIndexJob completed: {} stale summaries rebuilt", total);
    }

    private int rebuildStale(SummaryEntityType type, List<UUID> ids) {
        if (ids.isEmpty()) return 0;
        log.info("AiMappingIndexJob: rebuilding {} stale {} summaries", ids.size(), type);
        int rebuilt = 0;
        for (UUID id : ids) {
            try {
                summaryBuilderService.getOrBuildSummary(type, id);
                rebuilt++;
            } catch (Exception e) {
                log.warn("AiMappingIndexJob: failed to rebuild summary for {} {}: {}", type, id, e.getMessage());
            }
        }
        return rebuilt;
    }

    private List<UUID> staleRequirements() {
        String t = AiMappingTableNames.MAPPING_SUMMARY;
        String sql = """
                SELECT r.id FROM requirements_requirement r
                WHERE r.status NOT IN ('ARCHIVED', 'REJECTED', 'DEFERRED')
                  AND (
                    NOT EXISTS (SELECT 1 FROM %s ams WHERE ams.entity_type = 'REQUIREMENT' AND ams.entity_id = r.id)
                    OR EXISTS  (SELECT 1 FROM %s ams WHERE ams.entity_type = 'REQUIREMENT' AND ams.entity_id = r.id
                                AND ams.entity_version != COALESCE(r.version, 0))
                  )
                LIMIT %d
                """.formatted(t, t, STALE_BATCH_LIMIT);
        return jdbc.queryForList(sql, UUID.class);
    }

    private List<UUID> staleFunctions() {
        String t = AiMappingTableNames.MAPPING_SUMMARY;
        String sql = """
                SELECT fi.id FROM app_functional_item fi
                WHERE fi.status != 'ARCHIVED'
                  AND (
                    NOT EXISTS (SELECT 1 FROM %s ams WHERE ams.entity_type = 'FUNCTION' AND ams.entity_id = fi.id)
                    OR EXISTS  (SELECT 1 FROM %s ams WHERE ams.entity_type = 'FUNCTION' AND ams.entity_id = fi.id
                                AND ams.entity_version != COALESCE(fi.version, 0))
                  )
                LIMIT %d
                """.formatted(t, t, STALE_BATCH_LIMIT);
        return jdbc.queryForList(sql, UUID.class);
    }

    private List<UUID> staleUseCases() {
        String t = AiMappingTableNames.MAPPING_SUMMARY;
        String sql = """
                SELECT uc.id FROM app_use_case uc
                WHERE uc.status NOT IN ('ARCHIVED', 'DEPRECATED')
                  AND (
                    NOT EXISTS (SELECT 1 FROM %s ams WHERE ams.entity_type = 'USE_CASE' AND ams.entity_id = uc.id)
                    OR EXISTS  (SELECT 1 FROM %s ams WHERE ams.entity_type = 'USE_CASE' AND ams.entity_id = uc.id
                                AND ams.entity_version != COALESCE(uc.version, 0))
                  )
                LIMIT %d
                """.formatted(t, t, STALE_BATCH_LIMIT);
        return jdbc.queryForList(sql, UUID.class);
    }

    private List<UUID> staleTestCases() {
        String t = AiMappingTableNames.MAPPING_SUMMARY;
        String sql = """
                SELECT tc.id FROM quality_test_case tc
                WHERE tc.status != 'ARCHIVED'
                  AND tc.type = 'FUNCTIONAL'
                  AND (
                    NOT EXISTS (SELECT 1 FROM %s ams WHERE ams.entity_type = 'TEST_CASE' AND ams.entity_id = tc.id)
                    OR EXISTS  (SELECT 1 FROM %s ams WHERE ams.entity_type = 'TEST_CASE' AND ams.entity_id = tc.id
                                AND ams.entity_version != COALESCE(tc.version, 0))
                  )
                LIMIT %d
                """.formatted(t, t, STALE_BATCH_LIMIT);
        return jdbc.queryForList(sql, UUID.class);
    }
}
