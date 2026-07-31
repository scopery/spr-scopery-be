package com.company.scopery.modules.traceability.requirement.application.service;

import com.company.scopery.modules.traceability.requirement.application.response.LinkableFunctionResponse;
import com.company.scopery.modules.traceability.requirement.application.response.LinkableTestCaseResponse;
import com.company.scopery.modules.traceability.requirement.application.response.LinkableUseCaseResponse;
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

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<LinkableFunctionResponse> linkableFunctions(UUID projectId, UUID requirementId, String q, int limit) {
        authorization.requireView(projectId);
        String sql = """
                SELECT fi.id, fi.code, fi.title, fi.type, fi.status
                FROM app_functional_item fi
                WHERE fi.project_id = :projectId
                  AND fi.status <> 'ARCHIVED'
                  AND NOT EXISTS (
                      SELECT 1 FROM app_requirement_function rf
                      WHERE rf.requirement_id = :requirementId
                        AND rf.function_id = fi.id
                  )
                """ + (q != null && !q.isBlank()
                ? " AND (LOWER(fi.code) LIKE :q OR LOWER(fi.title) LIKE :q)" : "") + """
                ORDER BY fi.code ASC
                LIMIT :limit
                """;
        var nq = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("requirementId", requirementId)
                .setParameter("limit", limit <= 0 ? 20 : limit);
        if (q != null && !q.isBlank()) nq.setParameter("q", "%" + q.trim().toLowerCase() + "%");
        List<Object[]> rows = nq.getResultList();
        return rows.stream()
                .map(r -> new LinkableFunctionResponse(toUUID(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4])))
                .toList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<LinkableUseCaseResponse> linkableUseCases(UUID projectId, UUID requirementId, String q, int limit) {
        authorization.requireView(projectId);
        String sql = """
                SELECT uc.id, uc.key, uc.name, uc.status, uc.completeness_status
                FROM app_use_case uc
                WHERE uc.project_id = :projectId
                  AND uc.status <> 'ARCHIVED'
                  AND NOT EXISTS (
                      SELECT 1 FROM app_requirement_use_case ruc
                      WHERE ruc.requirement_id = :requirementId
                        AND ruc.use_case_id = uc.id
                  )
                """ + (q != null && !q.isBlank()
                ? " AND (LOWER(uc.key) LIKE :q OR LOWER(uc.name) LIKE :q)" : "") + """
                ORDER BY uc.key ASC
                LIMIT :limit
                """;
        var nq = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("requirementId", requirementId)
                .setParameter("limit", limit <= 0 ? 20 : limit);
        if (q != null && !q.isBlank()) nq.setParameter("q", "%" + q.trim().toLowerCase() + "%");
        List<Object[]> rows = nq.getResultList();
        return rows.stream()
                .map(r -> new LinkableUseCaseResponse(toUUID(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4])))
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
