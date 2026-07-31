package com.company.scopery.modules.quality.verificationcase.application.service;
import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.verificationcase.application.response.*;
import com.company.scopery.modules.quality.verificationcase.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class VerificationCaseQueryService {
    private final VerificationCaseRepository repo;
    private final QualityAuthorizationService authorization;
    public VerificationCaseQueryService(VerificationCaseRepository repo,
            QualityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public PageResponse<VerificationCaseListItemResponse> list(UUID projectId, UUID requirementId,
            String status, UUID assigneeId, String sort, int page, int size) {
        authorization.requireQualityView(projectId);
        String orderBy = resolveSort(sort);
        List<VerificationCaseListRow> rows = repo.searchList(projectId, requirementId, status, assigneeId, orderBy, size, (long) page * size);
        long total = repo.countSearch(projectId, requirementId, status, assigneeId);
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) total / size);
        var items = rows.stream().map(this::toListItem).toList();
        return new PageResponse<>(items, page, size, total, totalPages, page == 0, (long)(page+1)*size >= total);
    }

    @Transactional(readOnly = true)
    public VerificationCaseResponse get(UUID projectId, UUID id) {
        authorization.requireQualityView(projectId);
        return repo.findByIdAndProjectId(id, projectId)
                .map(VerificationCaseResponse::from)
                .orElseThrow(() -> QualityExceptions.verificationCaseNotFound(id));
    }

    private String resolveSort(String sort) {
        if (sort == null || sort.isBlank()) return "vc.updated_at DESC";
        String[] parts = sort.split(",");
        String field = switch (parts[0].trim()) {
            case "code" -> "vc.code";
            case "title" -> "vc.title";
            case "verificationMethod" -> "vc.verification_method";
            case "lifecycleStatus" -> "vc.lifecycle_status";
            case "createdAt" -> "vc.created_at";
            default -> "vc.updated_at";
        };
        String dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc") ? "ASC" : "DESC";
        return field + " " + dir;
    }

    private VerificationCaseListItemResponse toListItem(VerificationCaseListRow row) {
        VerificationCaseListItemResponse.AssigneeRef assignee = row.assigneeId() != null
                ? new VerificationCaseListItemResponse.AssigneeRef(row.assigneeId(), row.assigneeDisplayName()) : null;
        return new VerificationCaseListItemResponse(row.id(), row.projectId(), row.requirementId(),
                row.code(), row.title(), row.verificationMethod(), row.lifecycleStatus(), row.automationStatus(),
                assignee, row.version(), row.createdAt(), row.updatedAt());
    }
}
