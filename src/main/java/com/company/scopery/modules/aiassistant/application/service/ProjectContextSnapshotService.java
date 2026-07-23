package com.company.scopery.modules.aiassistant.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Builds a compact project snapshot (phases, overdue tasks, milestones) for AI context.
 * Cached 3 minutes in Redis — slight staleness is acceptable for a summary block.
 */
@Service
public class ProjectContextSnapshotService {

    private static final Logger log = LoggerFactory.getLogger(ProjectContextSnapshotService.class);
    private static final String CACHE_KEY_PREFIX = "snapshot:v1:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(3);

    private static final String PHASE_SQL = """
            SELECT
                pp.display_order,
                pp.name               AS phase_name,
                pp.status             AS phase_status,
                pp.planned_start_date::text AS start_date,
                pp.planned_end_date::text   AS end_date,
                COUNT(pt.id) FILTER (WHERE pt.status = 'DONE')                           AS done_count,
                COUNT(pt.id) FILTER (WHERE pt.status NOT IN ('DONE','TODO','CANCELLED'))  AS in_progress_count,
                COUNT(pt.id) FILTER (WHERE pt.status = 'TODO')                           AS todo_count
            FROM project_project_phase pp
            LEFT JOIN project_task pt
                   ON pt.project_phase_id = pp.id AND pt.status != 'CANCELLED'
            WHERE pp.project_id = :projectId::uuid
            GROUP BY pp.id, pp.display_order, pp.name, pp.status,
                     pp.planned_start_date, pp.planned_end_date
            ORDER BY pp.display_order ASC
            LIMIT 20
            """;

    private static final String OVERDUE_TASK_SQL = """
            SELECT title, priority, due_date::text AS due_date
            FROM project_task
            WHERE project_id = :projectId::uuid
              AND due_date < CURRENT_DATE
              AND status NOT IN ('DONE', 'CANCELLED')
            ORDER BY due_date ASC
            LIMIT 5
            """;

    private static final String MILESTONE_SQL = """
            SELECT name, milestone_date::text AS milestone_date, status
            FROM project_milestone
            WHERE project_id = :projectId::uuid
              AND status IN ('PLANNED', 'MISSED')
            ORDER BY
                CASE status WHEN 'MISSED' THEN 0 ELSE 1 END,
                milestone_date ASC
            LIMIT 8
            """;

    private final NamedParameterJdbcTemplate namedJdbc;
    private final StringRedisTemplate stringRedisTemplate;

    public ProjectContextSnapshotService(NamedParameterJdbcTemplate namedJdbc,
                                          StringRedisTemplate stringRedisTemplate) {
        this.namedJdbc = namedJdbc;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String getSnapshot(UUID projectId) {
        if (projectId == null) return "";
        String cacheKey = CACHE_KEY_PREFIX + projectId;
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) return cached;
        } catch (Exception e) {
            log.warn("[ProjectSnapshot] Cache read failed: {}", e.getMessage());
        }
        String snapshot = buildSnapshot(projectId);
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, snapshot, CACHE_TTL);
        } catch (Exception e) {
            log.warn("[ProjectSnapshot] Cache write failed: {}", e.getMessage());
        }
        return snapshot;
    }

    private String buildSnapshot(UUID projectId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("projectId", projectId.toString());
        StringBuilder sb = new StringBuilder();

        try {
            List<Map<String, Object>> phases = namedJdbc.queryForList(PHASE_SQL, params);
            if (!phases.isEmpty()) {
                sb.append("\nPhases:");
                for (Map<String, Object> p : phases) {
                    sb.append("\n  ").append(p.get("display_order")).append(". ")
                      .append(p.get("phase_name")).append(" [").append(p.get("phase_status")).append("]");
                    String start = (String) p.get("start_date");
                    String end = (String) p.get("end_date");
                    if (start != null || end != null) {
                        sb.append(" ").append(start != null ? start : "?")
                          .append(" → ").append(end != null ? end : "?");
                    }
                    sb.append("\n     Tasks: ").append(toLong(p.get("done_count"))).append(" DONE, ")
                      .append(toLong(p.get("in_progress_count"))).append(" IN_PROGRESS, ")
                      .append(toLong(p.get("todo_count"))).append(" TODO");
                }
            }
        } catch (Exception e) {
            log.warn("[ProjectSnapshot] Phase query failed: {}", e.getMessage());
        }

        try {
            List<Map<String, Object>> overdue = namedJdbc.queryForList(OVERDUE_TASK_SQL, params);
            if (!overdue.isEmpty()) {
                sb.append("\n\nOverdue tasks:");
                for (Map<String, Object> t : overdue) {
                    sb.append("\n  - \"").append(t.get("title")).append("\"")
                      .append(" [").append(t.get("priority")).append("]")
                      .append(" due ").append(t.get("due_date"));
                }
            }
        } catch (Exception e) {
            log.warn("[ProjectSnapshot] Overdue task query failed: {}", e.getMessage());
        }

        try {
            List<Map<String, Object>> milestones = namedJdbc.queryForList(MILESTONE_SQL, params);
            if (!milestones.isEmpty()) {
                sb.append("\n\nMilestones:");
                for (Map<String, Object> ms : milestones) {
                    sb.append("\n  - \"").append(ms.get("name")).append("\"")
                      .append(" [").append(ms.get("status")).append("]")
                      .append(" ").append(ms.get("milestone_date"));
                }
            }
        } catch (Exception e) {
            log.warn("[ProjectSnapshot] Milestone query failed: {}", e.getMessage());
        }

        if (sb.isEmpty()) return "";
        return "<project_snapshot>" + sb + "\n</project_snapshot>";
    }

    private static long toLong(Object val) {
        if (val instanceof Number n) return n.longValue();
        return 0L;
    }
}
