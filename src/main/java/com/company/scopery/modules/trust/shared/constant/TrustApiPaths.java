package com.company.scopery.modules.trust.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class TrustApiPaths {
    private static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/trust";
    public static final String CLASSIFICATION_POLICY = WS + "/classification-policy";
    public static final String SENSITIVE_OBJECTS = WS + "/sensitive-objects";
    public static final String SENSITIVE_OBJECT_BY_ID = WS + "/sensitive-objects/{objectId}";
    public static final String SENSITIVE_FIELDS = WS + "/sensitive-fields";
    public static final String SENSITIVE_FIELD_BY_ID = WS + "/sensitive-fields/{fieldId}";
    public static final String SENSITIVE_ACCESS_LOGS = WS + "/sensitive-access-logs";
    public static final String EXPORT_AUDIT_LOGS = WS + "/export-audit-logs";
    public static final String DATA_SUBJECTS = WS + "/data-subjects";
    public static final String DATA_SUBJECTS_REBUILD = WS + "/data-subjects/rebuild-index";
    public static final String PRIVACY_REQUESTS = WS + "/privacy-requests";
    public static final String PRIVACY_REQUEST_BY_ID = WS + "/privacy-requests/{requestId}";
    public static final String PRIVACY_EXPORT_PACKAGES = WS + "/privacy-export-packages";
    public static final String ANONYMIZATION_PLANS = WS + "/anonymization-plans";
    public static final String RETENTION_POLICIES = WS + "/retention-policies";
    public static final String RETENTION_JOBS = WS + "/retention-jobs";
    public static final String RETENTION_POLICY_DRY_RUN = WS + "/retention-policies/{policyId}/dry-run";
    public static final String LEGAL_HOLDS = WS + "/legal-holds";
    public static final String LEGAL_HOLD_RELEASE = WS + "/legal-holds/{holdId}/release";
    public static final String CONSENT_RECORDS = WS + "/consent-records";
    public static final String CONTACT_SUPPRESSIONS = WS + "/contact-suppressions";
    public static final String ACCESS_REVIEW_CAMPAIGNS = WS + "/access-review-campaigns";
    public static final String PERMISSION_FINDINGS = WS + "/permission-review-findings";
    public static final String EVIDENCE_RECORDS = WS + "/evidence-records";
    public static final String DASHBOARD = WS + "/dashboard";
    private TrustApiPaths(){}
}
