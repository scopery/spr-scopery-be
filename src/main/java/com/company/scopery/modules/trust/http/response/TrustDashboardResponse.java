package com.company.scopery.modules.trust.http.response;
public record TrustDashboardResponse(long openPrivacyRequests, long retentionPolicies,
        long accessReviewCampaigns, long exportAuditLogs, String note) {}
