package com.company.scopery.modules.trust.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.accessreview.application.service.AccessReviewQueryService;
import com.company.scopery.modules.trust.exportaudit.application.service.ExportAuditLogQueryService;
import com.company.scopery.modules.trust.http.response.TrustDashboardResponse;
import com.company.scopery.modules.trust.privacy.application.service.PrivacyRequestQueryService;
import com.company.scopery.modules.trust.retention.application.service.RetentionQueryService;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class TrustDashboardController {
    private final PrivacyRequestQueryService privacyQuery;
    private final RetentionQueryService retentionQuery;
    private final AccessReviewQueryService accessReviewQuery;
    private final ExportAuditLogQueryService exportAuditQuery;
    private final TrustAuthorizationService auth;
    public TrustDashboardController(PrivacyRequestQueryService privacyQuery, RetentionQueryService retentionQuery,
            AccessReviewQueryService accessReviewQuery, ExportAuditLogQueryService exportAuditQuery,
            TrustAuthorizationService auth) {
        this.privacyQuery = privacyQuery; this.retentionQuery = retentionQuery;
        this.accessReviewQuery = accessReviewQuery; this.exportAuditQuery = exportAuditQuery;
        this.auth = auth;
    }
    @GetMapping(TrustApiPaths.DASHBOARD) @Operation(summary = "Trust dashboard metrics")
    public ApiResponse<TrustDashboardResponse> dashboard(@PathVariable UUID workspaceId) {
        auth.requireView(workspaceId);
        long openPrivacy = privacyQuery.listByWorkspace(workspaceId).stream()
                .filter(p -> !"COMPLETED".equals(p.status()) && !"REJECTED".equals(p.status()) && !"CANCELLED".equals(p.status())).count();
        long retentionCount = retentionQuery.listPolicies(workspaceId).size();
        long campaignCount = accessReviewQuery.listCampaigns(workspaceId).size();
        long exportCount = exportAuditQuery.listByWorkspace(workspaceId).size();
        return ApiResponse.success(new TrustDashboardResponse(openPrivacy, retentionCount, campaignCount, exportCount,
                "Compliance readiness controls; not a certification claim"));
    }
}
