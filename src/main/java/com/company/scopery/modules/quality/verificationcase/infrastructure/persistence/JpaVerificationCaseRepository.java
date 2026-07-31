package com.company.scopery.modules.quality.verificationcase.infrastructure.persistence;
import com.company.scopery.modules.quality.verificationcase.domain.model.*;
import com.company.scopery.modules.quality.verificationcase.infrastructure.mapper.VerificationCasePersistenceMapper;
import jakarta.persistence.EntityManager; import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp; import java.time.Instant;
import java.util.*; import java.util.LinkedHashMap;
@Repository
public class JpaVerificationCaseRepository implements VerificationCaseRepository {
    private final SpringDataVerificationCaseJpaRepository springData;
    private final VerificationCasePersistenceMapper mapper;
    @PersistenceContext private EntityManager em;

    public JpaVerificationCaseRepository(SpringDataVerificationCaseJpaRepository springData,
            VerificationCasePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }

    @Override public VerificationCase save(VerificationCase e) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));
    }

    @Override public Optional<VerificationCase> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<VerificationCaseListRow> searchList(UUID projectId, UUID requirementId, String status,
            UUID assigneeId, String orderBy, int limit, long offset) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, requirementId, status, assigneeId);
        String sql = """
                SELECT vc.id, vc.project_id, vc.requirement_id, vc.code, vc.title,
                       vc.verification_method, vc.lifecycle_status, vc.automation_status,
                       vc.assignee_id, u.full_name AS assignee_display_name,
                       vc.version, vc.created_at, vc.updated_at
                FROM quality_verification_case vc
                LEFT JOIN iam_user u ON u.id = vc.assignee_id
                """ + where + "\n" + "ORDER BY\s" + orderBy + " LIMIT :limit OFFSET :offset";
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        nq.setParameter("limit", limit);
        nq.setParameter("offset", offset);
        @SuppressWarnings("unchecked") List<Object[]> rows = nq.getResultList();
        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public long countSearch(UUID projectId, UUID requirementId, String status, UUID assigneeId) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, requirementId, status, assigneeId);
        String sql = "SELECT COUNT(*) FROM quality_verification_case vc " + where;
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        return ((Number) nq.getSingleResult()).longValue();
    }

    private StringBuilder buildWhere(Map<String, Object> params, UUID projectId,
            UUID requirementId, String status, UUID assigneeId) {
        var where = new StringBuilder("WHERE vc.project_id = :projectId");
        params.put("projectId", projectId);
        if (requirementId != null) { where.append(" AND vc.requirement_id = :requirementId"); params.put("requirementId", requirementId); }
        if (status != null && !status.isBlank()) { where.append(" AND vc.lifecycle_status = :status"); params.put("status", status.toUpperCase()); }
        if (assigneeId != null) { where.append(" AND vc.assignee_id = :assigneeId"); params.put("assigneeId", assigneeId); }
        return where;
    }

    private VerificationCaseListRow mapRow(Object[] row) {
        int i = 0;
        UUID id = uuid(row[i++]); UUID projectId = uuid(row[i++]); UUID requirementId = uuid(row[i++]);
        String code = str(row[i++]); String title = str(row[i++]);
        String verificationMethod = str(row[i++]); String lifecycleStatus = str(row[i++]); String automationStatus = str(row[i++]);
        UUID assigneeId = uuid(row[i++]); String assigneeDisplayName = str(row[i++]);
        int version = (int) num(row[i++]);
        Instant createdAt = instant(row[i++]); Instant updatedAt = instant(row[i]);
        return new VerificationCaseListRow(id, projectId, requirementId, code, title,
                verificationMethod, lifecycleStatus, automationStatus,
                assigneeId, assigneeDisplayName, version, createdAt, updatedAt);
    }

    private UUID uuid(Object v) { return v == null ? null : UUID.fromString(v.toString()); }
    private String str(Object v) { return v == null ? null : v.toString(); }
    private long num(Object v) { if (v == null) return 0L; if (v instanceof Number n) return n.longValue(); return Long.parseLong(v.toString()); }
    private Instant instant(Object v) { if (v == null) return null; if (v instanceof Timestamp ts) return ts.toInstant(); return Instant.parse(v.toString()); }
}
