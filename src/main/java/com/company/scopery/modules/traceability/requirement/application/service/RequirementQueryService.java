package com.company.scopery.modules.traceability.requirement.application.service;

import com.company.scopery.modules.traceability.requirement.application.response.LinkableTestCaseResponse;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RequirementQueryService {

    private final RequirementRepository repo;
    private final TraceabilityAuthorizationService authorization;

    @PersistenceContext
    private EntityManager em;

    public RequirementQueryService(RequirementRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RequirementResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(RequirementResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RequirementResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId)
                .map(RequirementResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.requirementNotFound(id));
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<LinkableTestCaseResponse> linkableTestCases(UUID projectId, UUID requirementId, String q, int limit) {
        authorization.requireView(projectId);
        String sql = """
                SELECT tc.id, tc.code, tc.title, tc.status
                FROM quality_test_case tc
                WHERE tc.project_id = :projectId
                  AND tc.status NOT IN ('ARCHIVED','DEPRECATED')
                  AND NOT EXISTS (
                      SELECT 1 FROM traceability_link tl
                      WHERE tl.source_id   = :requirementId
                        AND tl.target_id   = tc.id
                        AND tl.source_type = 'REQUIREMENT'
                        AND tl.target_type = 'TEST_CASE'
                        AND tl.link_type   = 'TESTED_BY'
                        AND tl.status      = 'ACTIVE'
                        AND tl.project_id  = :projectId
                  )
                """ + (q != null && !q.isBlank()
                ? " AND (LOWER(tc.code) LIKE :q OR LOWER(tc.title) LIKE :q)" : "") + """
                ORDER BY tc.code ASC
                LIMIT :limit
                """;

        var nq = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("requirementId", requirementId)
                .setParameter("limit", limit <= 0 ? 20 : limit);
        if (q != null && !q.isBlank()) nq.setParameter("q", "%" + q.trim().toLowerCase() + "%");

        List<Object[]> rows = nq.getResultList();
        return rows.stream()
                .map(r -> new LinkableTestCaseResponse(
                        toUUID(r[0]), str(r[1]), str(r[2]), str(r[3])))
                .toList();
    }

    private static UUID toUUID(Object v) {
        if (v == null) return null;
        if (v instanceof UUID u) return u;
        return UUID.fromString(v.toString());
    }

    private static String str(Object v) {
        return v == null ? null : v.toString();
    }
}
